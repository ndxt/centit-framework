package com.centit.framework.staticsystem.config;

import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by zou_wy on 2017/6/15.
 */
public class StaticSystemConfigCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment evn = context.getEnvironment();
        String enable = evn.getProperty("sys.runas.staticsystem");
        if(enable == null) {
            return false;
        }
        return StringRegularOpt.isTrue(enable);
    }
}
