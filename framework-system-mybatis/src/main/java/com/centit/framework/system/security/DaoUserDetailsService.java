package com.centit.framework.system.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserRoleDao;
import com.centit.framework.system.dao.UserSettingDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.FVUserRoles;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserUnit;

@Service("centitUserDetailsService")
public class DaoUserDetailsService 
	implements CentitUserDetailsService,UserDetailsService,
		AuthenticationUserDetailsService<Authentication> {

    @Resource
    private UserInfoDao sysuserdao;

    @Resource
    private UnitInfoDao sysunitdao;
    
    @Resource
    private UserUnitDao userUnitdao;

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private UserSettingDao userSettingDao;
    
    @Transactional
    public Collection<GrantedAuthority> loadUserAuthorities(String loginname) throws UsernameNotFoundException {
        UserInfo userinfo = sysuserdao.getUserByLoginName(loginname);
        if(userinfo==null)
        	throw new UsernameNotFoundException("user '" + loginname + "' not found...");
        CentitUserDetailsImpl sysuser = new CentitUserDetailsImpl();
        sysuser.copy(userinfo);
        //sysuser.setSysusrodao(userRoleDao);
       // List<RoleInfo> roles = userRoleDao.getSysRolesByUserId(sysuser.getUserCode());
        
        
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
        
        
        sysuser.setAuthoritiesByRoles(roles);
        return sysuser.getAuthorities();
    }

    @Transactional
    private CentitUserDetails fillUserDetailsField(UserInfo userinfo ){
    	 CentitUserDetailsImpl sysuser = new CentitUserDetailsImpl();
         sysuser.copy(userinfo);
         //sysuser.setSysusrodao(userRoleDao);
        // List<RoleInfo> roles = userRoleDao.getSysRolesByUserId(sysuser.getUserCode());
         
         
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
         
         
         List<UserUnit> usun = userUnitdao.listUserUnitsByUserCode(sysuser.getUserCode());
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
    public CentitUserDetails loadUserByUsername(String loginname) throws UsernameNotFoundException {
    	CentitUserDetails ud = loadDetailsByLoginName(loginname);
	    if(ud==null)
			throw new UsernameNotFoundException("user: "+ loginname + " not found!");
	    return ud;
    }

    @Override
    @Transactional
    public CentitUserDetails loadDetailsByLoginName(String loginname) {
    	 UserInfo userinfo = sysuserdao.getUserByLoginName(loginname);
    	 if(userinfo==null)
    		 return null;
         return fillUserDetailsField(userinfo); 
    }

    @Override
    @Transactional
    public CentitUserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
        return loadUserByUsername(token.getName());       
    }

    
    
	@Override
	@Transactional
	public void saveUserSetting(String userCode, String paramCode,String paramValue,
			String paramClass, String paramName) {
		
		userSettingDao.saveUserSetting(new UserSetting(userCode, paramCode, paramValue,
				 paramClass,  paramName));
	}

	@Override
	@Transactional
	public CentitUserDetails loadDetailsByUserCode(String userCode) {
		UserInfo userinfo = sysuserdao.getUserByCode(userCode);
		if(userinfo==null)
   			return null;
        return fillUserDetailsField(userinfo);
	}

	@Override
	@Transactional
	public CentitUserDetails loadDetailsByRegEmail(String regEmail) {
		UserInfo userinfo = sysuserdao.getUserByRegEmail(regEmail);
		if(userinfo==null)
   			return null;
        return fillUserDetailsField(userinfo);
	}

	@Override
	@Transactional
	public CentitUserDetails loadDetailsByRegCellPhone(String regCellPhone) {
		UserInfo userinfo = sysuserdao.getUserByRegCellPhone(regCellPhone);
		if(userinfo==null)
   			return null;
        return fillUserDetailsField(userinfo);
	}

}
