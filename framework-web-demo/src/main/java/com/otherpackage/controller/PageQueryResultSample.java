package com.otherpackage.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.ToResponseData;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.support.database.utils.PageDesc;

import java.util.Collection;

/**
 * 一个测试示例，在 centit-persistence-extend.jar 中有
 * PageQueryResult 的实现，作为分页查询请使用这个类
 * 这个类仅仅用于测试， 业务项目中需要删除
 */
public class PageQueryResultSample<T> implements ToResponseData {
    private Collection<T> objList;
    private PageDesc pageDesc;
    private boolean mapDictionary;

    private PageQueryResultSample() {

    }

    public static <D> PageQueryResultSample<D>
    createResult(Collection<D> objList, PageDesc pageDesc) {
        PageQueryResultSample result = new PageQueryResultSample();
        result.objList = objList;
        result.pageDesc = pageDesc;
        result.mapDictionary = false;
        return result;
    }

    public static <D> PageQueryResultSample<D>
    createResultMapDict(Collection<D> objList, PageDesc pageDesc) {
        PageQueryResultSample result = new PageQueryResultSample();
        result.objList = objList;
        result.pageDesc = pageDesc;
        result.mapDictionary = true;
        return result;
    }

    @Override
    public ResponseData toResponseData() {

        ResponseMapData respData = new ResponseMapData();
        if (this.mapDictionary) {
            respData.addResponseData("objList",
                DictionaryMapUtils.objectsToJSONArray(this.objList));
        } else {
            respData.addResponseData("objList", this.objList);
        }
        respData.addResponseData("pageDesc", this.pageDesc);

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
