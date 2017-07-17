package com.centit.framework.system.dao;

import org.springframework.stereotype.Repository;

import com.centit.framework.system.po.OptFlowNoInfo;
import com.centit.framework.system.po.OptFlowNoInfoId;

@Repository
public interface OptFlowNoInfoDao {
	
	public OptFlowNoInfo getObjectById(OptFlowNoInfoId cid);
	
	public void deleteObjectById(OptFlowNoInfoId cid);
	
	public void saveObject(OptFlowNoInfo optMethod);

//    @SuppressWarnings("unchecked")
//    public OptFlowNoInfo getOptFlowNoInfoBy_ownerCode_year_codeCode(
//            String ownerCode, String baseYear, String codeCode) {
//        String hql = "FROM OptFlowNoInfo where cid.ownerCode=? and curyear=? and  cid.codeCode=?   ";
//
//        List<OptFlowNoInfo> opts = getHibernateTemplate().find(hql,
//                new Object[] { ownerCode, baseYear, codeCode });
//
//        if (opts == null || opts.size() == 0) {
//            return null;
//        }
//        return opts.get(0);
//    }

    /**
     * 获取下一个流水号
     * @param ownerCode
     * @param codeCode
     * @param curyear
     * @return
     */
//    public long newNextLshBaseYear(String ownerCode, String codeCode,
//            String curyear) {
//        OptFlowNoInfo noInfo = this.getOptFlowNoInfoBy_ownerCode_year_codeCode(
//                ownerCode, curyear, codeCode);
//        long nextCode = 1l;
//        if (noInfo == null) {
//            noInfo = new OptFlowNoInfo();
//
//            noInfo.setOwnerCode(ownerCode);
//            noInfo.setCodeCode(codeCode);
//            noInfo.setCodeDate(DatetimeOpt.currentUtilDate());
//
//            noInfo.setCuryear(curyear);
//            noInfo.setCurNo(nextCode);
//            noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());
//        } else {
//            nextCode = noInfo.getCurNo() + 1;
//            noInfo.setCurNo(nextCode);
//            noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());
//            noInfo.setCuryear(curyear);
//        }
//        this.saveObject(noInfo);
//        return nextCode;
//    }
}
