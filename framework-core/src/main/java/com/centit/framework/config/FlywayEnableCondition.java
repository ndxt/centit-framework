package com.centit.framework.config;

import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by zou_wy on 2017/6/21.
 */
public class FlywayEnableCondition implements Condition{
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment evn = context.getEnvironment();
        String flywayEnable = evn.getProperty("flyway.enable");
        if(flywayEnable == null){
            return false;
        }
        return StringRegularOpt.isTrue(flywayEnable);
    }
}
