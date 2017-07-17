package com.centit.framework.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.UserInfo;

@Repository
public interface UserInfoDao{
	
	void mergeObject(UserInfo userInfo);
	
	void saveNewObject(UserInfo userInfo);
	
	void deleteObjectById(String userCode);
	
	List<UserInfo> listObjects();
	
	List<UserInfo> listObjects(Map<String, Object> filterMap);
	
	
    int  pageCount(Map<String, Object> filterDescMap);
    List<UserInfo>  pageQuery(Map<String, Object> pageQureyMap);
	
	
	UserInfo getObjectById(String userCode);
	
    /**
     * 这个方法迁移到 ManagerImpl类中
     * hql = "SELECT COUNT(*) FROM UserInfo WHERE userCode = " + QueryUtils.buildStringForQuery(user.getUserCode());
     * hql = "SELECT COUNT(*) FROM UserInfo WHERE loginName = " + QueryUtils.buildStringForQuery(user.getLoginName());
     * 			" AND userCode <> " + QueryUtils.buildStringForQuery(user.getUserCode());
     * 
     *  放到impl中去了
     * @param map
     * @return
     */
    //boolean checkIfUserExists(UserInfo user);
    
	 int checkIfUserExists(Map map);
	
    //"U"+ DatabaseOptUtils.getNextKeyBySequence(this, "S_USERCODE", 7);
    String getNextKey();

    //设置主键 o.setUserCode(this.getNextKey()); 和初始密码
    void saveObject(UserInfo o);
    
    //hql = "FROM FVUserOptList urv where urv.id.userCode=?";
    List<FVUserOptList> getAllOptMethodByUser(String userCode);
	/*
     * FUserinfo loginUser(String userName, String password) { return
	 * (FUserinfo) getHibernateTemplate().find(
	 * "FROM FUserinfo WHERE username = ? AND userpin = ? ", new Object[] {
	 * userName, password }).get(0); }
	 */
   
    //return this.listObjects(filterMap);
    List<UserInfo> listUnderUnit(Map<String, Object> filterMap);

    // return this.listObjects(filterMap, pageDesc);
    //List<UserInfo> listUnderUnit(Map<String, Object> filterMap, PageDesc pageDesc);
    
    //return getObjectById(userCode);
    UserInfo getUserByCode(String userCode);
    
    
    // return super.getObjectByProperty("loginName",loginName.toLowerCase());
    UserInfo getUserByLoginName(String loginName);
    
    // return super.getObjectByProperty("regEmail", regEmail);
    UserInfo getUserByRegEmail(String regEmail);
    
    //return super.getObjectByProperty("regCellPhone", regCellPhone);
    UserInfo getUserByRegCellPhone(String regCellPhone);
    
    //return super.getObjectByProperty("userTag", userTag);
    UserInfo getUserByTag(String userTag);
    
    //return super.getObjectByProperty("userWord", userWord);
    UserInfo getUserByWord(String userWord);
    
  
    List<UserInfo> listUserinfoByUsercodes(List<String> userCodes);

  
    List<UserInfo> listUserinfoByLoginname(List<String> loginnames);   
    
    //add by zhuxw
    void restPwd(UserInfo user);
    
}
