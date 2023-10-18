package com.centit.framework.core.service.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.basedata.UnitInfo;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.basedata.UserRole;
import com.centit.framework.model.basedata.UserUnit;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentUserContext {
    public UserInfo userInfo;
    public String currentUnit;
    public String topUnit;

    public CurrentUserContext(UserInfo userInfo, String topUnit, String currentUnit){
        this.userInfo = userInfo;
        this.topUnit = topUnit;
        this.currentUnit = StringUtils.isBlank(currentUnit)?
            userInfo.getPrimaryUnit() : currentUnit;
    }


    public UnitInfo getCurrentUnit(){
        return CodeRepositoryUtil
            .getUnitInfoByCode(topUnit, currentUnit);
    }

    public UnitInfo getPrimaryUnit(){
        return CodeRepositoryUtil
            .getUnitInfoByCode(topUnit, userInfo.getPrimaryUnit());
    }

    public List<UserUnit> listUserUnits(){
        return CodeRepositoryUtil
            .listUserUnits(topUnit, userInfo.getUserCode());
    }

    public Map<String, List<UserUnit>> getRankUnitsMap(){
        List<UserUnit> userUnits = listUserUnits();
        Map<String, List<UserUnit>> rankUnits = new HashMap<>(5);
        for(UserUnit uu : userUnits ){
            List<UserUnit> rankUnit = rankUnits.get(uu.getUserRank());
            if(rankUnit==null){
                rankUnit = new ArrayList<>(4);
            }
            rankUnit.add(uu);
            rankUnits.put(uu.getUserRank(),rankUnit);
        }
        return rankUnits;
    }

    public Map<String, List<UserUnit>> getStationUnitsMap(){
        List<UserUnit> userUnits = listUserUnits();
        Map<String, List<UserUnit>> stationUnits = new HashMap<>(5);
        for(UserUnit uu : userUnits ){
            List<UserUnit> stationUnit = stationUnits.get(uu.getUserStation());
            if(stationUnit==null){
                stationUnit = new ArrayList<>(4);
            }
            stationUnit.add(uu);
            stationUnits.put(uu.getUserStation(),stationUnit);
        }
        return stationUnits;
    }

    public List<UserRole> listUserRoles() {
        return CodeRepositoryUtil.listUserRoles(topUnit, userInfo.getUserCode());
    }

    public List<UnitInfo> listSubUnits(){
        return CodeRepositoryUtil.getSubUnits(topUnit, currentUnit);
    }

    public List<UnitInfo> listAllSubUnits(){
        List<UnitInfo> allSubUnits=CodeRepositoryUtil.getAllSubUnits(topUnit, currentUnit);
        allSubUnits.add(CodeRepositoryUtil
            .getUnitInfoByCode(topUnit, currentUnit));
        return allSubUnits;
    }
}
