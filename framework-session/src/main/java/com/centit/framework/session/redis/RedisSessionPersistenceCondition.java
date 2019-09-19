package com.centit.framework.session.redis;

import com.centit.framework.common.SysParametersUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * Created by zou_wy on 2017/6/15.
 */
public class RedisSessionPersistenceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String type = SysParametersUtils.getStringValue("session.persistence.db.type");
        if(type == null) {
            return false;
        }
        return Objects.equals("redis",type);
    }
}
