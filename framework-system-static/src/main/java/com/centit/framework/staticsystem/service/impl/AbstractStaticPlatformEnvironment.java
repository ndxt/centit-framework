package com.centit.framework.staticsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.*;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractStaticPlatformEnvironment
    implements PlatformEnvironment {

    public CachedObject<List<DataDictionary>> allDictionaryRepo =
        new CachedObject<>(this::listAllDataDictionary,
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    public CachedObject<List<UserRole>> allUserRoleRepo =
        new CachedObject<>(this::listAllUserRole,
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    public CachedObject<List<JsonCentitUserDetails>> allUserDetailsRepo =
        new CachedObject<>(()-> { this.reloadPlatformData(); return null;},
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    protected CentitPasswordEncoder passwordEncoder;

    public void setPasswordEncoder(CentitPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    protected abstract void reloadPlatformData();

    @SuppressWarnings("unchecked")
    protected void organizePlatformData() {
        for (IDataCatalog dd :  CodeRepositoryCache.catalogRepo.getCachedTarget()) {
            ((DataCatalog)dd).setDataDictionaries(
                (List<DataDictionary>) listDataDictionaries(dd.getCatalogCode()));
        }

        for (IRoleInfo ri : CodeRepositoryCache.roleInfoRepo.getCachedTarget()) {
            for (IRolePower rp : CodeRepositoryCache.rolePowerRepo.getCachedTarget()) {
                if (StringUtils.equals(rp.getRoleCode(), ri.getRoleCode())) {
                    ((RoleInfo)ri).addRolePowers((RolePower)rp);
                    /*OptMethod om = getOptMethod(rp.getOptCode());
                    if(om!=null)
                        userOptList.put(om.getOptId()+"-"+om.getOptMethod(), "T");*/
                }
            }
        }

        List<? extends IUserInfo> userinfos = CodeRepositoryCache.userInfoRepo.getCachedTarget();
        List<JsonCentitUserDetails> userDetails = new ArrayList<>(userinfos.size());
        List<? extends IUserUnit> uus = CodeRepositoryCache.userUnitRepo.getCachedTarget();

        for (IUserInfo ui : userinfos) {
            List<RoleInfo> roles = new ArrayList<>();
            Map<String, String> userOptList = new HashMap<>();
            for (UserRole ur : allUserRoleRepo.getCachedTarget()) {
                if (StringUtils.equals(ur.getUserCode(), ui.getUserCode())) {
                    IRoleInfo ri = CodeRepositoryCache.codeToRoleMap.getCachedTarget().get(ur.getRoleCode());
                    if (ri != null) {
                        roles.add((RoleInfo)ri);
                        for (IRolePower rp : ri.getRolePowers()) {
                            IOptMethod om = CodeRepositoryCache.codeToMethodMap.getCachedTarget().get(rp.getOptCode());
                            if (om != null && StringUtils.isNotBlank(om.getOptMethod())) {
                                //om.getOptCode()
                                userOptList.put(om.getOptId() + "-" + om.getOptMethod(), om.getOptCode());
                            }
                        }
                    }
                }
            }
            JsonCentitUserDetails ud = new JsonCentitUserDetails();
            ud.setUserInfo((JSONObject) JSON.toJSON(ui));
            ud.getUserInfo().put("userPin", ui.getUserPin());
            ud.setAuthoritiesByRoles((JSONArray) JSON.toJSON(roles));
            ud.setUserOptList(userOptList);
            List<UserUnit> uulist = new ArrayList<>(10);
            for(IUserUnit uu : uus) {
                if (StringUtils.equals(uu.getUserCode(), ui.getUserCode())) {
                    uulist.add((UserUnit) uu);
                }
            }
            ud.setUserUnits((JSONArray) JSON.toJSON(uulist));
            userDetails.add(ud);
        }
        allUserDetailsRepo.setFreshtDate(userDetails);
    }

    @Override
    public boolean checkUserPassword(String userCode,String userPassword){
        UserInfo ui= (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedTarget().get(userCode);
        if(ui==null)
            return false;
        return passwordEncoder.isPasswordValid(ui.getUserPin(),userPassword, userCode);
    }

    private static List<OptInfo> getMenuFuncs(List<OptInfo> preOpts, List<OptInfo> ls) {
        boolean isNeeds[] = new boolean[preOpts.size()];
        for (int i = 0; i < preOpts.size(); i++) {
            isNeeds[i] = false;
        }
        List<OptInfo> opts = new ArrayList<>();

        for (OptInfo opm : ls) {
            opts.add(opm);
            for (int i = 0; i < preOpts.size(); i++) {
                if (opm.getPreOptId() != null && opm.getPreOptId().equals(preOpts.get(i).getOptId())) {
                    isNeeds[i] = true;
                    break;
                }
            }
        }

        List<OptInfo> needAdd = new ArrayList<>();
        for (int i = 0; i < preOpts.size(); i++) {
            if (isNeeds[i]) {
                needAdd.add(preOpts.get(i));
            }
        }

        boolean isNeeds2[] = new boolean[preOpts.size()];
        while (true) {
            int nestedMenu = 0;
            for (int i = 0; i < preOpts.size(); i++)
                isNeeds2[i] = false;

            for (int i = 0; i < needAdd.size(); i++) {
                for (int j = 0; j < preOpts.size(); j++) {
                    if (!isNeeds[j] && needAdd.get(i).getPreOptId() != null
                        && needAdd.get(i).getPreOptId().equals(preOpts.get(j).getOptId())) {
                        isNeeds[j] = true;
                        isNeeds2[j] = true;
                        nestedMenu++;
                        break;
                    }
                }
            }
            if (nestedMenu == 0)
                break;

            needAdd.clear();
            for (int i = 0; i < preOpts.size(); i++) {
                if (isNeeds2[i]) {
                    needAdd.add(preOpts.get(i));
                }
            }

        }

        for (int i = 0; i < preOpts.size(); i++) {
            if (isNeeds[i]) {
                opts.add(preOpts.get(i));
            }
        }
        return opts;
    }

    /**
     * 将菜单列表组装为树状
     * @param optInfos 菜单列表
     * @return 树状菜单列表
     */
    private List<OptInfo> formatMenuTree(List<OptInfo> optInfos) {
        Iterator<OptInfo> menus = optInfos.iterator();

        List<OptInfo> parentMenu = new ArrayList<>();
        while (menus.hasNext()) {
            OptInfo optInfo = menus.next();
            boolean getParent = false;
            for (OptInfo opt : optInfos) {
                if (opt.getOptId().equals(optInfo.getPreOptId())) {
                    opt.addChild(optInfo);
                    getParent = true;
                    break;
                }
            }
            if(!getParent) {
                parentMenu.add(optInfo);
            }
        }
        return parentMenu;
    }

    /**
     * 将菜单列表组装为树状
     * @param optInfos 菜单列表
     * @param superOptId 顶级菜单ID 不为空时返回该菜单的下级菜单
     * @return 树状菜单列表
     */
    private List<OptInfo> formatMenuTree(List<OptInfo> optInfos,String superOptId) {
        if (StringUtils.isEmpty(superOptId)){
            return Collections.emptyList();
        }

        Iterator<OptInfo> menus = optInfos.iterator();
        OptInfo parentOpt = null;

        while (menus.hasNext()) {
            OptInfo optInfo = menus.next();
            if (StringUtils.equals(superOptId, optInfo.getOptId())) {
                parentOpt=optInfo;
            }
            for (OptInfo opt : optInfos) {
                if (opt.getOptId().equals(optInfo.getPreOptId())) {
                    opt.addChild(optInfo);
                }
            }
        }
        if (parentOpt!=null){
            return parentOpt.getChildren();
        }else {
            return Collections.emptyList();
        }
    }

    private List<OptInfo> listUserOptInfos(String userCode){
        List<OptInfo> userOptinfos = new ArrayList<>();

        Set<String> optIds = new HashSet<>(20);
        for (UserRole ur : allUserRoleRepo.getCachedTarget()) {
            if (StringUtils.equals(ur.getUserCode(), userCode)) {
                RoleInfo ri = (RoleInfo)CodeRepositoryCache.codeToRoleMap.getCachedTarget().get(ur.getRoleCode());
                if (ri != null) {
                    for (RolePower rp : ri.getRolePowers()) {
                        OptMethod om = (OptMethod)CodeRepositoryCache.codeToMethodMap.getCachedTarget().get(rp.getOptCode());
                        if (om != null)
                            optIds.add(om.getOptId());
                    }
                }
            }
        }

        for(String optId : optIds){
            OptInfo oi = (OptInfo)CodeRepositoryCache.codeToOptMap.getCachedTarget().get(optId);
            if("Y".equals(oi.getIsInToolbar())){
                OptInfo soi = new OptInfo();
                soi.copy(oi);
                soi.setOptId(oi.getOptId());
                userOptinfos.add(soi);
            }
        }
        return userOptinfos;
    }


    protected List<OptInfo> getDirectOptInfo(){
        List<OptInfo> dirOptInfos = new ArrayList<>();
        for(IOptInfo oi : CodeRepositoryCache.optInfoRepo.getCachedTarget()){
            if(StringUtils.equals(oi.getOptUrl(),"...")){
                OptInfo soi = new OptInfo();
                soi.copy((OptInfo)oi);
                soi.setOptId(oi.getOptId());
                dirOptInfos.add(soi);
            }
        }
        return dirOptInfos;
    }


    @Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
        List<OptInfo> userOptinfos =listUserOptInfos(userCode/*ud.getUserCode()*/);
        List<OptInfo> preOpts = getDirectOptInfo();

        List<OptInfo> allUserOpt = getMenuFuncs(preOpts,userOptinfos);

        Collections.sort(allUserOpt, (o1, o2) -> // Long.compare(o1.getOrderInd() , o2.getOrderInd())) ;
            ( o2.getOrderInd() == null && o1.getOrderInd() == null)? 0 :
                ( (o2.getOrderInd() == null)? 1 :
                    (( o1.getOrderInd() == null)? -1 :
                        ((o1.getOrderInd() > o2.getOrderInd())? 1:(
                            o1.getOrderInd() < o2.getOrderInd()?-1:0 ) ))));
        return formatMenuTree(allUserOpt);
    }

    @Override
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(
        String userCode,String superOptId , boolean asAdmin) {

       /* CentitUserDetails ud =  loadUserDetailsByUserCode(userCode);
        if(ud==null)
            return null;*/
        List<OptInfo> userOptinfos =listUserOptInfos(userCode/*ud.getUserCode()*/);
        List<OptInfo> preOpts = getDirectOptInfo();

        List<OptInfo> allUserOpt = getMenuFuncs(preOpts,userOptinfos);

        Collections.sort(allUserOpt, (o1, o2) -> // Long.compare(o1.getOrderInd() , o2.getOrderInd())) ;
            ( o2.getOrderInd() == null && o1.getOrderInd() == null)? 0 :
                ( (o2.getOrderInd() == null)? 1 :
                    (( o1.getOrderInd() == null)? -1 :
                        ((o1.getOrderInd() > o2.getOrderInd())? 1:(
                            o1.getOrderInd() < o2.getOrderInd()?-1:0 ) ))));

        return formatMenuTree(allUserOpt,superOptId);
    }


    @Override
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        List<DataDictionary> dictionaries = new ArrayList<>(20);
        for(DataDictionary data : allDictionaryRepo.getCachedTarget()){
            if( StringUtils.equals(catalogCode,data.getCatalogCode())){
                dictionaries.add(data);
            }
        }
        return dictionaries.isEmpty()?null:dictionaries;
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode) {
        List<UserUnit> userUnits = new ArrayList<>(10);
        for (IUserUnit uu : CodeRepositoryCache.userUnitRepo.getCachedTarget()) {
            if(StringUtils.equals(userCode,uu.getUserCode())){
                userUnits.add((UserUnit)uu);
            }
        }
        return userUnits;
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        List<UserUnit> unitUsers = new ArrayList<>(10);
        for (IUserUnit uu : CodeRepositoryCache.userUnitRepo.getCachedTarget()) {
            if(StringUtils.equals(unitCode,uu.getUnitCode())){
                unitUsers.add((UserUnit)uu);
            }
        }
        return unitUsers;
    }

    /**
     * 获取 用户角色关系
     * @param userCode 用户代码
     * @return  List 用户所有菜单功能
     */
    @Override
    public List<UserRole> listUserRoles(String userCode){
        List<UserRole> roles = new ArrayList<>();
        for (UserRole ur : allUserRoleRepo.getCachedTarget()) {
            if (StringUtils.equals(ur.getUserCode(),userCode)) {
                roles.add(ur);
            }
        }
        return roles;
    }

    /**
     * 获取 角色用户关系
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    @Override
    public List<UserRole> listRoleUsers(String roleCode){
        List<UserRole> users = new ArrayList<>();
        for (UserRole ur : allUserRoleRepo.getCachedTarget()) {
            if (StringUtils.equals(ur.getRoleCode(),roleCode)) {
                users.add(ur);
            }
        }
        return users;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        for(JsonCentitUserDetails ud : allUserDetailsRepo.getCachedTarget()){
            if(StringUtils.equals(ud.getUserInfo().getString("loginName"), loginName))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        for(JsonCentitUserDetails ud : allUserDetailsRepo.getCachedTarget()){
            if(StringUtils.equals(ud.getUserInfo().getString("userCode"), userCode))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        for(JsonCentitUserDetails ud : allUserDetailsRepo.getCachedTarget()){
            if(StringUtils.equals(ud.getUserInfo().getString("regEmail"), regEmail))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        for(JsonCentitUserDetails ud : allUserDetailsRepo.getCachedTarget()){
            if(StringUtils.equals(ud.getUserInfo().getString("regCellPhone"), regCellPhone))
                return ud;
        }
        return null;
    }


    @Override
    public UserSetting getUserSetting(String userCode, String paramCode) {
        CentitUserDetails ud =  loadUserDetailsByUserCode(userCode);
        if(ud==null)
            return null;
        //userCode, String paramCode,String paramValue, String paramName
        return new UserSetting(ud.getUserCode(),paramCode,
            ud.getUserSettingValue(paramCode), "用户参数");
    }

    @Override
    public List<UserSetting> listUserSettings(String userCode){
        CentitUserDetails ud =  loadUserDetailsByUserCode(userCode);
        if(ud==null) {
            return null;
        }
        //userCode, String paramCode,String paramValue, String paramName
        Map<String, String> settingMap =  ud.getUserSettings();
        if(settingMap==null){
            return null;
        }
        List<UserSetting> userSettings = new ArrayList<>(settingMap.size()+1);
        for(Map.Entry<String, String> ent : ud.getUserSettings().entrySet()){
            userSettings.add( new UserSetting(ud.getUserCode(),
                ent.getKey(),ent.getValue(),"用户参数"));
        }
        return userSettings;
    }

    @Override
    public void updateUserInfo(IUserInfo userInfo) {
        UserInfo ui = (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedTarget().get(userInfo.getUserCode());
        if(ui==null)
            return;
        ui.copyNotNullProperty(userInfo);
    }

    @Override
    public void saveUserSetting(IUserSetting userSetting) {
        CentitUserDetails ud =  loadUserDetailsByUserCode(userSetting.getUserCode());
        if(ud==null)
            return;
        ud.setUserSettingValue(userSetting.getParamCode(),userSetting.getParamValue());
    }

    @Override
    public List<? extends IUserInfo> listAllUsers() {
        reloadPlatformData();
        return CodeRepositoryCache.userInfoRepo.getCachedTarget();
    }

    @Override
    public List<? extends IUnitInfo> listAllUnits() {
        reloadPlatformData();
        return CodeRepositoryCache.unitInfoRepo.getCachedTarget();
    }

    @Override
    public List<? extends IUserUnit> listAllUserUnits(){
        reloadPlatformData();
        return CodeRepositoryCache.userUnitRepo.getCachedTarget();
    }

    @Override
    public List<? extends IDataCatalog> listAllDataCatalogs(){
        reloadPlatformData();
        return CodeRepositoryCache.catalogRepo.getCachedTarget();
    }

    /**
     * 获取所有角色信息
     *
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRoleInfo> listAllRoleInfo() {
        reloadPlatformData();
        return CodeRepositoryCache.roleInfoRepo.getCachedTarget();
    }

    /**
     * 获取业务操作信息
     *
     * @return List 业务信息
     */
    @Override
    public List<? extends IOptInfo> listAllOptInfo() {
        reloadPlatformData();
        return CodeRepositoryCache.optInfoRepo.getCachedTarget();
    }

    /**
     * 获取所有角色和权限对应关系
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IRolePower> listAllRolePower(){
        reloadPlatformData();
        return CodeRepositoryCache.rolePowerRepo.getCachedTarget();
    }

    protected List<DataDictionary> listAllDataDictionary() {
        reloadPlatformData();
        return allDictionaryRepo.getCachedTarget();
    }

    protected List<UserRole> listAllUserRole() {
        reloadPlatformData();
        return allUserRoleRepo.getCachedTarget();
    }
    /**
     * 获取操作方法信息
     * @return List 操作方法信息
     */
    @Override
    public List<? extends IOptMethod> listAllOptMethod(){
        reloadPlatformData();
        return CodeRepositoryCache.optMethodRepo.getCachedTarget();
    }


    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return null;
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return null;
    }

    /**
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods){

    }

}
