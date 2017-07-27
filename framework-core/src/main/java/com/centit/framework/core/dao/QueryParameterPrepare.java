package com.centit.framework.core.dao;

import java.util.Map;

/**
 * Created by codefan on 17-7-25.
 */
public abstract class QueryParameterPrepare {
    /**
     * 处理翻页参数
     * @param pageQureyMap pageQureyMap
     * @param pageDesc pageDesc
     * @param total total
     * @return Map类型
     */
    public final static Map<String, Object> prepPageParmers
            (Map<String, Object> pageQureyMap, PageDesc pageDesc, int total) {

        int pageNo=pageDesc.getPageNo() ;
        int pageSize=pageDesc.getPageSize();
        int maxPageNo = (total - 1) / pageSize + 1;
        if (maxPageNo < pageNo) {
            pageNo = maxPageNo;// 页码校验
        }
        int start = (pageNo - 1) * pageSize;
        int end = pageNo * pageSize;
        pageQureyMap.put("startRow", new Integer(start));
        pageQureyMap.put("endRow", new Integer(end));
        pageQureyMap.put("maxSize", new Integer(pageSize));
        //回写总数量
        pageDesc.setTotalRows(total);
        //System.err.println("pageQureyMap========"+JSON.toJSONString(pageQureyMap));
        return pageQureyMap;
    }
}
