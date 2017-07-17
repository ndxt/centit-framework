package com.centit.framework.config;

import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * Created by zou_wy on 2017/6/15.
 */
public class RedisCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment evn = context.getEnvironment();
        String enable = evn.getProperty("session.persistence.enable");
        String type = evn.getProperty("session.persistence.db.type");
        if(enable == null || type == null) {
            return false;
        }
        return StringRegularOpt.isTrue(enable) && Objects.equals("redis",type);
    }
}
