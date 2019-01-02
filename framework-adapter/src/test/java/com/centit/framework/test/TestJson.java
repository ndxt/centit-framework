package com.centit.framework.test;

import com.centit.framework.appclient.HttpReceiveJSON;

import java.util.List;

/**
 * Created by codefan on 17-8-4.
 */
public class TestJson {
    public static void main(String[] args) {
        /*ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("hello","world");
        resData.addResponseData("hello2","world");
        System.out.println(resData.toJSONString());
        System.out.println(JSON.toJSONString(true));
*/
        String jsonStr = "{\"code\":0,\"message\":\"ok\"," +
            "\"data\":{\"hello\":\"Say Hello to\", \"world\":\" json world!\"," +
            "\"int\":1024, \"bool\":false, \"intArr\":[1,2,3,4]}}";
        HttpReceiveJSON json = HttpReceiveJSON.valueOfJson(jsonStr);
        System.out.println(json.getDataAsString());
        System.out.println(json.getDataAsString("hello")+ json.getDataAsString("world"));
        System.out.println(json.getDataAsObject("int", Integer.class));
        System.out.println(json.getDataAsObject("bool", Boolean.class));
        System.out.println(json.getDataAsArray("intArr", Integer.class));
        System.out.println(json.getOriginalJSON());

        json = HttpReceiveJSON.valueOfJson( "[1,2,3,4,5]");
        //Integer ints = json.getDataAsObject(Integer.class);
        List<Integer> intList = json.getDataAsArray(Integer.class);
        for(Integer i:intList){
            System.out.println(i);
        }
        json = HttpReceiveJSON.valueOfJson("102");
        System.out.println(json.getDataAsObject(Integer.class));

        json = HttpReceiveJSON.valueOfJson("true");
        Boolean bo = json.getDataAsObject(Boolean.class);
        System.out.println(bo);

        jsonStr = "{\"c\":0,\"m\":\"ok\"," +
            "\"d\":[1,'hello',3,4,5]}";
        json = HttpReceiveJSON.valueOfJson(jsonStr);
        System.out.println(json.getDataAsString());
        System.out.println(json.getMessage());

        System.out.println(json.getDataAsString("d"));

        List<String> strList = json.getDataAsArray("d", String.class);
        for(String s:strList){
            System.out.println(s);
        }
    }
}
