package com.centit.framework.staticsystem.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.model.adapter.PlatformEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.SysParametersUtils;
import com.centit.framework.core.common.ResponseJSON;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.DataCatalog;
import com.centit.framework.staticsystem.po.DataDictionary;
import com.centit.framework.staticsystem.po.OptInfo;
import com.centit.framework.staticsystem.po.OptMethod;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.RolePower;
import com.centit.framework.staticsystem.po.UnitInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.network.HttpExecutor;

/**
 * 集成平台客户端业务配置新, 所有的访问需要添加一个cache策略
 * @author codefan
 *
 */
public class IPClientPlatformEnvironment implements PlatformEnvironment
{
	private String platServerUrl;
	private String topOptId;

	public IPClientPlatformEnvironment() {
		
	}
	
	public void setTopOptId(String topOptId) {
		this.topOptId = topOptId;
	}

	public void setPlatServerUrl(String platServerUrl) {
		this.platServerUrl = platServerUrl;
	}
	
	//初始化  这个要定时刷新
	public void init(){
		if(StringUtils.isBlank(platServerUrl))
			return ;
		reloadSecurityMetadata();
	}
	
	

	@Override
	public String getSystemParameter(String paramCode) {
		return SysParametersUtils.getStringValue(paramCode);
	}

	@Override
	public String getUserSetting(String userCode, String paramCode) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/usersetting/"+userCode+"/"+paramCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsString("paramValue");
	}

	@Override
	public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/usermenu/"+topOptId+"/"+userCode+"?asAdmin="+asAdmin);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(OptInfo.class);
	}

	@Override
	public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId,
			boolean asAdmin) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/usermenu/"+superOptId+"/"+userCode+"?asAdmin="+asAdmin);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(OptInfo.class);
	}

	@Override
	public UserInfo getUserInfoByUserCode(String userCode) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/userinfo/"+userCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsObject(UserInfo.class);
	}


	@Override
	public UnitInfo getUnitInfoByUnitCode(String unitCode){
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/unitinfo/"+unitCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsObject(UnitInfo.class);
	}

	@Override
	public UserInfo getUserInfoByLoginName(String loginName) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/userinfobyloginname/"+loginName);
		if(resJson==null)
			return null;
		return resJson.getDataAsObject(UserInfo.class);
	}

	@Override
	public void changeUserPassword(String userCode, String userPassword) {
		CloseableHttpClient httpclient = HttpExecutor.createHttpClient();
		try {
			Map<String,String> userInfo = new HashMap<String,String>();
			userInfo.put("userCode", userCode);
			userInfo.put("password", userPassword);
			userInfo.put("newPassword", userPassword);
			HttpExecutor.jsonPost(httpclient, 
					platServerUrl + "/changepassword/"+userCode, 
					JSON.toJSONString(userInfo), true);
		} catch (IOException e) {
		}
		try {
			httpclient.close();
		}catch (IOException e) {
		}
	}

	@Override
	public boolean checkUserPassword(String userCode, String userPassword) {
		boolean ret = false;
		CloseableHttpClient httpclient = HttpExecutor.createHttpClient();
		try {
			Map<String,String> userInfo = new HashMap<String,String>();
			userInfo.put("userCode", userCode);
			userInfo.put("password", userPassword);
			userInfo.put("newPassword", userPassword);
			String sret = HttpExecutor.jsonPost(httpclient, 
					platServerUrl + "/checkpassword/"+userCode, 
					JSON.toJSONString(userInfo), true);
			ret = StringRegularOpt.isTrue(sret);
		} catch (IOException e) {
		}
		try {
			httpclient.close();
		}catch (IOException e) {
		}
		return ret;
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userList'" )
	public List<UserInfo> listAllUsers() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/allusers/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(UserInfo.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitList'")
	public List<UnitInfo> listAllUnits() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/allunits/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(UnitInfo.class);
	}

	@Override
	@Cacheable(value="AllUserUnits",key="'allUserUnits'")
	public List<UserUnit> listAllUserUnits() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/alluserunits/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(UserUnit.class);
	}

	@Override
	@Cacheable(value="UserUnits",key="#userCode")
	public List<UserUnit> listUserUnits(String userCode) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/userunits/"+topOptId+"/"+userCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(UserUnit.class);
	}

	@Override
	@Cacheable(value="UnitUsers",key="#unitCode")
	public List<UserUnit> listUnitUsers(String unitCode) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/unitusers/"+topOptId+"/"+unitCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(UserUnit.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'unitCodeMap'")
	public Map<String, UnitInfo> getUnitRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/unitrepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UnitInfo.class);
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'userCodeMap'" )
	public Map<String, UserInfo> getUserRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/userrepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UserInfo.class);
	}

	@Override
	@Cacheable(value = "UserInfo",key = "'loginNameMap'")
	public Map<String, UserInfo> getLoginNameRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/loginnamerepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UserInfo.class);
	}

	@Override
	@Cacheable(value="UnitInfo",key="'depNoMap'")
	public Map<String, UnitInfo> getDepNoRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/depnorepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(UnitInfo.class);
	}

	@Override
	@Cacheable(value="RoleInfo",key="'roleCodeMap'")
	public Map<String, RoleInfo> getRoleRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/rolerepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(RoleInfo.class);
	}

	@Override
	@Cacheable(value="OptInfo",key="'optIdMap'")
	public Map<String, OptInfo> getOptInfoRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/optinforepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(OptInfo.class);
	}

	@Override
	@Cacheable(value="OptInfo",key="'optCodeMap'")
	public Map<String, OptMethod> getOptMethodRepo() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/optmethodrepo/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsMap(OptMethod.class);
	}

	@Override
	@Cacheable(value = "DataDictionary",key="'CatalogCode'")
	public List<DataCatalog> listAllDataCatalogs() {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/catalogs/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(DataCatalog.class);
	}

	@Override
	@Cacheable(value = "DataDictionary",key="#catalogCode")
	public List<DataDictionary> listDataDictionaries(String catalogCode) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/dictionary/"+topOptId+"/"+catalogCode);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(DataDictionary.class);
	}

	
	public List<RolePower>  listAllRolePower(){
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/allrolepowers/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(RolePower.class);
	}
	
	public List<OptMethod> listAllOptMethod(){
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/alloptmethods/"+topOptId);
		if(resJson==null)
			return null;
		return resJson.getDataAsArray(OptMethod.class);
	}


	private CentitUserDetails loadUserDetails(String queryParam,String qtype) {
		ResponseJSON resJson = RestfulHttpRequest.getResponseData(
				platServerUrl + "/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype);
		
		if(resJson==null || resJson.getCode()!=0){
			return null;
		}
		UserInfo userInfo =  resJson.getDataAsObject("userInfo",UserInfo.class);
		if(userInfo==null)
			return null;
		
		List<String> userRoles = resJson.getDataAsArray("userRoles",String.class);
		List<UserUnit> userUnits = resJson.getDataAsArray("userUnits",UserUnit.class);
		userInfo.setUserUnits(userUnits);
		userInfo.setAuthoritiesByRoles(userRoles);
		return userInfo;
	}
	
	@Override
	public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
		return loadUserDetails(loginName,"loginName");
	}

	@Override
	public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
		return loadUserDetails(userCode,"userCode");
	}

	@Override
	public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
		return loadUserDetails(regEmail,"regEmail");
	}

	@Override
	public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
		return loadUserDetails(regCellPhone,"regCellPhone");
	}
	
	@Override
	@CacheEvict(value ={
			 "DataDictionary","OptInfo","RoleInfo","UserInfo","UnitInfo",
			 "UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
	public boolean reloadDictionary() {
		return true;
	}

	@Override
	public boolean reloadSecurityMetadata() {
		//这个要定时刷新 或者 通过集成平台来主动刷新
		CentitSecurityMetadata.optMethodRoleMap.clear();
        List<RolePower> rplist = listAllRolePower();
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
        Map<String, OptInfo> optRepo = getOptInfoRepo();
        List<OptMethod> oulist = listAllOptMethod();
        CentitSecurityMetadata.optTreeNode.setChildList(null);
        CentitSecurityMetadata.optTreeNode.setOptCode(null);
        for(OptMethod ou:oulist){
        	OptInfo oi = optRepo.get(ou.getOptId());
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
        //CentitSecurityMetadata.optTreeNode.printTreeNode();
		return true;
	}

}
