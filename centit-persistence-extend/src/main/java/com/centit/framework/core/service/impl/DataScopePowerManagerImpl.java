package com.centit.framework.core.service.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.dao.DataPowerFilter;
import com.centit.framework.core.service.DataScopePowerManager;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.framework.model.security.CentitUserDetails;

import java.util.List;

public class DataScopePowerManagerImpl implements DataScopePowerManager {
    /**
     * 获取用户数据权限过滤器
     * @param sUserCode sUserCode
     * @param sOptId 业务名称
     * @param sOptMethod 对应的方法名称
     * @return 过滤条件列表，null或者空位不过来
     */
    @Override
    public List<String> listUserDataFiltersByOptIdAndMethod(String topUnit, String sUserCode, String sOptId, String sOptMethod) {
        return CodeRepositoryUtil.listUserDataFiltersByOptIdAndMethod(topUnit, sUserCode, sOptId, sOptMethod);
    }

    @Override
    public DataPowerFilter createUserDataPowerFilter(UserInfo userInfo, String topUnit, String currentUnit) {

        DataPowerFilter dpf = new DataPowerFilter(topUnit);
        //当前用户信息
        dpf.addSourceData("currentUser", userInfo);
        dpf.addSourceData("currentStation", currentUnit);
        dpf.addSourceData("topUnitCode",topUnit);
        dpf.addSourceData("currentUnitCode", currentUnit);

        CurrentUserContext context = new CurrentUserContext(userInfo, topUnit, currentUnit);
        dpf.addSourceData("currentUnit", context::getCurrentUnit);
        dpf.addSourceData("primaryUnit", context::getPrimaryUnit);
        dpf.addSourceData("userUnits", context::listUserUnits);
        dpf.addSourceData("rankUnits", context::getRankUnitsMap);
        dpf.addSourceData("stationUnits", context::getStationUnitsMap);
        dpf.addSourceData("userRoles", context::listUserRoles);
        dpf.addSourceData("allSubUnits", context::listAllSubUnits);
        dpf.addSourceData("subUnits", context::listSubUnits);
        return dpf;
    }

    @Override
    public DataPowerFilter createUserDataPowerFilter(CentitUserDetails userDetails) {

        DataPowerFilter dpf = new DataPowerFilter(userDetails.getTopUnitCode());
        //当前用户信息
        dpf.addSourceData("currentUser", userDetails.getUserInfo());
        dpf.addSourceData("currentUnit", userDetails.getCurrentStation());
        dpf.addSourceData("currentStation", userDetails.getCurrentUnitCode());
        dpf.addSourceData("currentUnitCode", userDetails.getCurrentUnitCode());
        dpf.addSourceData("topUnitCode", userDetails.getTopUnitCode());
        //dpf.addSourceData("userSetting", userDetails.getUserSettings());
        dpf.addSourceData("userUnits", userDetails.getUserUnits());
        dpf.addSourceData("userRoles", userDetails.getUserRoles());
        CurrentUserContext context = new CurrentUserContext(userDetails.getUserInfo(), userDetails.getTopUnitCode(),
            userDetails.getCurrentUnitCode());
        dpf.addSourceData("primaryUnit", context::getPrimaryUnit);
        dpf.addSourceData("rankUnits", context::getRankUnitsMap);
        dpf.addSourceData("stationUnits", context::getStationUnitsMap);
        dpf.addSourceData("allSubUnits", context::listAllSubUnits);
        dpf.addSourceData("subUnits", context::listSubUnits);
        return dpf;
    }

}
