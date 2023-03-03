package com.centit.framework.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.Iterator;

public class DaoAccessDecisionManager implements AccessDecisionManager {
    private static final Logger logger = LoggerFactory.getLogger(DaoAccessDecisionManager.class);

    // In this method, need to compare authentication with configAttributes.
    // 1, A object is a URL, a filter was find permission configuration by this
    // URL, and pass to here.
    // 2, Check authentication has attribute in permission configuration
    // (configAttributes)
    // 3, If not match corresponding authentication, throw a
    // AccessDeniedException.
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {

        if(configAttributes.contains(new SecurityConfig(SecurityContextUtils.FORBIDDEN_ROLE_CODE))){
            String sErrMsg = "资源被禁止访问";
            logger.error(sErrMsg);
            throw new AccessDeniedException(sErrMsg);
        }

        //if(authentication!=null){
        Collection<? extends GrantedAuthority> userRoles = authentication.getAuthorities();
        if(userRoles!=null){
            Iterator<? extends GrantedAuthority> userRolesItr = userRoles.iterator();
            Iterator<ConfigAttribute> needRolesItr = configAttributes.iterator();
            /*for(ConfigAttribute ca : configAttributes) {
                if (ca == null) {
                    continue;
                }
                String needRole = ca.getAttribute();
                for (GrantedAuthority ga : authentication.getAuthorities()) {
                    if (needRole.equals(ga.getAuthority())) { // ga is user's role.
                        return;
                    }
                }
            }*/
            //将两个集合排序 是可以提高效率的， 但考虑到这两个集合都比较小（一般应该不会大于3）所以优化的意义不大
            String needRole = needRolesItr.next().getAttribute();
            String userRole = userRolesItr.next().getAuthority();
            while(true){
                int n = needRole.compareTo(userRole);
                if(n==0)
                    return; // 匹配成功 完成认证

                if(n<0){
                    if(!needRolesItr.hasNext())
                        break;
                    needRole = needRolesItr.next().getAttribute();
                }else{
                    if(!userRolesItr.hasNext())
                        break;
                    userRole = userRolesItr.next().getAuthority();
                }
            }
        }

        //没有权限，组织提示信息。
        FilterInvocation fi = (FilterInvocation) object;
        String requestUrl = fi.getRequestUrl();

        StringBuilder errorMsgBuilder = new StringBuilder("no auth: ").append(requestUrl)
            .append("; user role: ");
        boolean firstRole = true;
        for(GrantedAuthority ur : userRoles){
            if(firstRole){
                firstRole = false;
            } else {
                errorMsgBuilder.append(", ");
            }
            errorMsgBuilder.append(ur.getAuthority().substring(2));
        }
        errorMsgBuilder.append("; need role: ");
        firstRole = true;
        for(ConfigAttribute ca : configAttributes){
            if(firstRole){
                firstRole = false;
            } else {
                errorMsgBuilder.append(", ");
            }
            errorMsgBuilder.append(ca.getAttribute().substring(2));
        }
        errorMsgBuilder.append(".");
        String sErrMsg = errorMsgBuilder.toString();
        //fi.getRequest().setAttribute("CENTIT_SYSTEM_ERROR_MSG", sErrMsg);
        fi.getResponse().setHeader("CENTIT_SYSTEM_ERROR_MSG", sErrMsg);
        logger.error(sErrMsg);
        throw new AccessDeniedException(sErrMsg);
    }

    @Override
    public boolean supports(ConfigAttribute arg0) {
        return true;
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}
