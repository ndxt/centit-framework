package com.centit.framework.operationlog;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
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

	@Pointcut("@annotation(com.centit.framework.operationlog.RecordOperationLog)")
	public void logAspect(){}

	@Before("logAspect() && @annotation(operationLog)")
	public  void doBefore(JoinPoint joinPoint, RecordOperationLog operationLog) {
		if(operationLog.timing()){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			request.setAttribute("_before_method_run", System.currentTimeMillis());
		}
	}

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
			map.put(parameters[i].getName(),arguments[i]);
		}
		return map;
	}

	private static void writeOperationLog(JoinPoint joinPoint, RecordOperationLog operationLog, Throwable e ){
		Map<String, Object> map = getMethodDescription(joinPoint);
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		CentitUserDetails userInfo = WebOptUtils.getLoginUser(request);
		Map<String, Object> params = BaseController.collectRequestParameters(request);
		String newValue;
		if(operationLog.appendRequest()){
			map.putAll(params);
			newValue = JSON.toJSONString(map);
		}else{
			newValue = JSON.toJSONString(map);
			map.putAll(params);
		}
		map.put("userInfo",userInfo);
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

	@AfterThrowing(pointcut = "logAspect() && @annotation(operationLog)", throwing = "e")
	public  void doAfterThrowing(JoinPoint joinPoint, RecordOperationLog operationLog, Throwable e) {
		writeOperationLog(joinPoint, operationLog, e);

	}

	@AfterReturning(pointcut = "logAspect() && @annotation(operationLog)")
	public  void doAfterReturning(JoinPoint joinPoint, RecordOperationLog operationLog) {
		writeOperationLog(joinPoint, operationLog, null);
	}
}