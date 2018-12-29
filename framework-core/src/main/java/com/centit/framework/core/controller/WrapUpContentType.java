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
    MAP_DICT // 将对象中的 DictionaryMap 注解 映射为 对应的字段添加到json
}
