package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserRole;
import com.centit.framework.model.basedata.IUserUnit;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserUnitFilterCalcContext {

    void setVarTrans(UserUnitVariableTranslate varTrans);

    UserUnitVariableTranslate getVarTrans();

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

    /**
     * 获得机构所有的子机构
     * @param unitCode 机构代码
     * @return 子机构集合
     */
    List<? extends IUnitInfo> listSubUnit(String unitCode);

    IUserInfo getUserInfoByCode(String userCode);

    IUnitInfo getUnitInfoByCode(String unitCode);

    List<? extends IUserUnit> listAllUserUnits();

    List<? extends IUserUnit> listUnitUsers(String unitCode);

    List<? extends IUserUnit> listUserUnits(String userCode);

    List<? extends IUserRole> listUserRoles(String userCode);

    List<? extends IUserRole> listRoleUsers(String roleCode);

    //-----------------通用的常量---------------------
    // 系统角色
    Map<String, String> listAllSystemRole();
    // 岗位
    Map<String, String> listAllStation();
    // 办件角色 FlowUserRole// 流程角色，项目角色
    Map<String, String> listAllProjectRole();
    // 行政角色 代码、名称 、等级
    Map<String, String> listAllRank();
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
    int getUserRank(String userCode);

    int getUserUnitRank(String userCode, String unitCode);
}
