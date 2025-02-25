package com.centit.framework.security;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.security.OptTreeNode;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class SystemSecurityMetadata {

    private final CachedObject<OptTreeNode> optTreeNodeCache;
    public static boolean requestInSpringCloud = false;

    public SystemSecurityMetadata() {
        this.optTreeNodeCache =
            new CachedObject<>(this::reloadOptTreeNode);
    }

    public void evictCache(){
        optTreeNodeCache.evictCache();
    }

    public static void setRequestInSpringCloud(boolean requestInSpringCloud) {
        SystemSecurityMetadata.requestInSpringCloud = requestInSpringCloud;
    }

    public List<ConfigAttribute> matchUrlToRole(String sUrl, HttpServletRequest request) {
        if (requestInSpringCloud) {
            int count = sUrl.split("/").length - 1;
            if (count > 1) {
                sUrl = sUrl.substring(StringUtils.ordinalIndexOf(sUrl, "/", 2));
            }
        }
        List<ConfigAttribute> roles = matchCompleteUrlToRole(
            optTreeNodeCache.getCachedTarget(), sUrl, request.getMethod());
        if (roles == null && CentitSecurityMetadata.isForbiddenWhenAssigned) {
            List<ConfigAttribute> defaultRole = new ArrayList<>(2);
            defaultRole.add(new SecurityConfig(SecurityContextUtils.FORBIDDEN_ROLE_CODE));
            return defaultRole;
        }
        return roles;
    }

    private OptTreeNode reloadOptTreeNode() {
        OptTreeNode optTreeNode = CodeRepositoryCache.getPlatformEnvironment().getSysOptTree();
        this.confirmLoginCasMustBeAuthed(optTreeNode);
        return optTreeNode;
    }

    private List<String> parseRequestUrl(String sUrl, String httpMethod) {
        List<String> swords = new ArrayList<>();
        String sFunUrl;
        int p = sUrl.indexOf('?');
        if (p < 1) {
            sFunUrl = sUrl;
        } else {
            sFunUrl = sUrl.substring(0, p);
        }

        int nPos = sFunUrl.lastIndexOf('.');
        if(nPos > 0) {
            int nPos2 = sFunUrl.lastIndexOf('/');
            if (nPos > nPos2) {
                sFunUrl = sFunUrl.substring(0, nPos);
            }
        }

        swords.add(httpMethod);
        for (String s : sFunUrl.split("/")) {
            if (StringUtils.isNotBlank(s)) {
                swords.add(s);
            }
        }
        return swords;
    }

    /**
     * 根据url路径和请求类型匹配出权限角色。
     * 匹配规则：
     * 当optTree中某一路径为/a/b时 可以匹配的路径包括 /a  /a/b /a/b/c
     *
     * @param curOpt     权限操作树
     * @param sUrl       需要被匹配的url路径
     * @param httpMethod 方法类型 GET POST PUT DELETE
     * @return null 代表未匹配到
     */
    private List<ConfigAttribute> matchUrlToRole(OptTreeNode curOpt, String sUrl, String httpMethod) {
        if (curOpt == null) {
            return null;
        }
        List<String> urls = parseRequestUrl(sUrl, httpMethod);
        for (String s : urls) {
            if (curOpt.childList == null) {
                return curOpt.getRoleList();
            }
            OptTreeNode subOpt = curOpt.childList.get(s);
            if (subOpt == null) {
                subOpt = curOpt.childList.get("*");
                if (subOpt == null) {
                    return curOpt.getRoleList();
                }
            }
            curOpt = subOpt;
        }
        return curOpt.getRoleList();
    }

    /**
     * 根据url路径和请求类型匹配出权限角色。
     * 匹配规则：
     * 当optTree中某一路径为/a/b时 只能匹配的路径包括 /a/b
     *
     * @param curOpt     权限操作树
     * @param sUrl       需要被匹配的url路径
     * @param httpMethod 方法类型 GET POST PUT DELETE
     * @return null 代表未匹配到或者匹配到后的角色列表为空
     */
    private List<ConfigAttribute> matchCompleteUrlToRole(OptTreeNode curOpt, String sUrl, String httpMethod) {
        if (curOpt == null) {
            return null;
        }
        List<String> urls = parseRequestUrl(sUrl, httpMethod);
        for (String url : urls) {
            if(curOpt.isLeafNode()){ // 前缀匹配 可以批量设置
                return curOpt.getRoleList();
            }
            OptTreeNode subOpt = curOpt.childList.get(url);
            if (subOpt == null) {
                subOpt = curOpt.childList.get("*");
            }
            if (subOpt == null) {
                return null;
            }
            curOpt = subOpt;
        }
        return curOpt.getRoleList();
    }

    private void confirmLoginCasMustBeAuthed(OptTreeNode optTreeNode) {
        /**
         * 添加 logincas 的角色定义
         */
        if (optTreeNode==null){
            return;
        }
        List<ConfigAttribute> roles = matchUrlToRole(optTreeNode, "/system/mainframe/logincas", "GET");
        if (roles == null || roles.size() == 0) {
            List<List<String>> sOpt = optTreeNode.parsePowerDefineUrl(
                "/system/mainframe/logincas", "R");

            for (List<String> surls : sOpt) {
                OptTreeNode opt = optTreeNode;
                for (String surl : surls) {
                    opt = opt.setChildPath(surl);
                }
                roles = new ArrayList<>(2);
                roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + SecurityContextUtils.PUBLIC_ROLE_CODE));
                opt.addRoleList(roles);
            }
        }
    }
}
