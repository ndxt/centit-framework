package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.system.po.OptInfo;

public interface OptInfoManager{

	 List<OptInfo> listObjects();
	
	 List<OptInfo> listObjects(Map<String, Object> filterMap);
	
	 OptInfo getObjectById(String optId);
	
     boolean hasChildren(String optId);
    /**
     * 获取角色菜单中需要权限控制的业务菜单
     * @return 角色菜单中需要权限控制的业务菜单
     */
     
     List<OptInfo> listItemPowerOpts();
   
     List<OptInfo> listSysAndOptPowerOpts();


     Map<String, OptInfo> listObjectToOptRepo();

    /**
     * 获取用户数据权限过滤器
     * @param sUserCode sUserCode
     * @param sOptid 业务名称
     * @param sOptMethod 对应的方法名称
     * @return 过滤条件列表，null或者空位不过来
     */
     List<String> listUserDataFiltersByOptIDAndMethod(String sUserCode, String sOptid, String sOptMethod);
    /**
     * 将数据转换为树形结构
     *
     * @param optInfos optInfos
     * @param  fillDefAndScope boolean
     * @return 将数据转换为树形结构
     */
     List<OptInfo> listObjectFormatTree(List<OptInfo> optInfos,boolean fillDefAndScope);
    /**
     * 获取某一个机构的权限列表供定义机构角色使用
     * @param sUnitCode sUnitCode
     * @return 某一个机构的权限列表供定义机构角色使用
     */
     List<OptInfo> listOptWithPowerUnderUnit(String sUnitCode);
    
     void saveNewOptInfo(OptInfo optinfo);
    
     void updateOptInfoProperties(OptInfo optinfo);
    
     void updateOptInfo(OptInfo optinfo);
    
     void deleteOptInfo(OptInfo optinfo);
    
     void deleteOptInfoById(String optId);
    
    // void deleteOptInfoByIdCascade(String optId);
    
     OptInfo getOptInfoById(String optId);

	 List<OptInfo> getFunctionsByRoleCode(String roleCode);
    
}
