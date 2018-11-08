package com.centit.framework.operationlog;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ParamName;
import com.centit.support.compiler.Pretreatment;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class RecordOperationLogAspect {

    /**
     * 注册 注入点
     */
    @Pointcut("@annotation(com.centit.framework.operationlog.RecordOperationLog)")
    public void logAspect(){}

    /**
     * controller 方法执行开始前 记录当前时间，暂时不知道改怎么记录时间，
     * 所有只能放到request属性中
     * @param joinPoint 切入点
     * @param operationLog RecordOperationLog 注解信息
     */
    @Before("logAspect() && @annotation(operationLog)")
    public  void doBefore(JoinPoint joinPoint, RecordOperationLog operationLog) {
        if(operationLog.timing()){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            request.setAttribute("_before_method_run", System.currentTimeMillis());
        }
    }

    /**
     * 获取切入点中的参数信息，目前不能获取到参数名称，按道理java8可以通过反射获取方法参数名称的但是获得只是arg0.1.2...
     * @param joinPoint 切入点
     * @return 返回 map 中包括参数名和参数值
     */
    public static Map<String, Object> getMethodDescription(JoinPoint joinPoint){
        Map<String, Object> map = new HashMap<>(10);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] arguments = joinPoint.getArgs();
        int nps = parameters.length;
        int nas = arguments.length;
        for(int i=0; i<nps && i<nas; i++){
            if(arguments[i] instanceof ServletRequest)
                continue;
            if(arguments[i] instanceof ServletResponse)
                continue;
            String paramName = parameters[i].getName();
            if(parameters[i].isAnnotationPresent(ParamName.class)){
                ParamName param =  parameters[i].getAnnotation(ParamName.class);
                paramName = param.value();
            }            
            map.put(paramName,arguments[i]);
        }
        return map;
    }

    /**
     * 记录日志
     * @param joinPoint joinPoint 切入点
     * @param operationLog 注解
     * @param e 如果为null没有异常说明执行成功，否在记录异常信息
     */
    private static void writeOperationLog(JoinPoint joinPoint, RecordOperationLog operationLog, Throwable e ){
        Map<String, Object> map = getMethodDescription(joinPoint);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, Object> params = BaseController.collectRequestParameters(request);
        String newValue;
        if(operationLog.appendRequest()){
            map.putAll(params);
            newValue = JSON.toJSONString(map);
        }else{
            newValue = JSON.toJSONString(map);
            map.putAll(params);
        }
        CentitUserDetails userDetails = WebOptUtils.getLoginUser(request);
        IUserInfo userInfo = userDetails.getUserInfo();
        //map.put("userDetails", userDetails);
        map.put("loginUser", userInfo);
        map.put("loginIp", request.getRemoteHost()+":"+WebOptUtils.getRequestAddr(request));
        String optContent = Pretreatment.mapTemplateString(operationLog.content(),map);

        Object targetController = joinPoint.getTarget();
        String optId = StringBaseOpt.objectToString(
                ReflectionOpt.getFieldValue(targetController,"optId"));
        String logLevel = OperationLog.LEVEL_INFO;
        if(e != null){
            logLevel = OperationLog.LEVEL_ERROR;
            optContent += " 执行报错：" + e.getLocalizedMessage();
        }
        if(operationLog.timing()){
            Long beforeRun = (Long)request.getAttribute("_before_method_run");
            optContent += " 耗时：" + (System.currentTimeMillis() - beforeRun);
        }
        OperationLogCenter.log(logLevel, userInfo==null?"anonymous":userInfo.getUserCode(),
                optId, null ,joinPoint.getSignature().getName(),
                optContent, newValue, null);
    }

    /**
     * 执行错误时记录错误日志
     * @param joinPoint joinPoint 切入点
     * @param operationLog  RecordOperationLog 注解
     * @param e 如果为null没有异常说明执行成功，否在记录异常信息
     */
    @AfterThrowing(pointcut = "logAspect() && @annotation(operationLog)", throwing = "e")
    public  void doAfterThrowing(JoinPoint joinPoint, RecordOperationLog operationLog, Throwable e) {
        writeOperationLog(joinPoint, operationLog, e);

    }

    /**
     * 正常完成时记录日志
     * @param joinPoint joinPoint 切入点
     * @param operationLog  RecordOperationLog 注解
     */
    @AfterReturning(pointcut = "logAspect() && @annotation(operationLog)")
    public  void doAfterReturning(JoinPoint joinPoint, RecordOperationLog operationLog) {
        writeOperationLog(joinPoint, operationLog, null);
    }
}
