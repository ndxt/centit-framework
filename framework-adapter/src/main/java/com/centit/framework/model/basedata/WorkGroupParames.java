package com.centit.framework.model.basedata;

import lombok.Data;

/**
 * 移交组长使用参数对象
 */
@Data
public class WorkGroupParames {
    //这3个字段为主键
    private String groupId;

    private String userCode;

    //新组长code
    private String newUserCode;
}
