package com.centit.framework.appclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取http请求 返回的数据；这个应该命名为 receiveJson
 * 作为接收数据使用
 */
@SuppressWarnings({"unused","unchecked"})
public class HttpReceiveJSON {
    //private static final Logger logger = LoggerFactory.getLogger(HttpReceiveJSON.class);
    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     * resObj resJSONObject  这两个是同一个对象
     * isResponseData 表示 resJSONObject 中含有 code data message，是框架的标准回复
     */
    private Object resObj;

    private boolean isResponseData;
    private JSONObject resJSONObject;

    private HttpReceiveJSON() {
    }

    public boolean isNull() {
        return resObj == null;
    }

    public boolean isJSON() {
        return resObj instanceof JSON;
    }

    public boolean isResponseData() {
        return isResponseData;
    }

    public int getCode() {
        if(isResponseData) {
            Object retCode = resJSONObject.get(ResponseData.RES_CODE_FILED);
            return retCode == null ? 0
                : NumberBaseOpt.castObjectToInteger(retCode, 0);
        }
        return 0;
    }

    public String getMessage() {
        return isResponseData?
            resJSONObject.getString(ResponseData.RES_MSG_FILED) :
            "OK";
    }

    public Object getData() {
        if(resObj == null)
            return null;
        return isResponseData ?
            resJSONObject.get(ResponseData.RES_DATA_FILED) :
            resObj;
    }

    public String getDataAsString() {
        if(resObj==null)
            return null;
        Object obj = isResponseData ?
            resJSONObject.get(ResponseData.RES_DATA_FILED) :
            resObj;
        if(obj instanceof JSON){
            return ((JSON)obj).toJSONString();
        }
        return StringBaseOpt.objectToString(obj);
    }


    public <T> T getDataAsObject( Class<T> clazz) {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject) {
            return JSON.toJavaObject((JSONObject)data, clazz);
        }
        if(clazz.isAssignableFrom(data.getClass())){
            return (T) data;
        }
        return null;
    }

    public <T> List<T> getDataAsArray(Class<T> clazz) {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONArray) {
            return ((JSONArray)data).toJavaList(clazz);
        }
        return null;
    }

    public static <T> Map<String,T> convertJSONToMap(JSONObject jsonMap, Class<T> clazz) {
        Map<String,T> ret = new HashMap<>();
        for(String sKey : jsonMap.keySet()){
            ret.put( sKey, jsonMap.getObject(sKey, clazz));
        }
        return ret;
    }

    public <T> Map<String,T> getDataAsMap( Class<T> clazz) {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject){
            return convertJSONToMap((JSONObject)data,clazz);
        }
        return null;
    }

    public Object getData(String skey) {
        Object data = getData();
        if(data==null) {
            return null;
        }
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
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject) {
            return ((JSONObject) data).getObject(sKey, clazz);
        }
        return null;
    }

    public <T> List<T> getDataAsArray(String sKey, Class<T> clazz) {
        Object data = getData(sKey);
        if(data==null) {
            return null;
        }
        if(data instanceof JSONArray) {
            return ((JSONArray)data).toJavaList(clazz);
        }
        return null;
    }

    public <T> Map<String,T> getDataAsMap(String sKey, Class<T> clazz) {
        Object data = getData(sKey);
        if(data instanceof JSONObject) {
            return convertJSONToMap((JSONObject) data, clazz);
        }
        return null;
    }

    public static HttpReceiveJSON valueOfJson(String jsonStr){
        if(jsonStr==null)
            return null;
        HttpReceiveJSON recvJson = new HttpReceiveJSON();
        recvJson.resObj = JSON.parse(jsonStr);
        recvJson.isResponseData = false;
        if (recvJson.resObj instanceof JSONObject) {
            recvJson.resJSONObject = (JSONObject) recvJson.resObj;
            recvJson.isResponseData =
                recvJson.resJSONObject.containsKey(ResponseData.RES_CODE_FILED) &&
                recvJson.resJSONObject.containsKey(ResponseData.RES_MSG_FILED);
                // ResponseData 可能没有data部分，但是code和message一定是有的。
                // && recvJson.resJSONObject.containsKey(ResponseData.RES_DATA_FILED);
        }
        return recvJson;
    }

    public JSONObject getJSONObject() {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject) {
            return (JSONObject)data;
        }
        return null;
    }

    public JSONArray getJSONArray() {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONArray) {
            return (JSONArray)data;
        }
        return null;
    }

    public JSONObject getJSONObject(String sKey) {
        Object data = getData(sKey);
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject) {
            return (JSONObject)data;
        }
        return null;
    }

    public JSONArray getJSONArray(String sKey) {
        Object data = getData(sKey);
        if(data==null) {
            return null;
        }
        if(data instanceof JSONArray) {
            return (JSONArray)data;
        }
        return null;
    }

    public JSON getOriginalJSON() {
        if(isJSON()){
            return (JSON)resObj;
        }
        return null;
    }
}
