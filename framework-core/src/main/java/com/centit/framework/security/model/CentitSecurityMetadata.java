package com.centit.framework.security.model;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.framework.model.basedata.IRolePower;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.AbstractCachedObject;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CentitSecurityMetadata {
    public static final String ROLE_PREFIX = "R_";
    public static boolean isForbiddenWhenAssigned = false;

    public static final CachedObject<OptTreeNode> optTreeNodeCache =
        new CachedObject<>( CentitSecurityMetadata ::reloadOptTreeNode,
            new AbstractCachedObject<?>[]{CodeRepositoryCache.codeToOptMap,
                CodeRepositoryCache.optMethodRepo, CodeRepositoryCache.rolePowerRepo});

    private static OptTreeNode reloadOptTreeNode(){
        Map<String, List<ConfigAttribute>> optMethodRoleMap = new HashMap<>(100);
        List<? extends IRolePower> rolepowers =  CodeRepositoryCache.rolePowerRepo.getCachedTarget();
        if(rolepowers==null || rolepowers.size()==0)
            return null;
        for(IRolePower rp: rolepowers){
            List<ConfigAttribute/*roleCode*/> roles = optMethodRoleMap.get(rp.getOptCode());
            if(roles == null){
                roles = new ArrayList</*roleCode*/>();
            }
            roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + StringUtils.trim(rp.getRoleCode())));
            optMethodRoleMap.put(rp.getOptCode(), roles);
        }

        OptTreeNode optTreeNode = new OptTreeNode();
        Map<String, ? extends IOptInfo> codeToOptInfoMap = CodeRepositoryCache.codeToOptMap.getCachedTarget();

        for(IOptMethod ou : CodeRepositoryCache.optMethodRepo.getCachedTarget()){
            IOptInfo oi = codeToOptInfoMap.get(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = StringBaseOpt.concat(oi.getOptUrl(),ou.getOptUrl());
                if(StringUtils.isNotBlank(optDefUrl) && StringUtils.isNotBlank(ou.getOptReq())) {
                    List<List<String>> sOpt = CentitSecurityMetadata.parsePowerDefineUrl(
                        optDefUrl, ou.getOptReq());

                    for (List<String> surls : sOpt) {
                        OptTreeNode opt = optTreeNode;
                        for (String surl : surls)
                            opt = opt.setChildPath(surl);
                        List<ConfigAttribute/*roleCode*/> roles = optMethodRoleMap.get(ou.getOptCode());
                        opt.addRoleList(roles);
                    }
                }
            }
        }

        CentitSecurityMetadata.confirmLoginCasMustBeAuthed(optTreeNode);
        return optTreeNode;
    }

    /**
     * @param isForbiddenWhenAssigned 设置为true时，将url分配到菜单后 该url需要授权才能访问；
     * 设置为false时，将url分配到菜单后不会对该url进行拦截，只有将该url分配给某个角色，其他角色才会被拦截
     */
    public static void setIsForbiddenWhenAssigned(boolean isForbiddenWhenAssigned){
        CentitSecurityMetadata.isForbiddenWhenAssigned = isForbiddenWhenAssigned;
    }

    public static List<String> parseRequestUrl(String sUrl, String httpMethod){
        List<String> swords = new ArrayList<>();
        String sFunUrl ;
        int p = sUrl.indexOf('?');
        if(p<1) {
            sFunUrl = sUrl;
        }else {
            sFunUrl = sUrl.substring(0, p);
        }

        swords.add(httpMethod);
        for(String s:sFunUrl.split("/")){
            if(!StringBaseOpt.isNvl(s) /*&& !"*".equals(s)*/){
                swords.add(s);
            }
        }
        return swords;
    }
    /**
     * C D R  U  :  POST DELETE GET PUT
     * @param sOptDefUrl sOptDefUrl
     * @param sMethod sMethod
     * @return List parseUrl
     */
    public static List<List<String>> parsePowerDefineUrl(String sOptDefUrl,String sMethod){

        String sUrls[] = (sOptDefUrl).split("/");
        List<String> sopts = new ArrayList<>();
        for(String s:sUrls){
            if(!StringUtils.isBlank(s)/* && !"*".equals(s)*/)
                sopts.add(s);
        }

        List<List<String>> swords = new ArrayList<>();
        /*if(StringUtils.isBlank(sMethod)){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("GET");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
            return swords;
        }*/
        if(sMethod.indexOf('C')>=0){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("POST");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if(sMethod.indexOf('D')>=0){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("DELETE");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if(sMethod.indexOf('R')>=0){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("GET");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if(sMethod.indexOf('U')>=0){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("PUT");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }

        return swords;
    }

    private static List<ConfigAttribute> matchUrlToRole(OptTreeNode curOpt, String sUrl, String httpMethod){
        if(curOpt==null){
            return null;
        }
        List<String> urls = parseRequestUrl(sUrl,httpMethod);
        for(String s: urls){
            if(curOpt.childList == null) {
                return curOpt.getRoleList();
            }
            OptTreeNode subOpt = curOpt.childList.get(s);
            if(subOpt == null){
                subOpt = curOpt.childList.get("*");
                if(subOpt == null) {
                    return curOpt.getRoleList();
                }
            }
            curOpt = subOpt;
        }
        if(curOpt!=null) {
            return curOpt.getRoleList();
        }
        return null;
    }

    public static List<ConfigAttribute> matchUrlToRole(String sUrl,HttpServletRequest request){

        List<ConfigAttribute> roles = matchUrlToRole(
            optTreeNodeCache.getCachedTarget(), sUrl, request.getMethod());
        if(roles == null && isForbiddenWhenAssigned){
            List<ConfigAttribute> defaultRole = new ArrayList<>(2);
            defaultRole.add(new SecurityConfig(SecurityContextUtils.FORBIDDEN_ROLE_CODE));
            return defaultRole;
        }
       return roles;
    }


    private static void confirmLoginCasMustBeAuthed(OptTreeNode optTreeNode){
        /**
         * 添加 logincas 的角色定义
         */
        List<ConfigAttribute> roles = matchUrlToRole(optTreeNode,"/system/mainframe/logincas","GET");
        if(roles == null || roles.size()==0){
            List<List<String>> sOpt = parsePowerDefineUrl(
                    "/system/mainframe/logincas","R");

            for(List<String> surls : sOpt){
                OptTreeNode opt = optTreeNode;
                for(String surl : surls) {
                    opt = opt.setChildPath(surl);
                }
                roles = new ArrayList</*roleCode*/>(2);
                roles.add(new SecurityConfig(ROLE_PREFIX + SecurityContextUtils.PUBLIC_ROLE_CODE));
                opt.addRoleList(roles);
            }
        }
    }
}
