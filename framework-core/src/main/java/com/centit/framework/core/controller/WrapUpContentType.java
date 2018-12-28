package com.centit.framework.core.controller;

/**
 * 包装器内容类别
 */
public enum WrapUpContentType {
    DATA, // json data
    RAW,
    JAVASCRIPT,
    IMAGE,
    XML,
    HTML,
    FILE,
    MAP_DICT, // 将对象中的 DictionaryMap 注解 映射为 对应的字段添加到json
    PAGE_QUERY, // 分页查询，返回的将诶过必须为 Pair ， 左边为 objList 右边为 pageDesc
    MAP_DICT_PAGE_QUERY // 分页查询，返回的将诶过必须为 Pair ， 左边为 objList  右边为 pageDesc
                        // 并将左边的 list 做 MAP_DICT 转换
}
