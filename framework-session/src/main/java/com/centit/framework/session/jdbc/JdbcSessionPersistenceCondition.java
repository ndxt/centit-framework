package com.centit.framework.session.jdbc;

import com.centit.framework.common.SysParametersUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by zou_wy on 2017/6/15.
 */
public class JdbcSessionPersistenceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String type = SysParametersUtils.getStringValue("session.persistence.db.type");
        if(type == null) {
            return true;
        }
        return StringUtils.equals("jdbc",type);
    }
}
