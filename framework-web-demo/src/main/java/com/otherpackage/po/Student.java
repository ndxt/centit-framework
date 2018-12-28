package com.otherpackage.po;

import com.centit.framework.core.dao.DictionaryMap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "学生信息")
public class Student {
    @ApiModelProperty(value = "学号")
    private String studNo;

    @ApiModelProperty(value = "姓名")
    private String studName;
    /**
     * Y / N 是否为男的
     */
    @DictionaryMap(value=  "YesOrNo", fieldName = "isManText")
    private String isMan;

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

    public String getIsMan() {
        return isMan;
    }

    public void setIsMan(String isMan) {
        this.isMan = isMan;
    }
}
