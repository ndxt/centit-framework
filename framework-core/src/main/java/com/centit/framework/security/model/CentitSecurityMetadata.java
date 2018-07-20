package com.centit.framework.security.model;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.framework.model.basedata.IRolePower;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.AbstractCachedObject;
import com.centit.support.common.CachedObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CentitSecurityMetadata {
    public static final String ROLE_PREFIX = "R_";
    public static boolean isForbiddenWhenAssigned = false;

    public static final CachedObject<OptTreeNode > optTreeNodeCache =
        new CachedObject<>( CentitSecurityMetadata ::reloadOptTreeNode,
            new AbstractCachedObject<?>[]{CodeRepositoryCache.codeToOptMap,
                CodeRepositoryCache.optMethodRepo, CodeRepositoryCache.rolePowerRepo});

    public static final CachedObject<Map<String,List<ConfigAttribute>>>
        optMethodRoleMapCache = new CachedObject<>( CentitSecurityMetadata ::reloadOptMethodRoleMap,
            new AbstractCachedObject<?>[]{CodeRepositoryCache.codeToOptMap,
                CodeRepositoryCache.optMethodRepo, CodeRepositoryCache.rolePowerRepo});

    private static Pair<OptTreeNode, Map<String ,List<ConfigAttribute >>> reloadSecurityMetadata(){
        OptTreeNode optTreeNode = new OptTreeNode();
        Map<String, ? extends IOptInfo> codeToOptInfoMap = CodeRepositoryCache.codeToOptMap.getCachedTarget();
        for(IOptMethod ou : CodeRepositoryCache.optMethodRepo.getCachedTarget()){
            IOptInfo oi = codeToOptInfoMap.get(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = StringBaseOpt.concat(oi.getOptUrl(),ou.getOptUrl());
                List<List<String>> sOpt = CentitSecurityMetadata.parsePowerDefineUrl(
                    optDefUrl,ou.getOptReq());

                for(List<String> surls : sOpt){
                    OptTreeNode opt = optTreeNode;
                    for(String surl : surls)
                        opt = opt.setChildPath(surl);
                    opt.setOptCode(ou.getOptCode());
                }
            }
        }

        Map<String,List<ConfigAttribute>> optMethodRoleMap = new HashMap<>(100);
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

        CentitSecurityMetadata.confirmLoginCasMustBeAuthed(optTreeNode,optMethodRoleMap);

        //将操作和角色对应关系中的角色排序，便于权限判断中的比较
        for(Map.Entry<String ,List<ConfigAttribute >> roleMap : optMethodRoleMap.entrySet()){
            //排序便于后面比较
            roleMap.getValue().sort(
                Comparator.comparing(ConfigAttribute::getAttribute));
        }
        //测试比较排序效果
        return new ImmutablePair<>( optTreeNode , optMethodRoleMap);
    }

    /**
     * 这两个缓存式 耦合的 需要同时刷新
     * @return 同时刷新缓存
     */
    private static OptTreeNode reloadOptTreeNode(){
        Pair<OptTreeNode, Map<String ,List<ConfigAttribute >>>  securityData =  reloadSecurityMetadata();
        optMethodRoleMapCache.setFreshtDate(securityData.getRight());
        return securityData.getLeft();
    }

    private static Map<String,List<ConfigAttribute>>  reloadOptMethodRoleMap(){
        Pair<OptTreeNode, Map<String ,List<ConfigAttribute >>>  securityData =  reloadSecurityMetadata();
        optTreeNodeCache.setFreshtDate(securityData.getLeft());
        return securityData.getRight();
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

    private static String matchUrlToOpt(OptTreeNode curOpt , String sUrl, String httpMethod){
        if(curOpt==null){
            return null;
        }
        List<String> urls = parseRequestUrl(sUrl,httpMethod);
        for(String s: urls){
            if(curOpt.childList == null) {
                return curOpt.optCode;
            }
            OptTreeNode subOpt = curOpt.childList.get(s);
            if(subOpt == null){
                subOpt = curOpt.childList.get("*");
                if(subOpt == null) {
                    return curOpt.optCode;
                }
            }
            curOpt = subOpt;
        }
        if(curOpt!=null) {
            return curOpt.optCode;
        }
        return null;
    }


    public static String matchUrlToOpt(String sUrl, String httpMethod){
        return matchUrlToOpt(optTreeNodeCache.getCachedTarget(), sUrl, httpMethod);
    }
    //public abstract void loadRoleSecurityMetadata();
    public static String matchUrlToOpt(String sUrl,HttpServletRequest request){
        return matchUrlToOpt(sUrl, request.getMethod());
    }

    public static Collection<ConfigAttribute> matchUrlToRole(String sUrl,HttpServletRequest request){

        String sOptCode = matchUrlToOpt(sUrl,request);
        if(sOptCode==null) {
            return null;
        }
        List<ConfigAttribute> defaultRole = new ArrayList<>(2);
        defaultRole.add(new SecurityConfig(SecurityContextUtils.FORBIDDEN_ROLE_CODE));
        Collection<ConfigAttribute> roles = optMethodRoleMapCache.getCachedTarget().get(sOptCode);
        if(roles == null && isForbiddenWhenAssigned){
            return defaultRole;
        }
       return roles;
    }

    public static void printOptdefRoleMap(){
        for(Map.Entry<String ,List<ConfigAttribute >> roleMap :  optMethodRoleMapCache.getCachedTarget().entrySet()){
            if(roleMap.getValue().size()>1){
                System.out.print(roleMap.getKey());
                System.out.print(" : ");
                for(ConfigAttribute c : roleMap.getValue()){
                    System.out.print(c.getAttribute());
                    System.out.print("  ");
                }
                System.out.println();
            }
        }
        System.out.println("--------------------------------");
    }



    private static void confirmLoginCasMustBeAuthed(OptTreeNode optTreeNode,
                                                   Map<String ,List<ConfigAttribute >> optMethodRoleMap){
        /**
         * 添加 logincas 的角色定义
         */
        String loginCasOptCode = matchUrlToOpt(optTreeNode,"/system/mainframe/logincas","GET");
        if(StringUtils.isBlank(loginCasOptCode)){
            loginCasOptCode = "logincas";
            List<List<String>> sOpt = parsePowerDefineUrl(
                    "/system/mainframe/logincas","R");

            for(List<String> surls : sOpt){
                OptTreeNode opt = optTreeNode;
                for(String surl : surls) {
                    opt = opt.setChildPath(surl);
                }
                opt.setOptCode(loginCasOptCode);
            }
        }

        List<ConfigAttribute/*roleCode*/> roles = optMethodRoleMap.get(loginCasOptCode);
        if(CollectionUtils.isEmpty(roles)){
            if(roles == null){
                roles = new ArrayList</*roleCode*/>(2);
            }
            roles.add(new SecurityConfig(ROLE_PREFIX + SecurityContextUtils.PUBLIC_ROLE_CODE));
            optMethodRoleMap.put(loginCasOptCode, roles);
        }
    }
}
