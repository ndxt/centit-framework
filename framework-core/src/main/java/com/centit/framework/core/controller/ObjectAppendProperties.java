package com.centit.framework.core.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ToResponseData;
import com.centit.support.algorithm.CollectionsOpt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * 功能，在返回结果对象中添加额外的属性，比如数据字典映射值等等
 * @param <T> 模板对象，可以是任何类型
 */
@ApiModel(description = "在返回结果对象中添加额外的属性，比如数据字典映射值等等")
public class ObjectAppendProperties<T> implements ToResponseData {

    @ApiModelProperty(value = "结果对象")
    private T object;
    @ApiModelProperty(value = "额外属性")
    private Map<String, Object> extendProperties;

    /**
     * 在返回结果对象中添加额外的属性，比如数据字典映射值等等
     * @param obj 对象
     * @param extendProperties 额外属性
     */
    public ObjectAppendProperties(T obj, Map<String, Object> extendProperties){
        this.object = obj;
        this.extendProperties = extendProperties;
    }

    /**
     * 在返回结果对象中添加额外的属性，比如数据字典映射值等等
     * @param obj 对象
     * @param extendProperties 额外属性
     * @param <D> 对象类型
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static <D> ObjectAppendProperties<D>
        create(D obj, Map<String, Object> extendProperties){
        return new ObjectAppendProperties(obj, extendProperties);
    }

    @Override
    public ResponseData toResponseData(){
        Map<String, Object> objectMap = CollectionsOpt.objectToMap(object);
        objectMap.putAll(extendProperties);
        return ResponseData.makeResponseData(objectMap);
    }

}
