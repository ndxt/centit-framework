package com.centit.framework.security.model;

import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CentitSecurityMetadata {
    public static final String ROLE_PREFIX = "R_";
    public static boolean isForbiddenWhenAssigned = false;
    public static final OptTreeNode optTreeNode = new OptTreeNode();
    public static final Map<String/*optCode*/,List<ConfigAttribute/*roleCode*/>>
            optMethodRoleMap = new HashMap<>();

    /**
     * 设置为true时，将url分配到菜单后 该url需要授权才能访问；
     * 设置为false时，将url分配到菜单后不会对该url进行拦截，只有将该url分配给某个角色，其他角色才会被拦截
     * @param isForbiddenWhenAssigned
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

    public static String matchUrlToOpt(String sUrl, String httpMethod){
        List<String> urls = parseRequestUrl(sUrl,httpMethod);
        OptTreeNode curOpt = optTreeNode;
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
        if(optMethodRoleMap.get(sOptCode) == null && isForbiddenWhenAssigned){
            return defaultRole;
        }
       return optMethodRoleMap.get(sOptCode);
    }

    public static void printOptdefRoleMap(){
        for(Map.Entry<String ,List<ConfigAttribute >> roleMap : optMethodRoleMap.entrySet()){
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

    /**
     * 将操作和角色对应关系中的角色排序，便于权限判断中的比较
     */
    public static void sortOptMethodRoleMap(){
        //测试比较排序效果
        for(Map.Entry<String ,List<ConfigAttribute >> roleMap : optMethodRoleMap.entrySet()){
          //排序便于后面比较
            Collections.sort(roleMap.getValue(),
                    Comparator.comparing(ConfigAttribute::getAttribute));
        }
        //测试比较排序效果
    }

    public static void confirmLoginCasMustBeAuthed(){
        /**
         * 添加 logincas 的角色定义
         */
        String loginCasOptCode = matchUrlToOpt("/system/mainframe/logincas","GET");
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
