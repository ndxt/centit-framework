package com.centit.framework.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.hibernate.service.BaseEntityManagerImpl;
import com.centit.framework.system.dao.OptDataScopeDao;
import com.centit.framework.system.dao.OptInfoDao;
import com.centit.framework.system.dao.OptMethodDao;
import com.centit.framework.system.dao.RolePowerDao;
import com.centit.framework.system.po.OptDataScope;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.service.OptInfoManager;

@Service("functionManager")
public class OptInfoManagerImpl extends BaseEntityManagerImpl<OptInfo, String, OptInfoDao> 
implements OptInfoManager {
 
    @Resource(name = "optInfoDao")
    @NotNull
    @Override
    protected void setBaseDao(OptInfoDao baseDao) {
        super.baseDao = baseDao;
    }

    @Resource(name = "optMethodDao")
    @NotNull
    protected OptMethodDao optMethodDao;
    
    
    @Resource(name = "optDataScopeDao")
    @NotNull
    protected OptDataScopeDao dataScopeDao;
    
    @Resource(name = "rolePowerDao")
    @NotNull
    protected RolePowerDao rolePowerDao;
    
 
    @Override
    @Transactional(readOnly = true)
    public Map<String, OptInfo> listObjectToOptRepo() {
        Map<String, OptInfo> optRepo = new HashMap<>();
        List<OptInfo> optList = listObjects();
        if (optList != null) {
            for (OptInfo optinfo : optList) {
                optRepo.put(optinfo.getOptId(), optinfo);
            }
        }

        return optRepo;
    }
    
    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    @Transactional
    public void updateOptInfoProperties(OptInfo optinfo){
        baseDao.mergeObject(optinfo);
    }
    
    @Override
    @Transactional
    public boolean hasChildren(String optId){
    	return DatabaseOptUtils.getSingleIntByHql(baseDao,
    			"select count(1) as hasChildren from OptInfo where preOptId = ?",optId) > 0;
    }

    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    @Transactional
    public void saveNewOptInfo(OptInfo optInfo){        
        // 父级url必须设成...    	
        OptInfo parentOpt = baseDao.getObjectById(optInfo.getPreOptId());
        if (null != parentOpt) {
        	if(!"...".equals(parentOpt.getOptRoute())){
	            parentOpt.setOptRoute("...");
	            baseDao.mergeObject(parentOpt);
        	}
        }else{
        	optInfo.setPreOptId("0");
        }
        
        baseDao.saveNewObject( optInfo );
        
        if(optInfo.getOptMethods()!=null && optInfo.getOptMethods().size()>0 ){
            // 对于显示的菜单添加显示权限
            for(OptMethod o : optInfo.getOptMethods()){
                o.setOptCode(optMethodDao.getNextOptCode());
                o.setOptId(optInfo.getOptId());
                optMethodDao.saveNewObject(o);
            }
        }else if (!"W".equals(optInfo.getOptType())) {            
            OptMethod createDef = new OptMethod();
            createDef.setOptCode(optMethodDao.getNextOptCode());
            createDef.setOptId(optInfo.getOptId());
            createDef.setOptName("新建");
            createDef.setOptUrl("/");
            createDef.setOptReq("C");
            createDef.setOptDesc("新建（系统默认）");            
            optMethodDao.saveNewObject(createDef);
            
            OptMethod updateDef = new OptMethod();
            updateDef.setOptCode(optMethodDao.getNextOptCode());
            updateDef.setOptId(optInfo.getOptId());
            updateDef.setOptName("编辑");
            updateDef.setOptUrl("/*");
            updateDef.setOptReq("U");
            updateDef.setOptDesc("编辑（系统默认）");            
            optMethodDao.saveNewObject(updateDef);
            
            OptMethod deleteDef = new OptMethod();
            deleteDef.setOptCode(optMethodDao.getNextOptCode());
            deleteDef.setOptId(optInfo.getOptId());
            deleteDef.setOptName("删除");
            deleteDef.setOptUrl("/*");
            deleteDef.setOptReq("D");
            deleteDef.setOptDesc("删除（系统默认）");            
            optMethodDao.saveNewObject(deleteDef);
        }
    }
    
    
    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    @Transactional
    public void updateOptInfo(OptInfo optInfo) {       
        
        baseDao.mergeObject(optInfo);
        
        List<OptMethod>  newOpts = optInfo.getOptMethods();
        
        if(newOpts.size()>0 ){
            // 对于显示的菜单添加显示权限
            for(OptMethod o : newOpts){
                if(StringUtils.isBlank(o.getOptCode())){
                    o.setOptCode(optMethodDao.getNextOptCode());
                }
                o.setOptId(optInfo.getOptId());
            }                
        }
        
        List<OptMethod> oldOpts = optMethodDao.listOptMethodByOptID(optInfo.getOptId());
        
        if( oldOpts != null){           
            for(OptMethod o : oldOpts){
                if(! newOpts.contains(o)){
                    optMethodDao.deleteObject(o);
                    rolePowerDao.deleteRolePowersByOptCode(o.getOptCode());
                }
            }          
        }
        
        for(OptMethod o : newOpts){
            optMethodDao.mergeObject(o);            
        }
        
        List<OptDataScope>  newDataScopes = optInfo.getDataScopes();
        if(newDataScopes.size()>0 ){
            // 对于显示的菜单添加显示权限
            for(OptDataScope s : newDataScopes){
                if(StringUtils.isBlank(s.getOptScopeCode())){
                    s.setOptScopeCode(dataScopeDao.getNextOptCode());
                }
                s.setOptId(optInfo.getOptId());
            }                
        }
        
        List<OptDataScope> oldDataScopes = dataScopeDao.getDataScopeByOptID(optInfo.getOptId());
        
        if( oldDataScopes != null){           
            for(OptDataScope s : oldDataScopes){
                if(! newDataScopes.contains(s)){
                	dataScopeDao.deleteObject(s);
                }
            }          
        }
        
        for(OptDataScope s : newDataScopes){
        	dataScopeDao.mergeObject(s);            
        }
    }
    
    @Transactional
    public OptInfo getOptInfoById(String optId){
        OptInfo oinfo = baseDao.getObjectById(optId);
        if(oinfo!=null){
	        oinfo.addAllOptMethods(optMethodDao.listOptMethodByOptID(optId) );
	        oinfo.addAllDataScopes(dataScopeDao.getDataScopeByOptID(optId));
        }
        return oinfo;
    }

    @Override
    @CacheEvict(value="OptInfo",allEntries = true)
    @Transactional
    public void deleteOptInfoById(String optId) {
    	dataScopeDao.deleteDataScopeOfOptID(optId);
        optMethodDao.deleteOptMethodsByOptID(optId);
        baseDao.deleteObjectById(optId);
    }
    
    @Override
    @Transactional
    public void deleteOptInfo(OptInfo optinfo){
        deleteOptInfoById(optinfo.getOptId());
    }
    
    @Override
    @Transactional
    public List<OptInfo> listSysAndOptPowerOpts(){
        return baseDao.listObjects("From OptInfo where (optType='S' or optType='O')");
    }
    
    @Override
    @Transactional
    public List<OptInfo> listItemPowerOpts(){
        return baseDao.listObjects("From OptInfo where optType='I'");
    }
    
    /**
     * 获取用户数据权限过滤器
     * @param sUserCode sUserCode
     * @param sOptid 业务名称
     * @param sOptMethod 对应的方法名称
     * @return 过滤条件列表，null或者空位不过来
     */
    @Override
    @Transactional
    public List<String> listUserDataFiltersByOptIDAndMethod(String sUserCode, String sOptid, String sOptMethod){
    	
    	List<String> dataScopes = baseDao.listUserDataPowerByOptMethod( sUserCode,  sOptid,  sOptMethod);
    	if(dataScopes==null || dataScopes.size()==0)
    		return null;
    	
    	Set<String> scopeCodes = new HashSet<String>();
    	for(String scopes : dataScopes){
    		if(scopes==null || "null".equalsIgnoreCase(scopes) || "all".equalsIgnoreCase(scopes))
    			return null;
    		String [] codes = scopes.split(",");
    		for (String code : codes) {
    			if(code!=null && !"".equals(code.trim()))
    				scopeCodes.add(code.trim());
			}
    	}
    	if(scopeCodes.size()==0)
    		return null;
    	return dataScopeDao.listDataFiltersByIds(scopeCodes);
    }

   
    @Override
    @Transactional
    public List<OptInfo> listObjectFormatTree(List<OptInfo> optInfos,boolean fillDefAndScope) {
        // 获取当前菜单的子菜单
        Iterator<OptInfo> menus = optInfos.iterator();
        List<OptInfo> parentMenu = new ArrayList<OptInfo>();
        while (menus.hasNext()) {
            
            OptInfo optInfo = menus.next();
            //去掉级联关系后需要手动维护这个属性
            if(fillDefAndScope){
	            optInfo.addAllOptMethods( optMethodDao.listOptMethodByOptID(optInfo.getOptId()));
	            optInfo.addAllDataScopes( dataScopeDao.getDataScopeByOptID(optInfo.getOptId()));
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
        return parentMenu;
    }
    
    @Override
    @Transactional
    public List<OptInfo> listOptWithPowerUnderUnit(String sUnitCode) {
    	List<OptInfo>  allOpts = baseDao.listObjects("From OptInfo where (optType='S' or optType='O')");
    	List<OptMethod> optDefs = optMethodDao.listOptMethodByRoleCode("G$"+sUnitCode);
    	Set<OptInfo> roleOpts = new HashSet<OptInfo>();    	
    	
    	for(OptInfo optInfo : allOpts) {
            //去掉级联关系后需要手动维护这个属性
    		for(OptMethod def: optDefs){
    			if(optInfo.getOptId().equals(def.getOptId()))
    				optInfo.addOptMethod(def);
    		}
    		if(optInfo.getOptMethods().size()>0){
    			optInfo.addAllDataScopes( dataScopeDao.getDataScopeByOptID(optInfo.getOptId()));
    			roleOpts.add(optInfo);
    		}
    	}
    	Set<OptInfo> preParents  = new HashSet<OptInfo>();
    	preParents.addAll(roleOpts);
    	while(true){
    		int parents= 0;    		
    		Set<OptInfo> parentMenu = new HashSet<OptInfo>();
    		for(OptInfo optInfo :preParents){
                for (OptInfo opt : allOpts) {
                    if (opt.getOptId().equals(optInfo.getPreOptId())) {
                        //opt.getChildren().add(optInfo);
                        parentMenu.add(opt);
                        roleOpts.add(opt);
                        parents ++;
                        break;
                    }
                }    			
    		}    		
    		if(parents==0)
    			break;
    		preParents = parentMenu;
    	}
    	List<OptInfo> roleOptInfos = new ArrayList<OptInfo>();   
    	roleOptInfos.addAll(roleOpts);
    	return roleOptInfos;
    }

	@Override
	@Transactional
	public List<OptInfo> getFunctionsByRoleCode(String unitRoleCode) {
		String hql="From OptInfo where (optId in "				
					+ "(Select optId From OptMethod where optCode in"
					+ 	" (select id.optCode from RolePower  where id.roleCode=?) )) "
				+ "and (optType='S' or optType='O')";
		return baseDao.listObjects(hql, new String[]{unitRoleCode} );
	}
}
