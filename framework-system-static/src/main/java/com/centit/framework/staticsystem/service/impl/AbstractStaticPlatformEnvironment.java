package com.centit.framework.staticsystem.service.impl;

import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUnitRole;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserSetting;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.staticsystem.security.StaticCentitUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import java.util.*;

public abstract class AbstractStaticPlatformEnvironment
    implements PlatformEnvironment {
    protected List<UserInfo> userinfos;
    protected List<StaticCentitUserDetails> userDetails;
    protected List<OptInfo> optinfos;
    protected List<OptMethod> optmethods;
    protected List<RoleInfo> roleinfos;
    protected List<RolePower> rolepowers;
    protected List<UserRole> userroles;
    protected List<UserUnit> userunits;
    protected List<DataCatalog> datacatalogs;
    protected List<DataDictionary> datadictionaies;
    protected List<UnitInfo> unitinfos;

    protected CentitPasswordEncoder passwordEncoder;

    public void setPasswordEncoder(CentitPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    protected void organizeDictionaryData() {
        for (DataDictionary dd : datadictionaies) {
            DataCatalog dc = getDataCatalog(dd.getCatalogCode());
            if (dc != null)
                dc.addDataDictionary(dd);
        }

        for (RoleInfo ri : roleinfos) {
            for (RolePower rp : rolepowers) {
                if (StringUtils.equals(rp.getRoleCode(), ri.getRoleCode())) {
                    ri.addRolePowers(rp);
                    /*OptMethod om = getOptMethod(rp.getOptCode());
                    if(om!=null)
                        userOptList.put(om.getOptId()+"-"+om.getOptMethod(), "T");*/
                }
            }
        }
        userDetails = new ArrayList<>(userinfos.size());
        for (UserInfo ui : userinfos) {
            List<RoleInfo> roles = new ArrayList<>();
            Map<String, String> userOptList = new HashMap<>();
            for (UserRole ur : userroles) {
                if (StringUtils.equals(ur.getUserCode(), ui.getUserCode())) {
                    RoleInfo ri = getRoleInfo(ur.getRoleCode());
                    if (ri != null) {
                        roles.add(ri);
                        for (RolePower rp : ri.getRolePowers()) {
                            OptMethod om = getOptMethod(rp.getOptCode());
                            if (om != null)
                                userOptList.put(om.getOptId() + "-" + om.getOptMethod(), om.getOptMethod());
                        }
                    }
                }
            }
            StaticCentitUserDetails ud = new StaticCentitUserDetails(ui);
            ud.setAuthoritiesByRoles(roles);
            ud.setUserOptList(userOptList);
            userDetails.add(ud);
        }

        for (UnitInfo ui : unitinfos) {
            UnitInfo unit = getUnitInfoByUnitCode(ui.getParentUnit());
            if (unit != null)
                unit.addSubUnit(ui);
        }
        for (UserUnit uu : userunits) {
            UserInfo user = getUserInfoByUserCode(uu.getUserCode());
            if (user != null)
                user.addUserUnit(uu);
            UnitInfo unit = getUnitInfoByUnitCode(uu.getUnitCode());
            if (unit != null)
                unit.addUnitUser(uu);
        }
    }
    protected DataCatalog getDataCatalog(String catalogCode){
        for(DataCatalog dc : datacatalogs){
            if(StringUtils.equals(dc.getCatalogCode(),catalogCode))
                return dc;
        }
        return null;
    }

    protected List<OptInfo> getDirectOptInfo(){
        List<OptInfo> dirOptInfos = new ArrayList<OptInfo>();
        for(OptInfo oi : optinfos){
            if(StringUtils.equals(oi.getOptUrl(),"...")){
                OptInfo soi = new OptInfo();
                soi.copy(oi);
                soi.setOptId(oi.getOptId());
                dirOptInfos.add(soi);
            }
        }
        return dirOptInfos;
    }

    protected OptInfo getOptInfo(String optId){
        for(OptInfo oi : optinfos){
            if(StringUtils.equals(oi.getOptId(),optId))
                return oi;
        }
        return null;
    }

    protected OptMethod getOptMethod(String optCode){
        for(OptMethod om : optmethods){
            if(StringUtils.equals(om.getOptCode(),optCode))
                return om;
        }
        return null;
    }

    protected RoleInfo getRoleInfo(String roleCode){
        for(RoleInfo ri : roleinfos){
            if(StringUtils.equals(ri.getRoleCode(),roleCode))
                return ri;
        }
        return null;
    }

    @Override
    public UnitInfo getUnitInfoByUnitCode(String unitCode){
        for(UnitInfo ui : unitinfos){
            if(StringUtils.equals(ui.getUnitCode(),unitCode))
                return ui;
        }
        return null;
    }   

    @Override
    public UserInfo getUserInfoByUserCode(String userCode){
        for(UserInfo ui : userinfos){
            if(StringUtils.equals(ui.getUserCode(),userCode))
                return ui;
        }
        return null;
    }

    @Override
    public UserInfo getUserInfoByLoginName(String loginName) {
        for(UserInfo ui : userinfos){
            if(StringUtils.equals(ui.getLoginName(),loginName))
                return ui;
        }
        return null;
    }

    @Override
    public boolean checkUserPassword(String userCode,String userPassword){
        UserInfo ui= getUserInfoByUserCode(userCode);
        if(ui==null)
            return false;
        return passwordEncoder.isPasswordValid(ui.getUserPin(),userPassword, userCode);
    }

    @Override
    public String getSystemParameter(String paramCode) {
        return SysParametersUtils.getStringValue(paramCode);
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
    public void updateUserInfo(IUserInfo userInfo) {
        UserInfo ui = this.getUserInfoByUserCode(userInfo.getUserCode());
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
    public List<UserInfo> listAllUsers() {
        return userinfos;
    }

    @Override
    public List<UnitInfo> listAllUnits() {
        return unitinfos;
    }

    @Override
    public List<UserUnit> listAllUserUnits() {
        return userunits;
    }

    @Override
    public Map<String,RoleInfo> getRoleRepo() {
        Map<String,RoleInfo> roleRepo = new HashMap<String,RoleInfo>();
        for(RoleInfo role:roleinfos){
            roleRepo.put(role.getRoleCode(), role);
        }
        return roleRepo;
    }

    @Override
    public Map<String,OptMethod> getOptMethodRepo() {
        Map<String,OptMethod> methodRepo = new HashMap<String,OptMethod>();
        for(OptMethod method:optmethods){
            methodRepo.put(method.getOptCode(), method);
        }
        return methodRepo;
    }

    @Override
    public List<DataCatalog> listAllDataCatalogs() {
        return datacatalogs;
    }

    @Override
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        DataCatalog dc = getDataCatalog(catalogCode);
        if(dc!=null)
            return dc.getDataDictionaries();
        return null;
    }

    @Override
    public List<UserUnit> listUserUnits(String userCode) {
        UserInfo ui = getUserInfoByUserCode(userCode);
        if(ui!=null)
            return ui.getUserUnits();
        return null;
    }

    @Override
    public List<UserUnit> listUnitUsers(String unitCode) {
        UnitInfo ui = getUnitInfoByUnitCode(unitCode);
        if(ui!=null)
            return ui.getUnitUsers();
        return null;
    }

    @Override
    public Map<String,OptInfo> getOptInfoRepo() {
        Map<String,OptInfo> optRepo = new HashMap<String,OptInfo>();
        for(OptInfo opt:optinfos){
            optRepo.put(opt.getOptId(), opt);
        }
        return optRepo;
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

    private static List<OptInfo> listObjectFormatAndFilterOptId(List<OptInfo> optInfos,String superOptId) {
        // 获取当前菜单的子菜单
        Iterator<OptInfo> menus = optInfos.iterator();

        OptInfo parentOpt = null;

        List<OptInfo> parentMenu = new ArrayList<OptInfo>();
        while (menus.hasNext()) {

            OptInfo optInfo = menus.next();
            //去掉级联关系后需要手动维护这个属性

            if (superOptId!=null && superOptId.equals(optInfo.getOptId())) {
                parentOpt=optInfo;
            }
            boolean getParent = false;
            for (OptInfo opt : optInfos) {
                if (opt.getOptId().equals(optInfo.getPreOptId())) {
                    opt.addChild(optInfo);
                    getParent = true;
                    break;
                }
            }
            if(!getParent)
                parentMenu.add(optInfo);
        }

        if (superOptId!=null && parentOpt!=null){
                return parentOpt.getChildren();
            //else
                //return null;
        }else {
            return parentMenu;
        }
    }

    private List<OptInfo> listUserOptInfos(String userCode){
        List<OptInfo> userOptinfos = new ArrayList<>();

        Set<String> optIds = new HashSet<>(20);
        for (UserRole ur : userroles) {
            if (StringUtils.equals(ur.getUserCode(), userCode)) {
                RoleInfo ri = getRoleInfo(ur.getRoleCode());
                if (ri != null) {
                    for (RolePower rp : ri.getRolePowers()) {
                        OptMethod om = getOptMethod(rp.getOptCode());
                        if (om != null)
                            optIds.add(om.getOptId());
                    }
                }
            }
        }

        for(String optId : optIds){
            OptInfo oi = this.getOptInfo(optId);
            if("Y".equals(oi.getIsInToolbar())){
                OptInfo soi = new OptInfo();
                soi.copy(oi);
                soi.setOptId(oi.getOptId());
                userOptinfos.add(soi);
            }
        }
        return userOptinfos;
    }

    @Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
        CentitUserDetails ud =  loadUserDetailsByUserCode(userCode);
        if(ud==null)
            return null;

        List<OptInfo> userOptinfos =listUserOptInfos(ud.getUserCode());
        List<OptInfo> preOpts = getDirectOptInfo();

        List<OptInfo> allUserOpt = getMenuFuncs(preOpts,userOptinfos);

        Collections.sort(allUserOpt, (o1, o2) -> // Long.compare(o1.getOrderInd() , o2.getOrderInd())) ;
                ( o2.getOrderInd() == null && o1.getOrderInd() == null)? 0 :
                        ( (o2.getOrderInd() == null)? 1 :
                                (( o1.getOrderInd() == null)? -1 :
                                        ((o1.getOrderInd() > o2.getOrderInd())? 1:(
                                                o1.getOrderInd() < o2.getOrderInd()?-1:0 ) ))));

        return listObjectFormatAndFilterOptId(allUserOpt,null);
    }

    @Override
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(
            String userCode,String superOptId , boolean asAdmin) {
        CentitUserDetails ud =  loadUserDetailsByUserCode(userCode);
        if(ud==null)
            return null;

        List<OptInfo> userOptinfos =listUserOptInfos(ud.getUserCode());

        List<OptInfo> preOpts = getDirectOptInfo();

        List<OptInfo> allUserOpt = getMenuFuncs(preOpts,userOptinfos);

        return listObjectFormatAndFilterOptId(allUserOpt,superOptId);
    }

    /**
     * 获取用户所有角色
     * @param userCode 用户代码
     * @return  List 用户所有菜单功能
     */
    @Override
    public List<RoleInfo> listUserRolesByUserCode(String userCode){
        List<RoleInfo> roles = new ArrayList<>();
        for (UserRole ur : userroles) {
            if (StringUtils.equals(ur.getUserCode(),userCode)) {
                roles.add(getRoleInfo(ur.getRoleCode()));
            }
        }
        return roles;
    }

    /**
     * 获取拥有改角色的所有用户
     * @param roleCode 角色代码
     * @return  List 用户所有菜单功能
     */
    @Override
    public List<UserInfo> listRoleUserByRoleCode(String roleCode){
        List<UserInfo> users = new ArrayList<>();
        for (UserRole ur : userroles) {
            if (StringUtils.equals(ur.getRoleCode(),roleCode)) {
                users.add(getUserInfoByUserCode(ur.getRoleCode()));
            }
        }
        return users;
    }
    /**
     * 获取 用户角色关系
     * @param userCode 用户代码
     * @return  List 用户所有菜单功能
     */
    @Override
    public List<UserRole> listUserRoles(String userCode){
        List<UserRole> roles = new ArrayList<>();
        for (UserRole ur : userroles) {
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
        for (UserRole ur : userroles) {
            if (StringUtils.equals(ur.getRoleCode(),roleCode)) {
                users.add(ur);
            }
        }
        return users;
    }

    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return null;
    }

    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return null;
    }
    @Override
    public Map<String,UnitInfo> getUnitRepo() {
        Map<String,UnitInfo> unitRepo = new HashMap<>();
        for(UnitInfo unit:unitinfos){
            unitRepo.put(unit.getUnitCode(), unit);
        }
        return unitRepo;
    }

    @Override
    public Map<String,UserInfo> getUserRepo() {
        Map<String,UserInfo> userRepo = new HashMap<>();
        for(UserInfo user:userinfos){
            userRepo.put(user.getUserCode(), user);
        }
        return userRepo;
    }

    @Override
    public Map<String, UserInfo> getLoginNameRepo() {
        Map<String, UserInfo> userRepo = new HashMap<>();
        for(UserInfo user : userinfos){
            userRepo.put(user.getLoginName(), user);
        }
        return userRepo;
    }

    @Override
    public Map<String,UnitInfo> getDepNoRepo() {
        Map<String,UnitInfo> depnoRepo = new HashMap<>();
        for(UnitInfo unit:unitinfos){
            depnoRepo.put(unit.getDepNo(), unit);
        }
        return depnoRepo;
    }

    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        for(StaticCentitUserDetails ud : userDetails){
            if(StringUtils.equals(ud.getUserInfo().getLoginName(), loginName))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        for(StaticCentitUserDetails ud : userDetails){
            if(StringUtils.equals(ud.getUserInfo().getUserCode(), userCode))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        for(StaticCentitUserDetails ud : userDetails){
            if(StringUtils.equals(ud.getUserInfo().getRegEmail(), regEmail))
                return ud;
        }
        return null;
    }

    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        for(StaticCentitUserDetails ud : userDetails){
            if(StringUtils.equals(ud.getUserInfo().getRegCellPhone(), regCellPhone))
                return ud;
        }
        return null;
    }

    @Override
    public boolean reloadSecurityMetadata() {
        CentitSecurityMetadata.optMethodRoleMap.clear();
        if(rolepowers==null || rolepowers.size()==0)
            return false;
        for(RolePower rp: rolepowers ){
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
        for(OptMethod ou:optmethods){
            OptInfo oi = getOptInfo(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = oi.getOptUrl()+ou.getOptUrl();
                List<List<String>> sOpt = CentitSecurityMetadata.parseUrl(
                        optDefUrl,ou.getOptReq());

                for(List<String> surls : sOpt){
                    OptTreeNode opt = CentitSecurityMetadata.optTreeNode;
                    for(String surl : surls)
                        opt = opt.setChildPath(surl);
                    opt.setOptCode(ou.getOptCode());
                }
            }
        }
        return true;
    }

    @Override
    public List<UserSetting> getAllSettings(){
        return null;
    }

}
