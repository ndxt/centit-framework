package com.centit.framework.appclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
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
public class HttpReceiveJSON {
    private static final Logger logger = LoggerFactory.getLogger(HttpReceiveJSON.class);

    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     * resObj resJSON  resJSONObject  resJSONOArray 这四个是同一个对象
     * isResponseData 表示 resJSONObject 中含有 code data message，是框架的标准回复
     */
    private Object resObj;
    private JSON resJSON;
    private JSONObject resJSONObject;
    private JSONArray resJSONOArray;

    private boolean isJSON;
    private boolean isResponseData;
    private boolean isArray;

    private HttpReceiveJSON() {
    }

    public boolean isNull() {
        return resObj == null;
    }

    public boolean isJSON() {
        return isJSON;
    }

    public boolean isJSONObject() {
        return isJSON && !isArray && !isResponseData;
    }

    public boolean isResponseData() {
        return isResponseData;
    }

    public boolean isArray() {
        return isArray;
    }

    public int getCode() {
        if(resObj == null || !isResponseData)
            return 0;
        Integer retCode = resJSONObject.getInteger(ResponseData.RES_CODE_FILED);
        return retCode==null ? 0 : retCode;
    }

    public String getMessage() {
        if(resObj == null|| !isResponseData)
            return "OK";
        return resJSONObject.getString(ResponseData.RES_MSG_FILED);
    }

    public Object getData() {
        if(resObj==null)
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
        if(resJSON==null)
            return null;
        if( isResponseData ) {
            return resJSONObject.getObject(ResponseData.RES_DATA_FILED, clazz);
        }
        if(isJSON){
            return JSON.toJavaObject(resJSON, clazz);
        }
        if( clazz.isAssignableFrom(resObj.getClass())){
            return (T) resObj;
        }
        return null;
    }

    public <T> List<T> getDataAsArray(Class<T> clazz) {
        if(resJSON==null)
            return null;
        JSONArray jsonArray = null;
        if( isResponseData ) {
            jsonArray = resJSONObject.getJSONArray(ResponseData.RES_DATA_FILED);
        }else if(isArray){
            jsonArray = resJSONOArray;
        }
        if(jsonArray==null)
            return null;
        return jsonArray.toJavaList(clazz);
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

    public static HttpReceiveJSON valueOfJson(String jsonStr){
        if(jsonStr==null)
            return null;
        HttpReceiveJSON recvJson = new HttpReceiveJSON();
        recvJson.resObj = JSON.parse(jsonStr);
        recvJson.isJSON = recvJson.resObj instanceof JSON;
        if(recvJson.isJSON) {
            recvJson.resJSON = (JSON) recvJson.resObj;
            if (recvJson.resJSON instanceof JSONObject) {
                recvJson.resJSONObject = (JSONObject) recvJson.resJSON;
                recvJson.isArray = false;
                recvJson.isResponseData =
                    recvJson.resJSONObject.containsKey(ResponseData.RES_CODE_FILED)
                        && recvJson.resJSONObject.containsKey(ResponseData.RES_MSG_FILED)
                        && recvJson.resJSONObject.containsKey(ResponseData.RES_DATA_FILED);
            } else {
                recvJson.isResponseData = false;
                recvJson.isArray = recvJson.resJSON instanceof JSONArray;
                if (recvJson.isArray) {
                    recvJson.resJSONOArray = (JSONArray) recvJson.resJSON;
                }
            }
        }else{
            recvJson.isResponseData = false;
            recvJson.isArray = false;
        }
        return recvJson;
    }

    public JSON getOriginalJSON() {
        return resJSON;
    }
}
