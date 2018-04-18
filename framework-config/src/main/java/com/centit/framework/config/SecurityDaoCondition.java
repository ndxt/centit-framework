package com.centit.framework.config;

import com.centit.framework.common.SysParametersUtils;
import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by zou_wy on 2017/4/18.
 */
public class SecurityDaoCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean isDao = StringRegularOpt.isTrue(SysParametersUtils.getStringValue("login.dao.enable"));
        return isDao;
    }
}
