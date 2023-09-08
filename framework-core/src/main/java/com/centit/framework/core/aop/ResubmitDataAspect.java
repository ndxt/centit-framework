package com.centit.framework.core.aop;

import com.centit.framework.common.WebOptUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * @ClassName RequestDataAspect
 * @Description 工作流重复提交校验
 **/
@Aspect
@Component
public class ResubmitDataAspect {
    protected Logger logger = LoggerFactory.getLogger(ResubmitDataAspect.class);

    private final static String DATA = "data";
    private final static Object PRESENT = new Object();

    @Around("@annotation(com.centit.framework.core.aop.NoRepeatCommit)")
    public Object handleResubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("spring AOP 提交校验submit 开始");
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息
        NoRepeatCommit annotation = method.getAnnotation(NoRepeatCommit.class);
        //通过 类名，方法名，当前用户，参数 来确定同一个提交；
        StringBuilder sb = new StringBuilder(method.getClass()
            .getName()).append(".").append(method.getName());

        HttpServletRequest request = ((ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes()).getRequest();
        sb.append(",user:").append(WebOptUtils.getCurrentUserCode(request));

        Object[] pointArgs = joinPoint.getArgs();
        for(Object arg: pointArgs){
            sb.append(",");
            if(arg instanceof ServletRequest ||
               arg instanceof ServletResponse ||
             arg instanceof HttpSession){
                sb.append(arg.getClass().getName());
            } else {
                sb.append(StringBaseOpt.objectToString(arg));
            }
        }
        String key = ResubmitLock.handleKey(sb.toString());
        //执行锁
        boolean lock = false;
        try {
            //设置解锁key
            //logger.info("spring AOP key: {}", key);
            lock = ResubmitLock.getInstance().lock(key, PRESENT);
            if (lock) {
                //logger.info("spring AOP 提交校验submit -> 放行");
                //放行
                return joinPoint.proceed();
            } else {
                logger.info("spring AOP 提交校验submit -> 重复提交");
                //响应重复提交异常
                throw new ObjectException("spring AOP 提交校验submit：重复提交:" + sb );
            }
        } finally {
            //设置解锁key和解锁时间
            ResubmitLock.getInstance().unLock(lock, key, annotation.delaySeconds());
            logger.info("spring AOP 提交校验submit -> 解锁");
        }
    }
}
