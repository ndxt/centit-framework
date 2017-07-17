package com.centit.framework.system.dao;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.system.po.OptFlowNoInfo;
import com.centit.framework.system.po.OptFlowNoInfoId;

@Repository
public class OptFlowNoInfoDao extends BaseDaoImpl<OptFlowNoInfo, OptFlowNoInfoId> {

    public static final Logger logger = LoggerFactory.getLogger(OptFlowNoInfoDao.class);

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();

            filterField.put("ownerCode", "cid.ownerCode=?");

            filterField.put("codeDate", CodeBook.EQUAL_HQL_ID);

            filterField.put("codeCode", "cid.codeCode=?");

            filterField.put("curNo", CodeBook.LIKE_HQL_ID);

            filterField.put("lastCodeDate", CodeBook.LIKE_HQL_ID);

        }
        return filterField;
    }

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
