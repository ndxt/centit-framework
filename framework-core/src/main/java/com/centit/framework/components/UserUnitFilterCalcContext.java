package com.centit.framework.components;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserUnitFilterCalcContext {
   
    void setVarTrans(UserUnitVariableTranslate varTrans);

    boolean hasError();

    void clearError();

    String getLastErrMsg();

    void setLastErrMsg(String lastErrMsg);

    void clearParams() ;

    void addUnitParam(String paramName, String unitCode) ;
    
    void addAllUnitParam(Map<String, Set<String>> unitParam);
    
    void addUnitParam(String paramName, Set<String> unitCodes);

    void addUserParam(String paramName, String userCode);

    void addAllUserParam(Map<String, Set<String>> userParam);
    
    void addUserParam(String paramName, Set<String> userCodes);

    void addRankParam(String paramName, int r);

    void addAllRankParam(Map<String,Integer> rankParam);

    Set<String> getUnitCode(String paramName);

    Set<String> getUserCode(String paramName);

    int stringToRank(String srank);

    int getRank(String paramName);

    void setFormula(String sFormula);

    void setPreword(String preWord);

    void setCanAcceptOpt(boolean canAcceptOpt);

    boolean isLabel(String sWord);

    String getAWord( );

    boolean seekToRightBracket();

    List<? extends IUserInfo> listAllUserInfo();

    List<? extends IUnitInfo> listAllUnitInfo();

    IUnitInfo getUnitInfoByCode(String unitCode);

    List<? extends IUserUnit> listAllUserUnits();

    List<? extends IUserUnit> listUnitUsers(String unitCode);

    IUserInfo getUserInfoByCode(String userCode);

    /**
     * 从数据字典中获取 Rank 的等级
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    int getXzRank(String rankCode);
       /* IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", rankCode);
        if(dd!=null)
            return Integer.valueOf(dd.getExtraCode());
        return CodeRepositoryUtil.MAXXZRANK;
    }*/
}
