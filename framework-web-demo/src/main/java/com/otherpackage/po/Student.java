package com.otherpackage.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "学生信息")
public class Student {
    @ApiModelProperty(value = "学号")
    private String studNo;

    @ApiModelProperty(value = "姓名")
    private String studName;

    public String getStudNo() {
        return studNo;
    }

    public void setStudNo(String studNo) {
        this.studNo = studNo;
    }

    public String getStudName() {
        return studName;
    }

    public void setStudName(String studName) {
        this.studName = studName;
    }
}
