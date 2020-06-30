package com.centit.framework.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.basedata.NoticeMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/utils")
@Api(value="框架工具类接口",
    tags= "框架工具类接口")
public class UtilsController {

    @Autowired
    private NotificationCenter notificationCenter;

    @ApiOperation(value ="单位发送消息接口")
    @RequestMapping(value = "/sendUnitMsg", method = RequestMethod.POST)
    @ApiImplicitParam(
        name = "msgJson", value = "sender:发送人,unitCode:接收单位(必填),optId:表单ID,optTag:业务主键," +
        "title:标题, message:内容",
        required = true, paramType = "body", dataType = "String"
    )
    @WrapUpResponseBody
    public ResponseData sendUnitMessage(@RequestBody JSONObject msgJson){
        if(null==msgJson.getString("unitCode")) return null;
        return notificationCenter.sendUnitMessage(msgJson.getString("sender"),
            msgJson.getString("unitCode"), false,
            NoticeMessage.create().operation(msgJson.getString("optId"))
                .tag(msgJson.getString("optTag"))
                .subject(msgJson.getString("title"))
                .content(msgJson.getString("message")));
    }
}
