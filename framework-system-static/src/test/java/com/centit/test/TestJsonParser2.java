package com.centit.test;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.dao.DictionaryMapColumn;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.staticsystem.po.OptInfo;
import com.centit.framework.staticsystem.po.UserUnit;
import com.centit.framework.staticsystem.service.impl.JsonPlatformEnvironment;
import com.centit.support.file.FileIOOpt;

public class TestJsonParser2 {

    public static void main(String[] args) {
        JsonPlatformEnvironment plat =  new JsonPlatformEnvironment();
        plat.setAppHome("/home/codefan/projects/framework/centit-framework/framework-web-demo/src/main/resources");
        CodeRepositoryCache.setPlatformEnvironment(plat);
        List<DictionaryMapColumn> maps = DictionaryMapUtils.getDictionaryMapColumns(UserUnit.class);
        for(DictionaryMapColumn dmc : maps){
            System.out.println(dmc.getFieldName()+":" +dmc.getMapFieldName());
        }
        //toOptInfo2();
    }

    public static void toOptInfo3(){

        String jsonStr2="{"+
"    'icon':'icon-base icon-base-user',"+
"    'text':'下属部门管理',"+
"    'optId':'DEPTPOW',"+
"    'updator':'u0000000',"+
"    'preOptId':'DEPTMAG',"+
"    'pid':'DEPTMAG',"+
"    'optUrl':'/system/deptManager',"+
"    'url':'modules/sys/deptpow/deptpow.html',"+
"    'updateDate':'2016-05-27 15:19:33',"+
"    'creator':'u0000000',"+
"    'optType':'O',"+
"    'pageType':'D',"+
"    'id':'DEPTPOW',"+
"    'orderInd':1,"+
"    'optRoute':'modules/sys/deptpow/deptpow.html',"+
"    'optName':'下属部门管理',"+
"    'isInToolbar':'Y',"+
"    'dataScopes':["+
"    ],"+
"    'lastModifyDate':'2016-05-27 15:19:33',"+
"    'optMethods':["+
"    ],"+
"    'attributes':{"+
"        'external':false"+
"    }"+
"}";

        String jsonStr ="{"+
                "'text':'数据库定义',"+
                "'optId':'DatabaseInfo',"+
                "'preOptId':'MF_MDFORMMGR',"+
                "'pid':'MF_MDFORMMGR',"+
                "'optUrl':'service/sys/databaseinfo',"+
                "'url':'modules/sys/databaseinfo/databaseinfo.html',"+
                "'updateDate':'2016-07-29 16:46:48',"+
                "'id':'DatabaseInfo',"+
                "'optType':'O',"+
                "'pageType':'D',"+
                "'optRoute':'modules/sys/databaseinfo/databaseinfo.html',"+
                "'optName':'数据库定义',"+
                "'isInToolbar':'Y',"+
                "'dataScopes':["+
                "],"+
                "'lastModifyDate':'2016-07-29 16:46:48',"+
                "'attributes':{"+
                "    'external':false"+
                "},"+
                "'optMethods':["+
                "]"+
            "}";
        OptInfo opt = JSON.parseObject(jsonStr2, OptInfo.class);
        System.out.println(opt.getOptName());
    }

    public static void toOptInfo(){
        try {
            String text = FileIOOpt.readStringFromFile("D:\\Projects\\RunData\\optinfo.josn");
            JSONObject jsonMap = (JSONObject)JSON.parse(text);
            for(String skey : jsonMap.keySet()){
                String str = JSON.toJSONString(jsonMap.get(skey));
                OptInfo opt =  JSON.parseObject(str, OptInfo.class);
                System.out.println(opt.getOptName());
            }
        } catch (IOException e) {
            return;
        }

    }
    public static void toOptInfo2(){
        try {
            String text = FileIOOpt.readStringFromFile("D:\\Projects\\RunData\\optinfo.josn");
            JSONObject jsonMap = (JSONObject)JSON.parse(text);
            for(String skey : jsonMap.keySet()){
                OptInfo opt =  jsonMap.getObject(skey, OptInfo.class);
                System.out.println(opt.getOptName());
            }
        } catch (IOException e) {
            return;
        }

    }

}
