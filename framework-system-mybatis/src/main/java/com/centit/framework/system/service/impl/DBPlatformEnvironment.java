package com.centit.framework.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.system.dao.*;
import com.centit.framework.system.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.system.security.CentitUserDetailsImpl;
import com.centit.support.algorithm.StringRegularOpt;

@Service("platformEnvironment")
public class DBPlatformEnvironment implements PlatformEnvironment {

	@Resource
	private CentitPasswordEncoder passwordEncoder;

	@Resource
    @NotNull
    private UserSettingDao userSettingDao;
	
	@Resource
    @NotNull
	private OptInfoDao optInfoDao;
	
	@Resource
	private UserInfoDao sysuserdao;
	
	@Resource
    @NotNull
    private DataDictionaryDao dictionaryDao;

    @Resource
    @NotNull
    protected DataCatalogDao dataCatalogDao;
 
    @Resource
    @NotNull
    private UserUnitDao userUnitDao;

    @Resource
    @NotNull
    protected UnitInfoDao unitInfoDao;
 
    @Resource
    @NotNull
    protected RoleInfoDao roleInfoDao;
    
    @Resource
    @NotNull
    private UserRoleDao userRoleDao;
    
    @Resource
    @NotNull
    protected OptMethodDao optMethodDao;

	@Resource
	@NotNull
	private RolePowerDao rolePowerDao;
	/**
	 * 刷新数据字典
	 *
	 * @return  boolean 刷新数据字典
	 */
	@Override
	public boolean reloadDictionary() {
		return false;
	}

	/**
	 * 刷新权限相关的元数据
	 *
	 * @return boolean 刷新权限相关的元数据
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean reloadSecurityMetadata() {
		CentitSecurityMetadata.optMethodRoleMap.clear();
		List<RolePower> rplist = rolePowerDao.listObjectsAll();
		if(rplist==null || rplist.size()==0)
			return false;
		for(RolePower rp: rplist ){
			List<ConfigAttribute/*roleCode*/> roles = CentitSecurityMetadata.optMethodRoleMap.get(rp.getOptCode());
			if(roles == null){
				roles = new ArrayList<ConfigAttribute/*roleCode*/>();
			}
			roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + StringUtils.trim(rp.getRoleCode())));
			CentitSecurityMetadata.optMethodRoleMap.put(rp.getOptCode(), roles);
		}
		//将操作和角色对应关系中的角色排序，便于权限判断中的比较
		CentitSecurityMetadata.sortOptMethodRoleMap();

		List<OptMethodUrlMap> oulist = optInfoDao.listAllOptMethodUrlMap();
		CentitSecurityMetadata.optTreeNode.setChildList(null);
		CentitSecurityMetadata.optTreeNode.setOptCode(null);
		for(OptMethodUrlMap ou:oulist){
			List<List<String>> sOpt = CentitSecurityMetadata.parseUrl(ou.getOptDefUrl() ,ou.getOptReq());

			for(List<String> surls : sOpt){
				OptTreeNode opt = CentitSecurityMetadata.optTreeNode;
				for(String surl : surls)
					opt = opt.setChildPath(surl);
				opt.setOptCode(ou.getOptCode());
			}
		}
		return true;
	}

	@Override
	public String getSystemParameter(String paramCode) {
		return SysParametersUtils.getStringValue(paramCode);
	}

	@Override
	@Transactional(readOnly = true)
	public String getUserSetting(String userCode, String paramCode) {
		UserSetting us = userSettingDao.getObjectById(new UserSettingId(userCode,paramCode));
		if(us==null)
			return null;
		else 
			return us.getParamValue();
	}
	
	private List<OptInfo> formatMenuTree(List<OptInfo> optInfos,String superOptId) {
        // 获取当前菜单的子菜单
        Iterator<OptInfo> menus = optInfos.iterator();
        OptInfo parentOpt = null;
        
        List<OptInfo> parentMenu = new ArrayList<OptInfo>();
        while (menus.hasNext()) {            
            OptInfo optInfo = menus.next();
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
	    }else
	    	return parentMenu;
    }

	@Override
	@Transactional(readOnly = true)
	public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
	   // optInfoDao.getMenuFuncByUserID(userCode, asAdmin);
	    
	    List<OptInfo> preOpts=optInfoDao.getMenuFuncByOptUrl();
	    Map map=new HashMap();
	    map.put("userCode", userCode);
	    map.put("optType", asAdmin ? "S" : "O");
	    List<FVUserOptMoudleList> ls=optInfoDao.getMenuFuncByUserID(map);
	    List<OptInfo> menuFunsByUser = getMenuFuncs( preOpts,  ls);
	    
	    return formatMenuTree(menuFunsByUser,null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId,
			boolean asAdmin) {
		//List<OptInfo> menuFunsByUser = optInfoDao.getMenuFuncByUserID(userCode, asAdmin);   
		 List<OptInfo> preOpts=optInfoDao.getMenuFuncByOptUrl();
	    Map map=new HashMap();
	    map.put("userCode", userCode);
	    map.put("optType", asAdmin ? "S" : "O");
	    List<FVUserOptMoudleList> ls=optInfoDao.getMenuFuncByUserID(map);
	    List<OptInfo> menuFunsByUser = getMenuFuncs( preOpts,  ls);
        return formatMenuTree(menuFunsByUser,superOptId);
	}

	@Override
	@Transactional(readOnly = true)
	public UserInfo getUserInfoByUserCode(String userCode) {
		return sysuserdao.getObjectById(userCode);
	}

	@Override
	@Transactional(readOnly = true)
	public UserInfo getUserInfoByLoginName(String loginName) {
		return sysuserdao.getUserByLoginName(loginName);
	}

	@Override
	@Transactional(readOnly = true)
	public IUnitInfo getUnitInfoByUnitCode(String unitCode) {
		return unitInfoDao.getObjectById(unitCode);
	}

	@Override
	@Transactional
	public void changeUserPassword(String userCode, String userPassword) {
		UserInfo user = sysuserdao.getObjectById(userCode);
        user.setUserPin(passwordEncoder.encodePassword(userPassword, user.getUserCode()));
        sysuserdao.saveObject(user);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean checkUserPassword(String userCode, String userPassword) {
		UserInfo user = sysuserdao.getObjectById(userCode);
	    return passwordEncoder.isPasswordValid(user.getUserPin(),
	    			userPassword, user.getUserCode());
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userList'" )
	@Transactional(readOnly = true)
	public List<UserInfo> listAllUsers() {
		return sysuserdao.listObjects();
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitList'")
	@Transactional(readOnly = true)
	public List<UnitInfo> listAllUnits() {
		return unitInfoDao.listObjects();
	}

	@Override
	@Cacheable(value="AllUserUnits",key="'allUserUnits'")
	@Transactional(readOnly = true)
	public List<UserUnit> listAllUserUnits() {
		return userUnitDao.listObjectsAll();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value="UserUnits",key="#userCode")
	public List<UserUnit> listUserUnits(String userCode) {
		List<UserUnit> userUnits = userUnitDao.listUserUnitsByUserCode(userCode);
        if(userUnits!=null){
            for (UserUnit uu : userUnits) {
                if (null == uu) {
                    continue;
                }
                // 设置行政角色等级
                IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", uu.getUserRank());
                if (dd != null && dd.getExtraCode() != null && StringRegularOpt.isNumber(dd.getExtraCode())) {
                    try {
                        uu.setXzRank(Integer.valueOf(dd.getExtraCode()));
                    } catch (Exception e) {
                        uu.setXzRank(CodeRepositoryUtil.MAXXZRANK);
                    }
                 }
            }
        }
        return userUnits;
	}

	@Override
	@Cacheable(value="UnitUsers",key="#unitCode")
	@Transactional(readOnly = true)
	public List<UserUnit> listUnitUsers(String unitCode) {
		List<UserUnit> unitUsers = userUnitDao.listUnitUsersByUnitCode(unitCode);
        if(unitUsers!=null){
            for (UserUnit uu : unitUsers) {
                if (null == uu) {
                    continue;
                }
                // 设置行政角色等级
                IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", uu.getUserRank());
                if (dd != null && dd.getExtraCode() != null && StringRegularOpt.isNumber(dd.getExtraCode())) {
                    try {
                        uu.setXzRank(Integer.valueOf(dd.getExtraCode()));
                    } catch (Exception e) {
                        uu.setXzRank(CodeRepositoryUtil.MAXXZRANK);
                    }
                 }
            }
        }
        return unitUsers;
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value="RoleInfo",key="'roleCodeMap'")
	public Map<String, RoleInfo> getRoleRepo() {
        Map<String, RoleInfo> roleReop = new HashMap<>();
        List<RoleInfo> roleList = roleInfoDao.listObjectsAll();
        if(roleList!=null)
            for (Iterator<RoleInfo> it = roleList.iterator(); it.hasNext(); ) {
                RoleInfo roleinfo = it.next();
                roleReop.put(roleinfo.getRoleCode(), roleinfo);
            }
        return roleReop;
    }
	
	@Override
	@Cacheable(value="OptInfo",key="'optIdMap'")
    @Transactional(readOnly = true)
	public Map<String, OptInfo> getOptInfoRepo() {
        Map<String, OptInfo> optRepo = new HashMap<>();
        List<OptInfo> optList = optInfoDao.listObjectsAll();
        if (optList != null) {
            for (OptInfo optinfo : optList) {
                optRepo.put(optinfo.getOptId(), optinfo);
            }
        }

        return optRepo;
    }

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value="OptInfo",key="'optCodeMap'")
	public Map<String, OptMethod> getOptMethodRepo() {
        Map<String, OptMethod> powerRepo = new HashMap<>();

        List<OptMethod> optdefList = optMethodDao.listObjects();
        if(optdefList!=null){
            for (Iterator<OptMethod> it = optdefList.iterator(); it.hasNext(); ) {
                OptMethod optdef = it.next();
                powerRepo.put(optdef.getOptCode(), optdef);
            }
        }
        return powerRepo;
    }
	
	@Override
	@Cacheable(value = "DataDictionary",key="'CatalogCode'")
    @Transactional(readOnly = true)
	public List<DataCatalog> listAllDataCatalogs() {
		return dataCatalogDao.listObjects();
	}

	@Override
	@Cacheable(value = "DataDictionary",key="#catalogCode")
	@Transactional(readOnly = true)
	public List<DataDictionary> listDataDictionaries(String catalogCode) {
		return dictionaryDao.listDataDictionary(catalogCode);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitCodeMap'")
    @Transactional(readOnly = true)
	public Map<String,UnitInfo> getUnitRepo() {
		Map<String, UnitInfo> unitRepo = new HashMap<>();
        List<UnitInfo> unitList = unitInfoDao.listObjects();
        if (unitList != null){
            for (UnitInfo unitinfo : unitList) {
                unitRepo.put(unitinfo.getUnitCode(), unitinfo);
            }
        }
        /**
         * 计算所有机构的子机构。只计算启动的机构
         */
        for (Map.Entry<String, UnitInfo> ent : unitRepo.entrySet()) {
            UnitInfo u = ent.getValue();
            String sParentUnit = u.getParentUnit();
            if ("T".equals(u.getIsValid())
                    && (sParentUnit != null && (!"".equals(sParentUnit)) && (!"0".equals(sParentUnit)))) {
                UnitInfo pU = unitRepo.get(sParentUnit);
                if (pU != null)
                    pU.getSubUnits().add(u);
            }
        }

        return unitRepo;
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userCodeMap'" )
    @Transactional(readOnly = true)
	public Map<String,UserInfo> getUserRepo() {
		Map<String, UserInfo> userInfoMap = new HashMap<>();
        List<UserInfo> users = sysuserdao.listObjects();
        if(users!=null){
            for (UserInfo userInfo : users) {
                userInfoMap.put(userInfo.getUserCode(), userInfo);
            }
        }
        return userInfoMap;
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'loginNameMap'")
    @Transactional(readOnly = true)
	public Map<String, ? extends IUserInfo> getLoginNameRepo() {
		Map<String, UserInfo> userInfoMap = new HashMap<>();
        List<UserInfo> users = sysuserdao.listObjects();
        if(users!=null){
            for (UserInfo userInfo : users) {
                userInfoMap.put(userInfo.getLoginName(), userInfo);
            }
        }
        return userInfoMap;
	}

	@Override
	@Cacheable(value="UnitInfo",key="'depNoMap'")
    @Transactional(readOnly = true)
	public Map<String, ? extends IUnitInfo> getDepNoRepo() {
		Map<String, UnitInfo> depNo = new HashMap<>();
        List<UnitInfo> unitList = unitInfoDao.listObjects();
        if (unitList != null)
            for (UnitInfo unitinfo : unitList) {
                depNo.put(unitinfo.getDepNo(), unitinfo);
            }
        return depNo;
	}

	
	@Transactional
    private CentitUserDetailsImpl fillUserDetailsField(UserInfo userinfo ){
    	 CentitUserDetailsImpl sysuser = new CentitUserDetailsImpl();
         sysuser.copy(userinfo);
         //sysuser.setSysusrodao(userRoleDao);
         
         
         //List<RoleInfo> roles = userRoleDao.getSysRolesByUserId(sysuser.getUserCode());
         //edit by zhuxw  代码从原框架迁移过来，可和其它地方合并
         List<RoleInfo> roles = new ArrayList<>();
         //所有的用户 都要添加这个角色
         roles.add(new RoleInfo("G-public", "general public","G",
         		"G","T", "general public")); 
         List<FVUserRoles> ls = userRoleDao.getSysRolesByUserId(sysuser.getUserCode());
         if(ls!=null)
         {
        	 
        	 for (FVUserRoles l : ls) {
                 RoleInfo roleInfo = new RoleInfo();

                 BeanUtils.copyProperties(l, roleInfo);
                 roles.add(roleInfo);
             }
         }
        //add  end 
         
         
         
         
         
         List<UserUnit> usun = userUnitDao.listUserUnitsByUserCode(sysuser.getUserCode());
         sysuser.setUserUnits(usun);

         //sysuser.setUserFuncs(functionDao.getMenuFuncByUserID(sysuser.getUserCode()));
         if(roles==null || roles.size()<1){
             sysuser.setIsValid("F");
             return sysuser;
         }
         
         sysuser.setAuthoritiesByRoles(roles);

         List<FVUserOptList> uoptlist = sysuserdao.getAllOptMethodByUser(sysuser.getUserCode());
         Map<String, String> userOptList = new HashMap<String, String>();
         if (uoptlist != null) {
             for (FVUserOptList opt : uoptlist){
                 if(!StringUtils.isBlank(opt.getOptMethod()))
                     userOptList.put(opt.getOptId() + "-" + opt.getOptMethod(), "T");
             }
         }
         // ServletActionContext.getRequest().getSession().setAttribute("userOptList",
         // userOptList);
         sysuser.setUserOptList(userOptList);
         
         List<UserSetting> uss =userSettingDao.getUserSettingsByCode(sysuser.getUserCode());
         if(uss!=null){
             for(UserSetting us :uss)
                 sysuser.putUserSettingsParams(us.getParamCode(), us.getParamValue());
         }
         return sysuser;
    }
   
	@Override
	@Transactional
	public CentitUserDetailsImpl loadUserDetailsByLoginName(String loginName) {
		 UserInfo userinfo = sysuserdao.getUserByLoginName(loginName);
    	 if(userinfo==null)
    		 return null;
         return fillUserDetailsField(userinfo); 
	}

	@Override
	@Transactional
	public CentitUserDetailsImpl loadUserDetailsByUserCode(String userCode) {
		 UserInfo userinfo = sysuserdao.getUserByCode(userCode);
    	 if(userinfo==null)
    		 return null;
         return fillUserDetailsField(userinfo); 
	}

	@Override
	@Transactional
	public CentitUserDetailsImpl loadUserDetailsByRegEmail(String regEmail) {
		UserInfo userinfo = sysuserdao.getUserByRegEmail(regEmail);
	   	if(userinfo==null)
	   		 return null;
        return fillUserDetailsField(userinfo);
	}

	@Override
	@Transactional
	public CentitUserDetailsImpl loadUserDetailsByRegCellPhone(String regCellPhone) {
		UserInfo userinfo = sysuserdao.getUserByRegCellPhone(regCellPhone);
	   	if(userinfo==null)
	   		 return null;
        return fillUserDetailsField(userinfo);
	}

	   private static List<OptInfo> getMenuFuncs(List<OptInfo> preOpts, List<FVUserOptMoudleList> ls) {
	        boolean isNeeds[] = new boolean[preOpts.size()];
	        for (int i = 0; i < preOpts.size(); i++) {
	            isNeeds[i] = false;
	        }
	        List<OptInfo> opts = new ArrayList<OptInfo>();

	        for (FVUserOptMoudleList opm : ls) {
	            OptInfo opt = new OptInfo();
	            opt.setFormCode(opm.getFormcode());
	            opt.setImgIndex(opm.getImgindex());
	            opt.setIsInToolbar(opm.getIsintoolbar());
	            opt.setMsgNo(opm.getMsgno());
	            opt.setMsgPrm(opm.getMsgprm());
	            opt.setOptId(opm.getOptid());
	            opt.setOptType(opm.getOpttype());
	            opt.setOptName(opm.getOptname());
	            opt.setOptUrl(opm.getOpturl());
	            opt.setPreOptId(opm.getPreoptid());
	            opt.setTopOptId(opm.getTopoptid());
	            opt.setPageType(opm.getPageType());
	            opt.setOptRoute(opm.getOptRoute());

	            opts.add(opt);
	            for (int i = 0; i < preOpts.size(); i++) {
	                if (opt.getPreOptId() != null && opt.getPreOptId().equals(preOpts.get(i).getOptId())) {
	                    isNeeds[i] = true;
	                    break;
	                }
	            }
	        }

	        List<OptInfo> needAdd = new ArrayList<OptInfo>();
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
	        // end
//	        ListOpt.sortAsTree(opts, new ListOpt.ParentChild<OptInfo>() {
//				@Override
//				public boolean parentAndChild(OptInfo p, OptInfo c) {
//					return p.getOptId().equals(c.getPreOptId());
//				}
//	        	
//			});
	    }
}
