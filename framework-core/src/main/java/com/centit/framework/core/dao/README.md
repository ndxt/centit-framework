# 数据字典映射工具

数据字典的作用参见[数据字典](https://ndxt.github.io/system_design/concept_design.html#%E6%95%B0%E6%8D%AE%E5%AD%97%E5%85%B8)。

## 定义

在po的字段上用 DictionaryMap 注解添加映射关系。其中：
1. value 标识数据字典的catalog值。
2. fieldName 为将字典中的值映射到新的属性上。

## 实现

DictionaryMapUtils 实现对DictionaryMap映射关系的处理。他只用于将po转化为JSON。