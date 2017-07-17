package com.centit.framework.system.service.impl;

import com.centit.framework.core.common.ObjectException;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.system.dao.UnitInfoDao;
import com.centit.framework.system.dao.UserInfoDao;
import com.centit.framework.system.dao.UserRoleDao;
import com.centit.framework.system.dao.UserUnitDao;
import com.centit.framework.system.po.*;
import com.centit.framework.system.service.SysUserManager;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service("sysUserManager")
@Transactional
public class SysUserManagerImpl extends 
    BaseEntityManagerImpl<UserInfo, String, UserInfoDao> 
    implements SysUserManager {
    //private static final Logger logger = LoggerFactory.getLogger(SysUserManagerImpl.class);
    // 加密
    @Resource
    @NotNull
    private CentitPasswordEncoder passwordEncoder;

    @Resource
    @NotNull
    private UserUnitDao userUnitDao;

    @Resource
    @NotNull
    private UnitInfoDao unitInfoDao;

    @Resource
    @NotNull
    private UserRoleDao userRoleDao;

    @Override
    @Resource(name = "userInfoDao")
    protected void setBaseDao(UserInfoDao baseDao) {
        super.baseDao = baseDao;
    }

    private String getDefaultPassword(String userCode) {
        //final String defaultPassword = "000000";
        return passwordEncoder.createPassword("000000", userCode);
    }

    @Override
    public List<RoleInfo> listUserValidRoles(String userCode) {
        List<RoleInfo> roles = userRoleDao.getSysRolesByUserId(userCode);
        return roles;
    }

    public List<UserRole> listUserValidRoles(String userCode, String rolePrefix) {
        return userRoleDao.getUserRolesByUserId(userCode, rolePrefix);
    }

    public List<UserRole> listUserAllRoles(String userCode, String rolePrefix) {
        return userRoleDao.getAllUserRolesByUserId(userCode, rolePrefix);
    }

    @Override
    public void resetPwd(String userCode) {
        UserInfo user = baseDao.getObjectById(userCode);
        user.setUserPin(getDefaultPassword(user.getUserCode()));
        baseDao.saveObject(user);
    }

    @Override
    public void resetPwd(String[] userCodes) {
        for (String userCode : userCodes) {
            resetPwd(userCode);
        }
    }
    @Override
    public boolean checkUserPassword(String userCode, String oldPassword) {
        UserInfo user = baseDao.getObjectById(userCode);
        return passwordEncoder.isPasswordValid(user.getUserPin(),oldPassword,user.getUserCode());
    }
    /**
     * 用户修改密码
     * @param userCode
     * @param oldPassword 旧密码，前段已经处理过
     * @param newPassword   新密码 前段也已经处理过
     */
    @Override
    public void setNewPassword(String userCode, String oldPassword, String newPassword) {
        UserInfo user = baseDao.getObjectById(userCode);
        if (!passwordEncoder.isPasswordValid(user.getUserPin(),oldPassword,user.getUserCode() ))
            throw new ObjectException("旧密码不正确！");
        
        if (user.getUserPin().equals(passwordEncoder.encodePassword(newPassword, user.getUserCode())))
            throw new ObjectException("新密码和旧密码一致，请重新输入新密码！");

        user.setUserPin(passwordEncoder.encodePassword(newPassword, user.getUserCode()));
        baseDao.saveObject(user);
    }

    /**
     * f服务端强制设置用户密码，密码没有在前段处理过
     * @param userCode
     * @param newPassword
     */
    @Override
    public void forceSetPassword(String userCode, String newPassword){
        UserInfo user = baseDao.getObjectById(userCode);
        user.setUserPin(passwordEncoder.createPassword(newPassword, user.getUserCode()));
        baseDao.saveObject(user);
    }

    @Override
    @CacheEvict(value = "UserInfo",allEntries = true)
    public void mergeObject(UserInfo sysuser) {
        if (StringUtils.isBlank(sysuser.getUserCode())) {// 新添
            boolean hasExist = baseDao.checkIfUserExists(sysuser);// 查该登录名是不是已经被其他用户使
            if (hasExist) {
                throw new ObjectException("当前登录名已存在");
            }
            sysuser.setUserCode(getNextUserCode());
//            sysuser.setIsValid("T");
            sysuser.setUserPin(getDefaultPassword(sysuser.getUserCode()));
        }

        if (StringUtils.isBlank(sysuser.getUserPin())) {
            sysuser.setUserPin(getDefaultPassword(sysuser.getUserCode()));
        }
        sysuser.setUserWord(StringBaseOpt.getFirstLetter(sysuser.getUserName()));
        super.mergeObject(sysuser);
    }

    @CacheEvict(value = "UserInfo",allEntries = true)
    public void saveObject(UserInfo sysuser) {
        boolean hasExist = baseDao.checkIfUserExists(sysuser);// 查该登录名是不是已经被其他用户使

        if (StringUtils.isBlank(sysuser.getUserCode())) {// 新添
            // sysuser.setUsercode( getNextUserCode('u'));
            sysuser.setIsValid("T");
            sysuser.setUserPin(getDefaultPassword(sysuser.getUserCode()));
        }
        if (!hasExist && StringUtils.isBlank(sysuser.getUserPin()))
            sysuser.setUserPin(getDefaultPassword(sysuser.getUserCode()));
        sysuser.setUserWord(StringBaseOpt.getFirstLetter(sysuser.getUserName()));
        baseDao.saveObject(sysuser);
    }
   
    
    
    @Override
    @CacheEvict(value ={"UserInfo","UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public void saveNewUserInfo(UserInfo userInfo){
        baseDao.saveNewObject(userInfo);
        if(null!=userInfo.getUserUnits()){
            for(UserUnit uu:userInfo.getUserUnits()){
                userUnitDao.saveNewObject(uu);
            }
        }
        if(null!=userInfo.listUserRoles()){
            for(UserRole ur:userInfo.listUserRoles()){
                userRoleDao.saveNewObject(ur);
            }
        }
    }

    
    @Override
    @CacheEvict(value ={"UserInfo","UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public void updateUserInfo(UserInfo userinfo){
        
        baseDao.mergeObject(userinfo);
        
        /*List<UserUnit> oldUserUnits = userUnitDao.listUserUnitsByUserCode(userinfo.getUserCode());
         if(oldUserUnits!=null){
            for(UserUnit uu: oldUserUnits ){
                if(null ==userinfo.getUserUnits() ||
                        ! userinfo.getUserUnits().contains(uu)){
                    userUnitDao.deleteObject(uu);
                }
            }
        }
        
        if(userinfo.getUserUnits() !=null){
            for(UserUnit uu: userinfo.getUserUnits() ){
                 userUnitDao.mergeObject(uu);
            }
        }
        
        */
    }
    
    @Override
    @CacheEvict(value ="UserInfo",allEntries = true)
    public void updateUserProperities(UserInfo userinfo){
        baseDao.mergeObject(userinfo);
    }
    
    @Override
    @CacheEvict(value ={"UserInfo","UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public void deleteUserInfo(String userCode){       
        userUnitDao.deleteUserUnitByUser(userCode);
        userRoleDao.deleteByUserId(userCode);        
        baseDao.deleteObjectById(userCode);        
    }
    

    public String getNextUserCode() {
        return baseDao.getNextKey();
    }

    @Override
    public List<UserInfo> search(String key, String[] field) {
        return baseDao.search(key, field);
    }

    @Override
    public UserInfo loadUserByLoginname(String userCode){
        return baseDao.getUserByLoginName(userCode);
    }
    @Override
    public List<FVUserOptList> getAllOptMethodByUser(String userCode){
        return baseDao.getAllOptMethodByUser(userCode);
    }
    @Transactional
    @Override
    public boolean isLoginNameExist(String userCode, String loginName){
        String sql = "select count(*) as usersCount from F_USERINFO t " +
                "where t.USERCODE <> ? and t.LOGINNAME = ?";
        Object obj  = DatabaseOptUtils.getSingleObjectBySql(baseDao, sql,
                new Object[]{userCode, loginName} );
        Long uc =NumberBaseOpt.castObjectToLong(obj);
        return (uc!=null && uc>0);
    }

    @Transactional
    @Override
    public boolean isCellPhoneExist(String userCode, String regPhone){
        String sql = "select count(*) as usersCount from F_USERINFO t " +
                "where t.USERCODE <> ? and t.REGCELLPHONE = ?";
        Object obj  = DatabaseOptUtils.getSingleObjectBySql(baseDao, sql,
                new Object[]{userCode, regPhone} );
        Long uc =NumberBaseOpt.castObjectToLong(obj);
        return (uc!=null && uc>0);
    }

    @Transactional
    @Override
    public boolean isEmailExist(String userCode,String regEmail){
        String sql = "select count(*) as usersCount from F_USERINFO t " +
                "where t.USERCODE <> ? and t.REGEMAIL = ?";
        Object obj  = DatabaseOptUtils.getSingleObjectBySql(baseDao, sql,
                new Object[]{userCode, regEmail} );
        Long uc =NumberBaseOpt.castObjectToLong(obj);
        return (uc!=null && uc>0);
    }


    @Transactional
	@Override
	public boolean isAnyOneExist(String userCode, String loginName,String regPhone,String regEmail){
		String sql = "select count(*) as usersCount from F_USERINFO t " +
                "where t.USERCODE != ? and " +
                "(t.LOGINNAME = ? or t.REGCELLPHONE= ? or t.REGEMAIL = ?)";
		Object obj  = DatabaseOptUtils.getSingleObjectBySql(baseDao, sql,
                new Object[]{StringUtils.isBlank(userCode)?"null":userCode,
                        StringUtils.isBlank(loginName)?"null":loginName,
                        StringUtils.isBlank(regPhone)?"null":regPhone,
                        StringUtils.isBlank(regEmail)?"null":regEmail} );
		Long uc =NumberBaseOpt.castObjectToLong(obj);
        return (uc!=null && uc>0);
	}
}
