package com.centit.framework.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.algorithm.StringBaseOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取http请求 返回的数据；这个应该命名为 receiveJson
 * 作为接收数据使用
 */
@SuppressWarnings("unused")
public class ResponseJSON {
    private static final Logger logger = LoggerFactory.getLogger(ResponseJSON.class);

    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     */
    private JSONObject resJSON;

    private ResponseJSON() {
    }

    public boolean isNull() {
        return resJSON==null;
    }

    public Integer getCode() {
        if(resJSON==null)
            return null;
        return resJSON.getInteger(ResponseData.RES_CODE_FILED);
    }

    public String getMessage() {
        if(resJSON==null)
            return null;
        return resJSON.getString(ResponseData.RES_MSG_FILED);
    }

    public Object getData() {
        if(resJSON==null)
            return null;
        return resJSON.get(ResponseData.RES_DATA_FILED);
    }


    public String getDataAsString() {
        Object obj = resJSON.get(ResponseData.RES_DATA_FILED);
        if(obj instanceof JSON){
            return ((JSON)obj).toJSONString();
        }
        return StringBaseOpt.objectToString(obj);
    }


    public <T> T getDataAsObject( Class<T> clazz) {
        return resJSON.getObject(ResponseData.RES_DATA_FILED,clazz);
    }

    public <T> List<T> getDataAsArray(Class<T> clazz) {
        JSONArray jsonArray = resJSON.getJSONArray(ResponseData.RES_DATA_FILED);
        if(jsonArray==null)
            return null;
        return jsonArray.toJavaList(clazz);
    }

    public <T> Map<String,T> convertJSONToMap(JSONObject jsonMap, Class<T> clazz) {
        Map<String,T> ret = new HashMap<>();
        for(String sKey : jsonMap.keySet()){
            ret.put( sKey, jsonMap.getObject(sKey, clazz));
        }
        return ret;
    }

    public <T> Map<String,T> getDataAsMap( Class<T> clazz) {
        Object data = getData();
        if(data==null)
            return null;
        if(data instanceof JSONObject){
            return convertJSONToMap((JSONObject)data,clazz);
        }
        return null;
    }

    public Object getData(String skey) {
        Object data = getData();
        if(data==null)
            return null;
        if(data instanceof JSONObject) {
            return ((JSONObject) data).get(skey);
        }
        return null;
    }

    public String getDataAsString(String skey) {
        Object obj = getData(skey);
        if(obj instanceof JSON){
            return ((JSON)obj).toJSONString();
        }
        return StringBaseOpt.objectToString(obj);
    }



    public <T> T getDataAsObject(String sKey, Class<T> clazz) {
        Object data = getData();
        if(data==null)
            return null;
        if(data instanceof JSONObject) {
            return ((JSONObject) data).getObject(sKey,clazz);
        }
        return null;
    }

    public <T> List<T> getDataAsArray(String sKey, Class<T> clazz) {
        Object data = getData(sKey);
        if(data==null/* || !(data instanceof Map)*/)
            return null;
        if(data instanceof JSONArray) {
            return ((JSONArray)data).toJavaList(clazz);
        }
        return null;
    }

    public <T> Map<String,T> getDataAsMap(String sKey, Class<T> clazz) {
        Object data = getData(sKey);
        if(data==null || !(data instanceof JSONObject))
            return null;
        return convertJSONToMap((JSONObject)data,clazz);
    }

    public static ResponseJSON valueOfJson(String jsonStr){
        if(jsonStr==null)
            return null;
        //System.out.print(jsonStr);
        ResponseJSON retJson = new ResponseJSON();
        retJson.resJSON = JSON.parseObject(jsonStr);
        return retJson;
        //JSON.parseObject(jsonStr, ResponseJSON.class);
    }

    public JSONObject getOriginalJSON() {
        return resJSON;
    }
}
