package com.centit.framework.config;

import com.centit.support.json.JSONOpt;

public class InitialWebRuntimeEnvironment {

    public InitialWebRuntimeEnvironment(){

    }

    public void initialEnvironment(){
        configFastjson();
    }

    /**
     * 初始化fastJson的序列化类
     */
    public static void configFastjson(){
        JSONOpt.fastjsonGlobalConfig();
    }
}
