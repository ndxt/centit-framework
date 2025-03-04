package com.centit.framework.model.adapter;

import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.basedata.UserRole;
import com.centit.framework.model.basedata.UserUnit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserUnitFilterCalcContext {

    String getTopUnit();

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

    void addRankParam(String paramName, String r);

    void addAllRankParam(Map<String, String> rankParam);

    Set<String> getUnitCode(String paramName);

    Set<String> getUserCode(String paramName);

    String getRank(String paramName);

    void setFormula(String sFormula);

    void writeBackAWord(String preWord);

    void setCanAcceptOpt(boolean canAcceptOpt);

    boolean isLabel(String sWord);

    String getAWord( );

    boolean seekToRightBracket();

    List<UserInfo> listAllUserInfo();

    List<UnitInfo> listAllUnitInfo();

    /**
     * 获得机构所有的子机构
     * @param unitCode 机构代码
     * @return 子机构集合
     */
    List<UnitInfo> listSubUnit(String unitCode);

    UserInfo getUserInfoByCode(String userCode);

    UnitInfo getUnitInfoByCode(String unitCode);

    UserInfo getUserInfoByWord(String userWord);

    UnitInfo getUnitInfoByWord(String unitWord);

    UnitInfo getUnitInfoByDepNo(String depNo);

    List<UserUnit> listAllUserUnits();

    List<UserUnit> listUnitUsers(String unitCode);

    List<UserUnit> listUserUnits(String userCode);

    List<UserRole> listUserRoles(String userCode);

    List<UserRole> listRoleUsers(String roleCode);

    //-----------------通用的常量---------------------
    // 系统角色
    Map<String, String> listAllSystemRole();
    // 岗位
    Map<String, String> listAllStation();

    // 行政角色 代码、名称 、等级
    Map<String, String> listAllRank();

    String getUserRank(String userCode);

    String getUserUnitRank(String userCode, String unitCode);
}
