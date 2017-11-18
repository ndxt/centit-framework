package com.centit.framework.listener;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.SqlDateDeserializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.centit.framework.core.controller.DatetimeSerializer;
import com.centit.framework.core.controller.LobSerializer;
import com.centit.framework.core.controller.SqlTimestampDeserializer;
import com.centit.framework.core.controller.UtilDateDeserializer;

public class InitialWebRuntimeEnvironment {

    public InitialWebRuntimeEnvironment(){

    }

    public void initialEnvironment(){
        configFastjson();
    }

    public static void configFastjson(){
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Timestamp.class, SqlTimestampDeserializer.instance);
        ParserConfig.getGlobalInstance().putDeserializer(java.sql.Date.class,  SqlDateDeserializer.instance);
        ParserConfig.getGlobalInstance().putDeserializer(java.util.Date.class, UtilDateDeserializer.instance);

        SerializeConfig.getGlobalInstance().put(java.util.Date.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Date.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Timestamp.class, DatetimeSerializer.instance);
        SerializeConfig.getGlobalInstance().put(java.sql.Blob.class, LobSerializer.instance);
        //SerializeConfig.getGlobalInstance().put(oracle.sql.BLOB.class, LobSerializer.instance);
    }
}
