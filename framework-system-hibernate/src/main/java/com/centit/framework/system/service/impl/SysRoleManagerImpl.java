package com.centit.framework.system.service.impl;

import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.system.dao.OptInfoDao;
import com.centit.framework.system.dao.OptMethodDao;
import com.centit.framework.system.dao.RoleInfoDao;
import com.centit.framework.system.dao.RolePowerDao;
import com.centit.framework.system.po.*;
import com.centit.framework.system.service.SysRoleManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("sysRoleManager")
public class SysRoleManagerImpl extends
        BaseEntityManagerImpl<RoleInfo, String, RoleInfoDao> implements SysRoleManager{

    //private static final Logger logger = LoggerFactory.getLogger(SysRoleManagerImpl.class);
    @Resource
    @NotNull
    private OptInfoDao optInfoDao;

    @Resource
    @NotNull
    private OptMethodDao optMethodDao;
    
    @Resource
    @NotNull
    private RolePowerDao rolePowerDao;

    @Resource(name = "roleInfoDao")
    @NotNull
    @Override
    protected void setBaseDao(RoleInfoDao baseDao) {
        super.baseDao = baseDao;
    }

    @PostConstruct
    public void init() {
        loadRoleSecurityMetadata();
    }

    @Override
    @Transactional(readOnly=true)
    public void loadRoleSecurityMetadata() {
        CentitSecurityMetadata.optMethodRoleMap.clear();
        List<RolePower> rplist = listAllRolePowers();
        if(rplist==null || rplist.size()==0)
        	return;
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
        
        List<OptMethodUrlMap> oulist = listAllOptMethodUrlMap();
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
        //CentitSecurityMetadata.optTreeNode.printTreeNode();
    }
    // 各种角色代码获得该角色的操作权限 1对多
    @Transactional
    public List<RolePower> getRolePowers(String rolecode) {
        return rolePowerDao.listRolePowersByRoleCode(rolecode);
    }

    @Override
    @Transactional
    public List<RolePower> getRolePowersByDefCode(String optCode) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("optCode", optCode);

        return rolePowerDao.listObjects(filterMap);
    }

    @Override
    @CacheEvict(value="RoleInfo",key="'roleCodeMap'")
    @Transactional
    public Serializable saveNewRoleInfo(RoleInfo o){
        Serializable pk = baseDao.saveNewObject(o);
        return pk;
    }    
 
    // 获取菜单TREE
    @Transactional
    public List<VOptTree> getVOptTreeList() {
        return baseDao.getVOptTreeList();
    }

    @Transactional
    public List<RolePower> listAllRolePowers() { 
        return rolePowerDao.listObjects();
    }

    @Transactional
    public List<OptMethod> listAllOptMethods() { 
         return optMethodDao.listObjects();
    }

    @Transactional
    private List<OptMethodUrlMap> listAllOptMethodUrlMap() { 
         return optInfoDao.listAllOptMethodUrlMap();
    }

    @Override
    @CacheEvict(value="RoleInfo",allEntries = true)
    @Transactional
    public void updateRoleInfo(RoleInfo o) {
        super.mergeObject(o);
        List<RolePower> newRPs = o.getRolePowers();
        if(newRPs == null || newRPs.size()<1)
            return;
        
        List<RolePower> rps = rolePowerDao.listRolePowersByRoleCode(o.getRoleCode());
    
        for(RolePower rp : newRPs){
            rp.setRoleCode(o.getRoleCode());
        }
        if( rps != null){           
            for(RolePower rp : rps){
                if(! newRPs.contains(rp)){
                    rolePowerDao.deleteObject(rp);
                }
            }          
        }
        
        for(RolePower rp : newRPs){
            rolePowerDao.mergeObject(rp);            
        }    
    }

    @Override
    @CacheEvict(value="RoleInfo",allEntries = true)
    @Transactional
    public void deleteRoleInfo(String roleCode){
        rolePowerDao.deleteRolePowersByRoleCode(roleCode);
        baseDao.deleteObjectById(roleCode);
        
    }
    
    @Override
    @Transactional
    public RoleInfo getRoleInfo(String roleCode){
        RoleInfo rf = baseDao.getObjectById(roleCode);
        rf.addAllRolePowers( rolePowerDao.listRolePowersByRoleCode(roleCode) );
        return rf;
        
    }
 
    /* (non-Javadoc)
     * @see com.centit.framework.common.SysRoleManager#search(java.lang.String, java.lang.String[])
     */
    @Override
    @Transactional
    public List<RoleInfo> search(String key, String[] field){
        return baseDao.search(key, field);
    }

       @Override
       @Transactional
       public int countRoleUserSum(String roleCode){
           return baseDao.countRoleUserSum(roleCode);
       }
}
