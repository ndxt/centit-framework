package com.centit.framework.security;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.TopUnitSecurityMetadata;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

//@Component("centitSecurityMetadataSource")
public class DaoInvocationSecurityMetadataSource
            implements FilterInvocationSecurityMetadataSource
         {
    //private static final Logger logger = LoggerFactory.getLogger(DaoInvocationSecurityMetadataSource.class);
    //private static boolean logDebug = logger.isDebugEnabled();
    @Override
    public boolean supports(Class<?> clazz) {
        if (FilterInvocation.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    // According to a URL, Find out permission configuration of this URL.
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // guess object is a URL.
        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("对不起,目标对象不是类型");
        }
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getHttpRequest();
        String requestUrl = fi.getRequestUrl();
        /* if (logDebug) {
            logger.debug("通过权限过滤器 请求url = " + requestUrl + " 请求类型 = " + request.getMethod());
        }*/
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        TopUnitSecurityMetadata metadata = CentitSecurityMetadata.securityMetadata.getCachedValue(topUnit);
        return metadata==null? null : metadata.matchUrlToRole(requestUrl, request);
/*        Collection<ConfigAttribute> needRoles = CentitSecurityMetadata.matchUrlToRole(requestUrl,request);
        if(needRoles==null && requestUrl.contains("/mainframe/logincas")){
            needRoles = new ArrayList<>(1);
            needRoles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + SecurityContextUtils.PUBLIC_ROLE_CODE));
        }
        return needRoles;*/
    }
}
