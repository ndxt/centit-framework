package com.centit.framework.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.support.algorithm.ClassScannerOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.compiler.Pretreatment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

@SuppressWarnings("unused")
public abstract class RequestMappingUtils {

    private static void mapOptInfo(JSONObject optInfo, Class clazz){
        String optId = clazz.getSimpleName();
        if(optId.endsWith("Controller") && optId.length()>10){
            optId = optId.substring(0,optId.length()-10);
        }
        optInfo.put("optId",optId);
        if(clazz.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            String optUrl = StringBaseOpt.castObjectToString(requestMapping.value());
            optInfo.put("optUrl", Pretreatment.mapTemplateString(optUrl,null,"*"));
        }
    }

    private static void mapRequestInfo(JSONObject optMethod, Method method){
        RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
        optMethod.put("optName",method.getName());
        optMethod.put("optUrl",Pretreatment.mapTemplateString(
                StringBaseOpt.castObjectToString(methodMapping.value()),null,"*"));
        //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        optMethod.put("optReq",StringBaseOpt.castObjectToString(methodMapping.method()));
    }

    private static void mapGetRequestInfo(JSONObject optMethod, Method method){
        GetMapping methodMapping = method.getAnnotation(GetMapping.class);
        optMethod.put("optName",method.getName());
        optMethod.put("optUrl",Pretreatment.mapTemplateString(
                StringBaseOpt.castObjectToString(methodMapping.value()),null,"*"));
        //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        optMethod.put("optReq",StringBaseOpt.castObjectToString("GET"));
    }

    private static void mapPostRequestInfo(JSONObject optMethod, Method method){
        PostMapping methodMapping = method.getAnnotation(PostMapping.class);
        optMethod.put("optName",method.getName());
        optMethod.put("optUrl",Pretreatment.mapTemplateString(
                StringBaseOpt.castObjectToString(methodMapping.value()),null,"*"));
        //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        optMethod.put("optReq",StringBaseOpt.castObjectToString("POST"));
    }

    private static void mapPutRequestInfo(JSONObject optMethod, Method method){
        PutMapping methodMapping = method.getAnnotation(PutMapping.class);
        optMethod.put("optName",method.getName());
        optMethod.put("optUrl",Pretreatment.mapTemplateString(
                StringBaseOpt.castObjectToString(methodMapping.value()),null,"*"));
        //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        optMethod.put("optReq",StringBaseOpt.castObjectToString("PUT"));
    }

    private static void mapDeleteRequestInfo(JSONObject optMethod, Method method){
        DeleteMapping methodMapping = method.getAnnotation(DeleteMapping.class);
        optMethod.put("optName",method.getName());
        optMethod.put("optUrl",StringBaseOpt.castObjectToString(methodMapping.value()));
        //GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        optMethod.put("optReq",StringBaseOpt.castObjectToString("DELETE"));
    }

    public static JSONObject mapControllerInfo(Class clazz){
        JSONObject optInfo = new JSONObject(8);
        mapOptInfo(optInfo, clazz);
        JSONArray optMethodList = new JSONArray(20);

        for(Method method : clazz.getMethods()){
            if(method.isAnnotationPresent(RequestMapping.class)){
                JSONObject optMethod = new JSONObject(10);
                optMethod.put("optId",optInfo.get("optId"));
                mapRequestInfo(optMethod, method);
                optMethodList.add(optMethod);
            } else if(method.isAnnotationPresent(GetMapping.class)){
                JSONObject optMethod = new JSONObject(10);
                optMethod.put("optId",optInfo.get("optId"));
                mapGetRequestInfo(optMethod, method);
                optMethodList.add(optMethod);
            } else if(method.isAnnotationPresent(PostMapping.class)){
                JSONObject optMethod = new JSONObject(10);
                optMethod.put("optId",optInfo.get("optId"));
                mapPostRequestInfo(optMethod, method);
                optMethodList.add(optMethod);
            } else if(method.isAnnotationPresent(PutMapping.class)){
                JSONObject optMethod = new JSONObject(10);
                optMethod.put("optId",optInfo.get("optId"));
                mapPutRequestInfo(optMethod, method);
                optMethodList.add(optMethod);
            } else if(method.isAnnotationPresent(DeleteMapping.class)){
                JSONObject optMethod = new JSONObject(10);
                optMethod.put("optId",optInfo.get("optId"));
                mapDeleteRequestInfo(optMethod, method);
                optMethodList.add(optMethod);
            }
        }
        optInfo.put("optMethods",optMethodList);
        return optInfo;
    }

    public static JSONArray mapControllerInfosByPackage(String packageName) {
        List<Class<?>> classes = ClassScannerOpt.getClassList(packageName ,
                true, Controller.class);
        JSONArray optInfoList = new JSONArray(100);
        for(Class clazz : classes){
            //System.out.println(clazz.getTypeName());
            optInfoList.add(mapControllerInfo(clazz));
        }

        classes = ClassScannerOpt.getClassList(packageName ,
                true, RestController.class);
        for(Class clazz : classes){
            optInfoList.add(mapControllerInfo(clazz));
        }
        return optInfoList;
    }
}
