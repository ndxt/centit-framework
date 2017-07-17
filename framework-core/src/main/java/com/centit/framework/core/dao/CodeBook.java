package com.centit.framework.core.dao;

/**
 * @author codefan@centit.com
 */
public class CodeBook {

    /*
     * like字段匹配的map名称
     */
    public static final String LIKE_HQL_ID = "LIKE";
    public static final String EQUAL_HQL_ID = "EQUAL";
    public static final String NO_PARAM_FIX = "NP_";
    public static final String IN_HQL_ID = "IN";
    /*
     * orderby
     */
    /**
     * 默认的排序，放到 filterField 中
     */
    public static final String ORDER_BY_HQL_ID = "ORDER BY";
    
    /**
     * 用户自定义排序描述，  放到 filterDesc 中
     */
    public static final String SELF_ORDER_BY = "ORDER_BY";
    /**
     * 用户自定义排序字段 ， 放到 filterDesc 中
     */
    public static final String TABLE_SORT_FIELD = "sort";
    /**
     * 用户自定义排序字段的排序顺序 ， 放到 filterDesc 中
     */
    public static final String TABLE_SORT_ORDER = "order";

}