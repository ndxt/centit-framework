package com.centit.framework.common;

public abstract class GlobalConstValue {
    //'all'代表返回所有租户的内容，这是topunit这个属性作为租户可以有其他的解释
    public static final String NO_TENANT_TOP_UNIT = "all";
    //用户没有登录获取不到租户
    public static final String UNKNOW_TENANT_TOP_UNIT = "none";
    //系统管理员、系统维护相关业务的租户为 system；称为系统租户代码
    public static final String SYSTEM_TENANT_TOP_UNIT = "system";
    //全局应用归属租户
    public static final String GLOBAL_TENANT_TOP_UNIT = "global";
    //用户职务数据字典
    public static final String DATA_CATALOG_RANK = "RankType";
    //在多租户系统中用户职务数据字典为 租户代码+这个后缀
    public static final String DATA_CATALOG_RANK_SUFFIX = "-RT";
    //用户岗位数据字典
    public static final String DATA_CATALOG_STATION = "StationType";
    //在多租户系统中用户岗位数据字典为 租户代码+这个后缀
    public static final String DATA_CATALOG_STATION_SUFFIX = "-ST";
}
