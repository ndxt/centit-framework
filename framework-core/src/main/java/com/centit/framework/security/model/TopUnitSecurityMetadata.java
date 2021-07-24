package com.centit.framework.security.model;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.framework.model.basedata.IRolePower;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.CollectionsOpt;
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

public class TopUnitSecurityMetadata {

    private String topUnit;
    private CachedObject<OptTreeNode> optTreeNodeCache;
    public static boolean requestInSpringCloud = false;

    public TopUnitSecurityMetadata(String topUnit){
        this.topUnit = topUnit;
        this.optTreeNodeCache =
            new CachedObject<>( this::reloadOptTreeNode,
                new AbstractCachedObject<?>[]{ CodeRepositoryCache.optInfoRepo,
                    CodeRepositoryCache.optMethodRepo, CodeRepositoryCache.rolePowerRepo});
    }

    public static void setRequestInSpringCloud(boolean requestInSpringCloud) {
        TopUnitSecurityMetadata.requestInSpringCloud = requestInSpringCloud;
    }

    private OptTreeNode reloadOptTreeNode(){
        Map<String, List<ConfigAttribute>> optMethodRoleMap = new HashMap<>(100);
        List<? extends IRolePower> rolepowers =
            CodeRepositoryCache.rolePowerRepo.getCachedValue(this.topUnit);
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
        List<? extends IOptInfo> optInfos = CodeRepositoryCache.optInfoRepo.getCachedValue(this.topUnit);
        Map<String, ? extends IOptInfo> optInfoMap = CollectionsOpt.createHashMap(optInfos, IOptInfo::getOptId);
        for(IOptMethod ou : CodeRepositoryCache.optMethodRepo
                .getCachedValue(this.topUnit).getListData()){
            IOptInfo oi = optInfoMap.get(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = StringBaseOpt.concat(oi.getOptUrl(),ou.getOptUrl());
                if(StringUtils.isNotBlank(optDefUrl) && StringUtils.isNotBlank(ou.getOptReq())) {
                    List<List<String>> sOpt = this.parsePowerDefineUrl(
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

        this.confirmLoginCasMustBeAuthed(optTreeNode);
        return optTreeNode;
    }

    public List<String> parseRequestUrl(String sUrl, String httpMethod){
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
            if(StringUtils.isNotBlank(s) /*&& !"*".equals(s)*/){
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
    public List<List<String>> parsePowerDefineUrl(String sOptDefUrl,String sMethod){

        String sUrls[] = (sOptDefUrl).split("/");
        List<String> sopts = new ArrayList<>();
        for(String s:sUrls){
            if(StringUtils.isNotBlank(s)/* && !"*".equals(s)*/)
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

    private List<ConfigAttribute> matchUrlToRole(OptTreeNode curOpt, String sUrl, String httpMethod){
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

    public List<ConfigAttribute> matchUrlToRole(String sUrl,HttpServletRequest request){
        if (requestInSpringCloud) {
            int count = sUrl.split("/").length - 1;
            if (count > 1) {
                sUrl = sUrl.substring(StringUtils.ordinalIndexOf(sUrl, "/", 2), sUrl.length());
            }
        }
        List<ConfigAttribute> roles = matchUrlToRole(
            optTreeNodeCache.getCachedTarget(), sUrl, request.getMethod());
        if(roles == null && CentitSecurityMetadata.isForbiddenWhenAssigned){
            List<ConfigAttribute> defaultRole = new ArrayList<>(2);
            defaultRole.add(new SecurityConfig(SecurityContextUtils.FORBIDDEN_ROLE_CODE));
            return defaultRole;
        }
       return roles;
    }


    private void confirmLoginCasMustBeAuthed(OptTreeNode optTreeNode){
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
                roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + SecurityContextUtils.PUBLIC_ROLE_CODE));
                opt.addRoleList(roles);
            }
        }
    }
}
