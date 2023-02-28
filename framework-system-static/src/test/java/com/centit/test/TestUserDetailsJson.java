package com.centit.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUserDetailsJson {
    @Test
    public void createUserDetails() throws Exception {
        JsonCentitUserDetails userDetails = new JsonCentitUserDetails();
        UserInfo userInfo = new UserInfo(
                "anonymousUser",
                "T",
                "anonymousUser",
                "anonymousUser");
        List<UserUnit> uus = new ArrayList<>();
        UserUnit uu = new UserUnit("00001", "nq","zz","F");
        uu.setUserCode("anonymousUser");
        uu.setUnitCode("U00001");
        uus.add(uu);

        uu = new UserUnit("00002", "gl","zr","T");
        uu.setUserCode("anonymousUser");
        uu.setUnitCode("U00002");
        uus.add(uu);

        userDetails.setUserUnits((JSONArray)JSON.toJSON(uus));
        List<RoleInfo> roles = new ArrayList<>(2);
        RoleInfo roleInfo = new RoleInfo("anonymous", "匿名用户角色","G",
                "U00001","T","匿名用户角色");
        roles.add(roleInfo);
        roleInfo = new RoleInfo("admin", "管理员角色","G",
                "U00002","T","管理员角色");
        roles.add(roleInfo);

        userDetails.setUserInfo((JSONObject) JSON.toJSON(userInfo));

        userDetails.setAuthoritiesByRoles((JSONArray) JSON.toJSON(roles));

        String s = JSON.toJSONString(userDetails);

        System.out.println(s);

        userDetails = JSON.parseObject(s,JsonCentitUserDetails.class);
        s = JSON.toJSONString(userDetails);

        System.out.println(s);
    }
}
