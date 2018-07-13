package com.centit.framework.staticsystem.service.impl;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.staticsystem.security.StaticCentitUserDetails;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import java.util.*;

public abstract class AbstractStaticPlatformEnvironment
    implements PlatformEnvironment {


    public CachedObject<List<DataDictionary>> allDictionaryRepo =
        new CachedObject<>(this::listAllDataDictionary,
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    public CachedObject<List<UserRole>> allUserRoleRepo =
        new CachedObject<>(this::listAllUserRole,
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    public CachedObject<List<StaticCentitUserDetails>> allUserDetailsRepo =
        new CachedObject<>(()-> { this.reloadDictionary(); return null;},
            CodeRepositoryCache.CACHE_NEVER_EXPIRE );

    protected CentitPasswordEncoder passwordEncoder;

    public void setPasswordEncoder(CentitPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    protected abstract List<DataDictionary> listAllDataDictionary();
    protected abstract List<UserRole> listAllUserRole();

    @SuppressWarnings("unchecked")
    protected void organizeDictionaryData() {
        for (IDataCatalog dd :  CodeRepositoryCache.catalogRepo.getCachedObject()) {
            ((DataCatalog)dd).setDataDictionaries(
                (List<DataDictionary>) listDataDictionaries(dd.getCatalogCode()));
        }

        for (IRoleInfo ri : CodeRepositoryCache.roleInfoRepo.getCachedObject()) {
            for (IRolePower rp : CodeRepositoryCache.rolePowerRepo.getCachedObject()) {
                if (StringUtils.equals(rp.getRoleCode(), ri.getRoleCode())) {
                    ((RoleInfo)ri).addRolePowers((RolePower)rp);
                    /*OptMethod om = getOptMethod(rp.getOptCode());
                    if(om!=null)
                        userOptList.put(om.getOptId()+"-"+om.getOptMethod(), "T");*/
                }
            }
        }
        List<? extends IUserInfo> userinfos = CodeRepositoryCache.userInfoRepo.getCachedObject();

        List<StaticCentitUserDetails> userDetails = new ArrayList<>(userinfos.size());
        for (IUserInfo ui : userinfos) {
            List<RoleInfo> roles = new ArrayList<>();
            Map<String, String> userOptList = new HashMap<>();
            for (UserRole ur : allUserRoleRepo.getCachedObject()) {
                if (StringUtils.equals(ur.getUserCode(), ui.getUserCode())) {
                    IRoleInfo ri = CodeRepositoryCache.codeToRoleMap.getCachedObject().get(ur.getRoleCode());
                    if (ri != null) {
                        roles.add((RoleInfo)ri);
                        for (IRolePower rp : ri.getRolePowers()) {
                            IOptMethod om = CodeRepositoryCache.codeToMethodMap.getCachedObject().get(rp.getOptCode());
                            if (om != null)
                                userOptList.put(om.getOptId() + "-" + om.getOptMethod(), om.getOptMethod());
                        }
                    }
                }
            }
            StaticCentitUserDetails ud = new StaticCentitUserDetails((UserInfo) ui);
            ud.setAuthoritiesByRoles(roles);
            ud.setUserOptList(userOptList);
            userDetails.add(ud);
        }
        allUserDetailsRepo.setFreshtDate(userDetails);

        for (IUnitInfo ui : CodeRepositoryCache.unitInfoRepo.getCachedObject()) {
            UnitInfo unit = (UnitInfo)CodeRepositoryCache.codeToUnitMap.getCachedObject().get(ui.getParentUnit());
            if (unit != null)
                unit.addSubUnit((UnitInfo)ui);
        }
        for (IUserUnit uu : CodeRepositoryCache.userUnitsRepo.getCachedObject()) {
            UserInfo user = (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedObject().get(uu.getUserCode());
            if (user != null)
                user.addUserUnit((UserUnit)uu);
            UnitInfo unit = (UnitInfo)CodeRepositoryCache.codeToUnitMap.getCachedObject().get(uu.getUnitCode());
            if (unit != null)
                unit.addUnitUser((UserUnit)uu);
        }
    }


    @Override
    public boolean checkUserPassword(String userCode,String userPassword){
        UserInfo ui= (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedObject().get(userCode);
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
        for (UserRole ur : allUserRoleRepo.getCachedObject()) {
            if (StringUtils.equals(ur.getUserCode(), userCode)) {
                RoleInfo ri = (RoleInfo)CodeRepositoryCache.codeToRoleMap.getCachedObject().get(ur.getRoleCode());
                if (ri != null) {
                    for (RolePower rp : ri.getRolePowers()) {
                        OptMethod om = (OptMethod)CodeRepositoryCache.codeToMethodMap.getCachedObject().get(rp.getOptCode());
                        if (om != null)
                            optIds.add(om.getOptId());
                    }
                }
            }
        }

        for(String optId : optIds){
            OptInfo oi = (OptInfo)CodeRepositoryCache.codeToOptMap.getCachedObject().get(optId);
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
        for(IOptInfo oi : CodeRepositoryCache.optInfoRepo.getCachedObject()){
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
        for(DataDictionary data : allDictionaryRepo.getCachedObject()){
            if( StringUtils.equals(catalogCode,data.getCatalogCode())){
                dictionaries.add(data);
            }
        }
        return dictionaries;
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode) {
        List<UserUnit> userUnits = new ArrayList<>(10);
        for (IUserUnit uu : CodeRepositoryCache.userUnitsRepo.getCachedObject()) {
            if(StringUtils.equals(userCode,uu.getUserCode())){
                userUnits.add((UserUnit)uu);
            }
        }
        return userUnits;
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        List<UserUnit> unitUsers = new ArrayList<>(10);
        for (IUserUnit uu : CodeRepositoryCache.userUnitsRepo.getCachedObject()) {
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
        for (UserRole ur : allUserRoleRepo.getCachedObject()) {
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
        for (UserRole ur : allUserRoleRepo.getCachedObject()) {
            if (StringUtils.equals(ur.getRoleCode(),roleCode)) {
                users.add(ur);
            }
        }
        return users;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        for(StaticCentitUserDetails ud : allUserDetailsRepo.getCachedObject()){
            if(StringUtils.equals(ud.getUserInfo().getLoginName(), loginName))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        for(StaticCentitUserDetails ud : allUserDetailsRepo.getCachedObject()){
            if(StringUtils.equals(ud.getUserInfo().getUserCode(), userCode))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        for(StaticCentitUserDetails ud : allUserDetailsRepo.getCachedObject()){
            if(StringUtils.equals(ud.getUserInfo().getRegEmail(), regEmail))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        for(StaticCentitUserDetails ud : allUserDetailsRepo.getCachedObject()){
            if(StringUtils.equals(ud.getUserInfo().getRegCellPhone(), regCellPhone))
                return ud;
        }
        return null;
    }

    @Override
    public boolean reloadSecurityMetadata() {
        CentitSecurityMetadata.optMethodRoleMap.clear();
        List<? extends IRolePower> rolepowers =  CodeRepositoryCache.rolePowerRepo.getCachedObject();
        if(rolepowers==null || rolepowers.size()==0)
            return false;
        for(IRolePower rp: rolepowers){
            List<ConfigAttribute/*roleCode*/> roles = CentitSecurityMetadata.optMethodRoleMap.get(rp.getOptCode());
            if(roles == null){
                roles = new ArrayList</*roleCode*/>();
            }
            roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + StringUtils.trim(rp.getRoleCode())));
            CentitSecurityMetadata.optMethodRoleMap.put(rp.getOptCode(), roles);
        }
        //将操作和角色对应关系中的角色排序，便于权限判断中的比较
        CentitSecurityMetadata.sortOptMethodRoleMap();


        CentitSecurityMetadata.optTreeNode.setChildList(null);
        CentitSecurityMetadata.optTreeNode.setOptCode(null);
        for(IOptMethod ou : CodeRepositoryCache.optMethodRepo.getCachedObject()){
            IOptInfo oi = CodeRepositoryCache.codeToOptMap.getCachedObject().get(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = StringBaseOpt.concat(oi.getOptUrl(),ou.getOptUrl());
                List<List<String>> sOpt = CentitSecurityMetadata.parsePowerDefineUrl(
                        optDefUrl,ou.getOptReq());

                for(List<String> surls : sOpt){
                    OptTreeNode opt = CentitSecurityMetadata.optTreeNode;
                    for(String surl : surls)
                        opt = opt.setChildPath(surl);
                    opt.setOptCode(ou.getOptCode());
                }
            }
        }

        CentitSecurityMetadata.confirmLoginCasMustBeAuthed();

        return true;
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
        UserInfo ui = (UserInfo)CodeRepositoryCache.codeToUserMap.getCachedObject().get(userInfo.getUserCode());
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
    /**
     * 新增菜单和操作
     * @param optInfos 菜单对象集合
     * @param optMethods 操作对象集合
     */
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods){

    }

}
