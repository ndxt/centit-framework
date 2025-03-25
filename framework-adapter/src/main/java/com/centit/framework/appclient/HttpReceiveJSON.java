package com.centit.framework.appclient;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ToResponseData;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.json.JSONOpt;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取http请求 返回的数据；这个应该命名为 receiveJson
 * 作为接收数据使用
 */
@SuppressWarnings({"unused","unchecked"})
public class HttpReceiveJSON implements ToResponseData {
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
        return resObj instanceof JSONObject || resObj instanceof JSONArray;
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
        return isResponseData && resJSONObject.containsKey(ResponseData.RES_MSG_FILED) ?
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
        if(obj instanceof JSONObject || obj instanceof JSONArray){
            return obj.toString();
        }
        return StringBaseOpt.objectToString(obj);
    }

    public <T> T getDataAsObject(Class<T> clazz) {
        Object data = getData();
        if(data==null) {
            return null;
        }
        if(data instanceof JSONObject) {
            return ((JSONObject)data).toJavaObject(clazz);
        }
        if(data instanceof Map) {
            JSONObject jsonObject = JSONObject.from(data);
            return jsonObject.toJavaObject(clazz);
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
        if(obj instanceof JSONObject || obj instanceof JSONArray){
            return obj.toString();
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
        if(data instanceof Map) {
            JSONObject jsonObject = JSONObject.from(data);
            return jsonObject.toJavaObject(clazz);
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

    public static HttpReceiveJSON valueOf(Object json){
        if(json==null)
            return null;
        HttpReceiveJSON recvJson = new HttpReceiveJSON();
        recvJson.resObj = JSON.toJSON(json);
        recvJson.isResponseData = false;
        if (recvJson.resObj instanceof JSONObject) {
            JSONObject resJson = (JSONObject) recvJson.resObj;
            boolean hasCode = false, hasMessage = false, hasData = false, hasExt = false;
            for(String sKey : resJson.keySet()){
                switch (sKey) {
                    case ResponseData.RES_CODE_FILED:
                        hasCode = true;
                        break;
                    case ResponseData.RES_MSG_FILED:
                        hasMessage = true;
                        break;
                    case ResponseData.RES_DATA_FILED:
                        hasData = true;
                        break;
                    default:
                        hasExt = true;
                        break;
                }
            }

            recvJson.isResponseData = hasCode && hasMessage && !hasExt;
            if(recvJson.isResponseData) {
                recvJson.resJSONObject = new JSONObject();
                recvJson.resJSONObject.put(ResponseData.RES_CODE_FILED, resJson.get(ResponseData.RES_CODE_FILED));
                recvJson.resJSONObject.put(ResponseData.RES_MSG_FILED, resJson.get(ResponseData.RES_MSG_FILED));
                if (hasData) {
                    recvJson.resJSONObject.put(ResponseData.RES_DATA_FILED,
                        resJson.get(ResponseData.RES_DATA_FILED));
                }
            }
        }
        return recvJson;
    }

    public static HttpReceiveJSON dataOf(Object json){
        HttpReceiveJSON receiveJson = new HttpReceiveJSON();
        receiveJson.resObj = JSON.toJSON(json);
        receiveJson.isResponseData = false;
        return receiveJson;
    }

    public static HttpReceiveJSON valueOfJson(String jsonStr){
        if(StringUtils.isBlank(jsonStr))
            return null;
        return valueOf(JSONOpt.parseJsonString(jsonStr));
    }

    public static HttpReceiveJSON dataOfJson(String jsonStr){
        if(StringUtils.isBlank(jsonStr))
            return dataOf(null);
        return dataOf(JSONOpt.parseJsonString(jsonStr));
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

    public Object getOriginalJSON() {
        if(isJSON()){
            return resObj;
        }
        return null;
    }

    @Override
    public ResponseData toResponseData() {
        return ResponseData.makeErrorMessageWithData(
            this.getData(), this.getCode(), this.getMessage()
        );
    }
}
