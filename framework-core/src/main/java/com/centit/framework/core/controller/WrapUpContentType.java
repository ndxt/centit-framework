package com.centit.framework.core.controller;

/**
 * 包装器内容类别
 */
public enum WrapUpContentType {
    DATA, // json data，默认属性，返回json格式的数据
    RAW,  // 返回函数返回值的元素数据，如果不是String类型，转换为String类型，非标量类型转换为json， 等价于 @RespondBody
    JAVASCRIPT, // 返回脚本
    IMAGE, // 返回图片流
    XML,  //将对象转换为xml返回
    HTML, // 返回html文本
    FILE, // 返回文件流
    MAP_DICT, // 将对象中的 DictionaryMap 注解 映射为 对应的字段添加到json
    BASE64 // 对data部分进行 base64 编码
}
