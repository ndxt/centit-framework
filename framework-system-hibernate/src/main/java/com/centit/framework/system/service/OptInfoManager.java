package com.centit.framework.system.service;

import java.util.List;
import java.util.Map;

import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptInfo;

public interface OptInfoManager extends BaseEntityManager<OptInfo, String> {

    public boolean hasChildren(String optId);
    /**
     * 获取角色菜单中需要权限控制的业务菜单
     * @return
     */
     
    public List<OptInfo> listItemPowerOpts();
   
    public List<OptInfo> listSysAndOptPowerOpts();


    public Map<String, OptInfo> listObjectToOptRepo();

    /**
     * 获取用户数据权限过滤器
     * @param sUserCode
     * @param sOptid 业务名称
     * @param sOptMethod 对应的方法名称
     * @return 过滤条件列表，null或者空位不过来
     */
    public List<String> listUserDataFiltersByOptIDAndMethod(String sUserCode, String sOptid, String sOptMethod);
    /**
     * 将数据转换为树形结构
     *
     * @param optInfos
     * @return
     */
    public List<OptInfo> listObjectFormatTree(List<OptInfo> optInfos,boolean fillDefAndScope);
    /**
     * 获取某一个机构的权限列表供定义机构角色使用
     * @param sUnitCode
     * @return
     */
    public List<OptInfo> listOptWithPowerUnderUnit(String sUnitCode);
    
    public void saveNewOptInfo(OptInfo optinfo);
    
    public void updateOptInfoProperties(OptInfo optinfo);
    
    public void updateOptInfo(OptInfo optinfo);
    
    public void deleteOptInfo(OptInfo optinfo);
    
    public void deleteOptInfoById(String optId);
    
    //public void deleteOptInfoByIdCascade(String optId);
    
    public OptInfo getOptInfoById(String optId);

	public List<OptInfo> getFunctionsByRoleCode(String roleCode);
    
}
