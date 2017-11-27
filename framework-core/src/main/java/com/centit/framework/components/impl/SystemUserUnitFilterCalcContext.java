package com.centit.framework.components.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.UserUnitFilterCalcContext;
import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.framework.model.basedata.IDataDictionary;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.compiler.Lexer;

import java.util.*;

public class SystemUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext
    implements UserUnitFilterCalcContext {

    public SystemUserUnitFilterCalcContext(){
        super();
    }

    @Override
    public List<? extends IUserInfo> listAllUserInfo(){
        return CodeRepositoryUtil.listAllUsers();
    }

    @Override
    public List<? extends IUnitInfo> listAllUnitInfo() {
        return CodeRepositoryUtil.listAllUnits();
    }

    @Override
    public List<? extends IUnitInfo> listSubUnit(String unitCode) {
        return CodeRepositoryUtil.getSubUnits(unitCode);
    }

    @Override
    public List<? extends IUnitInfo> listSubUnitAll(String unitCode) {
        return CodeRepositoryUtil.getAllSubUnits(unitCode);
    }

    @Override
    public IUnitInfo getUnitInfoByCode(String unitCode) {
        return CodeRepositoryUtil.getUnitInfoByCode(unitCode);
    }

    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        return CodeRepositoryUtil.listAllUserUnits();
    }

    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return CodeRepositoryUtil.listUnitUsers(unitCode);
    }

    @Override
    public IUserInfo getUserInfoByCode(String userCode) {
        return CodeRepositoryUtil.getUserInfoByCode(userCode);
    }

    /**
     * 从数据字典中获取 Rank 的等级
     *
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        IDataDictionary dd = CodeRepositoryUtil.getDataPiece("RankType", rankCode);
        if(dd!=null)
            return Integer.valueOf(dd.getExtraCode());
        return CodeRepositoryUtil.MAXXZRANK;
    }
}
