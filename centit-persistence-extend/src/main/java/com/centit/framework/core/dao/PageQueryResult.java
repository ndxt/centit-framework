package com.centit.framework.core.dao;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.ToResponseData;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Collection;

/**
 * 功能，创建作为分页查询的返回结果，结果包括objList数据部分和pageDesc分页信息部分
 * 这个对象还复制对 返回结果中的内容进行 数据字段转换 和 字段属性过滤功能
 * @param <T> 模板对象，对于JSONArray来说这个对象就是 Object
 */
@ApiModel(description = "分页查询返回结果")
public class PageQueryResult<T> implements ToResponseData, Serializable {
    private static final long serialVersionUID = 1L;

    // 同 BaseController.OBJLIST 耦合代码
    public static final String OBJECT_LIST_LABEL = "objList";
    // 同 BaseController.PAGE_DESC 耦合代码
    public static final String PAGE_INFO_LABEL = "pageDesc";

    @ApiModelProperty(value = "查询结果")
    private Collection<T> objList;
    @ApiModelProperty(value = "分页信息")
    private PageDesc pageDesc;
    // 是否做数据字段映射
    private boolean mapDictionary;
    // 属性过滤器，如果不为null则只有类表中的字段才会被返回
    private String[] filterFields;

    private PageQueryResult(){
    }

    private static <D> PageQueryResult<D>
    innerCreateResult(Collection<D> objList, PageDesc pageDesc, boolean mapDict, String[] filterFields){
        PageQueryResult result = new PageQueryResult();
        result.objList = objList;
        result.pageDesc = pageDesc;
        result.mapDictionary = mapDict;
        result.filterFields = filterFields;
        return result;
    }

    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param filterFields 属性过滤器，只有这个列表中的属性才会被返回
     * @param <D> 对象类型
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static <D> PageQueryResult<D>
        createResult(Collection<D> objList, PageDesc pageDesc, String[] filterFields){
        return innerCreateResult(objList, pageDesc, true, filterFields);
    }

    /**
     * 将对象转换为分页返回结果， 对象上如果有DictionaryMap注解的属性，将别数据转换为指定的属性添加到JSON中
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param filterFields 属性过滤器，只有这个列表中的属性才会被返回
     * @param <D> 对象类型
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static <D> PageQueryResult<D>
        createResultMapDict(Collection<D> objList, PageDesc pageDesc, String[] filterFields){
        return innerCreateResult(objList, pageDesc, true, filterFields);
    }

    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param <D> 对象类型
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static <D> PageQueryResult<D>
        createResult(Collection<D> objList, PageDesc pageDesc){
        return innerCreateResult(objList, pageDesc, true, null);
    }

    /**
     * 将对象转换为分页返回结果， 对象上如果有DictionaryMap注解的属性，将别数据转换为指定的属性添加到JSON中
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param <D> 对象类型
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static <D> PageQueryResult<D>
        createResultMapDict(Collection<D> objList, PageDesc pageDesc){
        return innerCreateResult(objList, pageDesc, true, null);
    }

    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static PageQueryResult<Object>
        createJSONArrayResult(JSONArray objList, PageDesc pageDesc){
        return innerCreateResult(objList, pageDesc, false, null);
    }

    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param filterFields 属性过滤器，只有这个列表中的属性才会被返回
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static PageQueryResult<Object>
    createJSONArrayResult(JSONArray objList, PageDesc pageDesc, String[] filterFields){
        return innerCreateResult(objList, pageDesc, false, filterFields);
    }
    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param objTypes 需要做数据字典映射的对象，可以是多个
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static PageQueryResult<Object>
        createJSONArrayResult(JSONArray objList, PageDesc pageDesc, Class<?>... objTypes){
        return innerCreateResult(
            DictionaryMapUtils.mapJsonArray(objList, objTypes),
            pageDesc, false, null);
    }

    /**
     * 将对象转换为分页返回结果
     * @param objList 查询返回结果，需要 Collection 类型，并且类别中的对象不能为null
     * @param pageDesc 分页信息
     * @param filterFields 属性过滤器，只有这个列表中的属性才会被返回
     * @param objTypes 需要做数据字典映射的对象，可以是多个
     * @return ToResponseData 用于 WarpUpResponseBody 的处理
     */
    public static PageQueryResult<Object>
    createJSONArrayResult(JSONArray objList, PageDesc pageDesc, String[] filterFields, Class<?>... objTypes){
        return innerCreateResult(
            DictionaryMapUtils.mapJsonArray(objList, objTypes),
            pageDesc, false, filterFields);
    }

    @Override
    public ResponseData toResponseData(){

        ResponseMapData respData = new ResponseMapData();
        if(this.mapDictionary){
            respData.addResponseData(PageQueryResult.OBJECT_LIST_LABEL,
                DictionaryMapUtils.objectsToJSONArray(this.objList, this.filterFields));
        } else {
            if(this.filterFields != null && this.filterFields.length > 0){
                respData.addResponseData(PageQueryResult.OBJECT_LIST_LABEL,
                    DictionaryMapUtils.objectsToJSONArrayNotMapDict(this.objList, this.filterFields));
            }else {
                respData.addResponseData(PageQueryResult.OBJECT_LIST_LABEL, this.objList);
            }
        }
        respData.addResponseData(PageQueryResult.PAGE_INFO_LABEL, this.pageDesc);

        return respData;
    }

    public Collection<T> getObjList() {
        return objList;
    }

    public void setObjList(Collection<T> objList) {
        this.objList = objList;
    }

    public PageDesc getPageDesc() {
        return pageDesc;
    }

    public void setPageDesc(PageDesc pageDesc) {
        this.pageDesc = pageDesc;
    }
}
