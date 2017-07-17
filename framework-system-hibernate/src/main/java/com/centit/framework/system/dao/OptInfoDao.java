package com.centit.framework.system.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.po.FVUserOptList;
import com.centit.framework.system.po.FVUserOptMoudleList;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.OptMethodUrlMap;

@Repository
public class OptInfoDao extends BaseDaoImpl<OptInfo, String> {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("OPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTURL", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTNAME", CodeBook.LIKE_HQL_ID);            
            filterField.put("preOptId", CodeBook.EQUAL_HQL_ID);            
            filterField.put("NP_TOPOPT", "(preOptId is null or preOptId='0')");
            filterField.put("OPTTYPE", CodeBook.EQUAL_HQL_ID);
            filterField.put("TOPOPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("ISINTOOLBAR", CodeBook.EQUAL_HQL_ID);
            filterField.put(CodeBook.ORDER_BY_HQL_ID, " preoptid, orderind");
        }
        return filterField;
    }

    @Override
    @Transactional
    public List<OptInfo> listValidObjects() {
        String hql = "from OptInfo opt where opt.isInToolbar = 'T'";

        return listObjects(hql);
    }

    @SuppressWarnings("unchecked")
    public List<OptInfo> getFunctionsByUserID(String userID) {
        String[] params = null;
        String hql = "FROM FVUserOptMoudleList where userCode=?";
        // + " ORDER BY preoptid, formcode";

        params = new String[]{userID};
        
        List<FVUserOptMoudleList> ls = (List<FVUserOptMoudleList>)
                DatabaseOptUtils.findObjectsByHql(this,hql, (Object[]) params);
        List<OptInfo> opts = new ArrayList<OptInfo>();
        for (FVUserOptMoudleList opm : ls) {
            OptInfo opt = new OptInfo();
            opt.setFormCode(opm.getFormcode());
            opt.setImgIndex(opm.getImgindex());
            opt.setIsInToolbar(opm.getIsintoolbar());
            opt.setMsgNo(opm.getMsgno());
            opt.setMsgPrm(opm.getMsgprm());
            opt.setOptId(opm.getOptid());
            opt.setOptName(opm.getOptname());
            opt.setOptUrl(opm.getOpturl());
            opt.setPreOptId(opm.getPreoptid());
            opt.setTopOptId(opm.getTopoptid());
            opts.add(opt);
        }
        return opts;
    }

 

    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptInfo> getMenuFuncByUserID(String userID, boolean isAdmin) {
        String hql1 = "FROM OptInfo where optUrl='...' order by orderInd ";
        List<OptInfo> preOpts = listObjects(hql1);

        String hql = "FROM FVUserOptMoudleList where isintoolbar='Y' and userCode=? and optType = " +
                (isAdmin ? "'S'" : "'O'") + " ORDER BY orderind";
        // + " ORDER BY preoptid, formcode";
        List<FVUserOptMoudleList> ls = (List<FVUserOptMoudleList>) DatabaseOptUtils.findObjectsByHql
                (this, hql,new Object[]{userID});

        return getMenuFuncs(preOpts, ls);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<String> listUserDataPowerByOptMethod(String userCode,String optid,String optMethod) {
       
    	String sSqlsen = "select OPTSCOPECODES " +
                 "from F_V_USEROPTDATASCOPES " +
                 "where USERCODE = ? and OPTID = ? and OPTMETHOD = ?";
    	 
    	List<Object[]> l = (List<Object[]>) DatabaseOptUtils.findObjectsBySql
    			 (this, sSqlsen,new Object[]{userCode, optid, optMethod});
    	 
    	if(l==null)
    		 return null;
    	List<String> scopeCodes = new ArrayList<String>();
     	for(Object[] obj : l)
     		scopeCodes.add(String.valueOf(obj[0]));
     	return scopeCodes;
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
       /* // end
        ListOpt.sortAsTree(opts, new ListOpt.ParentChild<OptInfo>() {
			@Override
			public boolean parentAndChild(OptInfo p, OptInfo c) {
				return p.getOptId().equals(c.getPreOptId());
			}
        	
		});*/
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptInfo> getFunctionsByUserAndSuperFunctionId(String userID, String superFunctionId) {
        String[] params = null;
        String hql = "FROM FVUserOptMoudleList  where userCode=? and topoptid=?" + " ORDER BY preoptid, orderind";

        params = new String[]{userID, superFunctionId};
        List<FVUserOptMoudleList> ls = (List<FVUserOptMoudleList>)DatabaseOptUtils.findObjectsByHql
                (this, hql, (Object[]) params);
        List<OptInfo> opts = new ArrayList<OptInfo>();
        for (FVUserOptMoudleList opm : ls) {
            OptInfo opt = new OptInfo();
            opt.setFormCode(opm.getFormcode());
            opt.setImgIndex(opm.getImgindex());
            opt.setIsInToolbar(opm.getIsintoolbar());
            opt.setMsgNo(opm.getMsgno());
            opt.setOptType(opm.getOpttype());
            opt.setMsgPrm(opm.getMsgprm());
            opt.setOptId(opm.getOptid());
            opt.setOptName(opm.getOptname());
            opt.setOptUrl(opm.getOpturl());
            opt.setPreOptId(opm.getPreoptid());
            opt.setTopOptId(opm.getTopoptid());
            opts.add(opt);
            System.out.print(opt.getOptType());
        }

        return opts;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptMethod> getMethodByUserAndOptid(String userCode, String optid) {
        String[] params = null;
        String hql = "FROM FVUserOptList urv where urv.id.userCode=? and optid= ?";

        params = new String[]{userCode, optid};
        List<FVUserOptList> ls = (List<FVUserOptList>) DatabaseOptUtils.findObjectsByHql
                (this,hql, (Object[]) params);
        List<OptMethod> methods = new ArrayList<OptMethod>();
        for (FVUserOptList opm : ls) {
            OptMethod method = new OptMethod();
            method.setOptCode(opm.getId().getOptcode());
            method.setOptId(opm.getOptId());
            method.setOptMethod(opm.getOptMethod());
            method.setOptName(opm.getOptName());
            methods.add(method);
        }
        return methods;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptMethodUrlMap> listAllOptMethodUrlMap() {
        List<?> listObjects = DatabaseOptUtils.findObjectsByHql
                (this, "from OptMethodUrlMap");

        return (List<OptMethodUrlMap>) listObjects;
    }

 
}
