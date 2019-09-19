package com.centit.framework.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.config.InitialWebRuntimeEnvironment;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.support.algorithm.DatetimeOpt;

import java.util.ArrayList;
import java.util.List;

public class TestJsonParser {

    public static void main(String[] args) {
        toJsonDate();
    }

    public static void toJsonDate(){

        JSONObject jo= new JSONObject();
        jo.put("Date", DatetimeOpt.currentUtilDate());
        System.out.println(jo.toJSONString());
        InitialWebRuntimeEnvironment iwr = new InitialWebRuntimeEnvironment();
        iwr.initialEnvironment();
        System.out.println(jo.toJSONString());
    }

    public static void toJsonObject(){

        String sJson ="{\"userCode\":\"hello\",\"userName\":\"world\",\"userMail\":0}";
        UserInfo us = JSON.parseObject(sJson,UserInfo.class);
        System.out.println(us.getUserName());
    }


    public static void toJsonString(){
        int a = 1;
        int [] aa = new int[]{1,2,3,4,5,6};
        List<String> astr = new ArrayList<String>();
        astr.add("A");
        astr.add("B");
        astr.add("C");
        astr.add("D");
        System.out.println(JSON.toJSONString(a));
        System.out.println(JSON.toJSONString(aa));
        System.out.println(JSON.toJSONString(astr));
    }

    public static void testJsonParse(){
        String sJson ="{\"message\":\"OK\",\"data\":[{\"isValid\":\"T\",\"label\":\"实施人员2\",\"roleCode\":\"G-DEPLOY\",\"roleDesc\":\"实施人员角色\",\"roleName\":\"实施人员2\",\"rolePowers\":[],\"value\":\"G-DEPLOY\"},{\"isValid\":\"T\",\"label\":\"系统管理员\",\"roleCode\":\"G-SYSADMIN\",\"roleDesc\":\"所有系统配置功能\",\"roleName\":\"系统管理员\",\"rolePowers\":[],\"value\":\"G-SYSADMIN\"}],\"code\":0}";
        JSONObject jobj = JSON.parseObject(sJson);
        List<RoleInfo> roles  = JSON.parseArray(jobj.get("data").toString(), RoleInfo.class);

        System.out.println(roles.size());

    }
}
