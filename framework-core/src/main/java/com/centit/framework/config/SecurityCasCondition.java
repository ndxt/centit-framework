package com.centit.framework.config;

import com.centit.framework.common.SysParametersUtils;
import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by zou_wy on 2017/4/18.
 */
public class SecurityCasCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean isCas = StringRegularOpt.isTrue(SysParametersUtils.getStringValue("cas.sso"));
        return isCas;
   }
}