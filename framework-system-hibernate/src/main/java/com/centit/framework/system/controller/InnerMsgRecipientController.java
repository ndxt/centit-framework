package com.centit.framework.system.controller;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.SysDaoOptUtils;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.po.InnerMsg;
import com.centit.framework.system.po.InnerMsgRecipient;
import com.centit.framework.system.service.InnerMsgManager;
import com.centit.framework.system.service.InnerMsgRecipientManager;
import com.centit.support.json.JsonPropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/innermsgrecipient")
public class InnerMsgRecipientController extends BaseController {
    @Resource
    public InnerMsgRecipientManager innerMsgRecipientManager;

    @Resource(name="innerMessageManager")
    @NotNull
    public InnerMsgManager innerMsgManager;

    /**
     * 查询收件箱
     * @param field     显示结果中只需要显示的字段
     * @param pageDesc  PageDesc
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/inbox", method = { RequestMethod.GET })
    public void listInbox(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);

        String receive = (String) searchColumn.get("receive");
        if (StringUtils.isBlank(receive)) {
            searchColumn.put("receive", WebOptUtils.getLoginUser(request).getUserCode());
        }
        String s = (String) searchColumn.get("msgTitle");
        if (null != s && StringUtils.isNotBlank(s)) {
            searchColumn.put("msgTitle", "%" + s + "%");
        }
        
        s = (String) searchColumn.get("msgContent");
        if (null != s && StringUtils.isNotBlank(s)) {
            searchColumn.put("msgContent", "%" + s + "%");
        }
        List<InnerMsgRecipient> listObjects = null;
        if (null == pageDesc)
            listObjects = innerMsgRecipientManager.listObjects(searchColumn);
        else
            listObjects = innerMsgRecipientManager.listObjects(searchColumn,pageDesc);
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST,SysDaoOptUtils.objectsToJSONArray(listObjects));
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response, JsonPropertyUtils.getIncludePropPreFilter(InnerMsgRecipient.class, field));
    }
    
    @RequestMapping(value = "/unreadMsgCount", method = { RequestMethod.GET })
    public void unreadMsgCount(HttpServletRequest request, HttpServletResponse response) {
        String currUser = WebOptUtils.getLoginUser(request).getUserCode();
        long unreadMsg = innerMsgRecipientManager.getUnreadMessageCount(currUser);
        JsonResultUtils.writeSingleDataJson(unreadMsg, response);
    } 
    /**
     * 查询发件箱
     * 
     * @param field      显示结果中只需要显示的字段
     * @param pageDesc   PageDesc
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     */
    @RequestMapping(value = "/outbox", method = { RequestMethod.GET })
    public void listOutbox(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        
        String sender = (String) searchColumn.get("sender");
        if (StringUtils.isBlank(sender)) {
            searchColumn.put("sender", WebOptUtils.getLoginUser(request).getUserCode());
        }

        List<InnerMsg> listObjects = null;
        if (null == pageDesc)
            listObjects = innerMsgManager.listObjects(searchColumn);
        else
            listObjects = innerMsgManager.listObjects(searchColumn,pageDesc);
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, SysDaoOptUtils.objectsToJSONArray(listObjects));
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response, JsonPropertyUtils.getIncludePropPreFilter(InnerMsgRecipient.class, field));
    }

    /**
     * 是否有发公告权限
     * @param response   HttpServletResponse
     */
    @RequestMapping(value = "/cangivenotify", method = { RequestMethod.GET })
    public void cangivenotify(HttpServletResponse response) {
        boolean s = CodeRepositoryUtil
                .checkUserOptPower("MSGMAG", "givenotify");
        JsonResultUtils.writeSingleDataJson(s, response);
    }
    
    /**
     * 是否有发公告权限
     * @param msgCode   msgCode
     * @param response   HttpServletResponse
     */
    @RequestMapping(value = "/{msgCode}", method = { RequestMethod.GET })
    public void getInnerMsg(@PathVariable String msgCode, HttpServletResponse response) {
        InnerMsgRecipient msgCopy = innerMsgRecipientManager.getObjectById(msgCode);
        JsonResultUtils.writeSingleDataJson(msgCopy, response);
    }

    /**
     * 公告列表
     * 
     * @param field       显示结果中只需要显示的字段
     * @param pageDesc    PageDesc
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     */
    @RequestMapping(value = "/notice", method = { RequestMethod.GET })
    public void listnotify(String[] field, PageDesc pageDesc,
            HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        searchColumn.put("msgType", "A");
        List<InnerMsg> listObjects = innerMsgManager.listObjects(searchColumn,pageDesc);
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, SysDaoOptUtils.objectsToJSONArray(listObjects));
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response, JsonPropertyUtils
                .getIncludePropPreFilter(InnerMsg.class, field));
    }

    /**
     * 按部门发公告，会匹配该部门以及所有子部门的用户，群发消息
     * @param unitCode unitCode
     * @param innerMsg  InnerMsg
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws Exception  Exception
     */
    @RequestMapping(value = "/notify/{unitCode}", method = { RequestMethod.POST })
    public void noticeByUnit(@PathVariable String unitCode,@Valid InnerMsg innerMsg,HttpServletRequest request, HttpServletResponse response) throws Exception {
       
        if (!StringUtils.isNotBlank(innerMsg.getSender())) {
            innerMsg.setSender(WebOptUtils.getLoginUser(request).getUserCode());
            //innerMsg.setSenderName(WebOptUtils.getLoginUserName(request));
        }
        if (null == innerMsg.getSendDate()) {
            innerMsg.setSendDate(new Date());
        }
        innerMsgRecipientManager.noticeByUnitCode(unitCode,innerMsg);
        JsonResultUtils.writeSuccessJson(response);
    }
 
    
    /**
     * 发送或群发消息，recipient必须包含mInnerMsg对象属性，recipient.receive传入是由userCode拼接成的字符串，以逗号隔开
     * @param recipient InnerMsgRecipient
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/sendMsg", method = { RequestMethod.POST })
    public void sendMsg(@Valid InnerMsgRecipient recipient,HttpServletRequest request,
            HttpServletResponse response) {
        innerMsgRecipientManager.sendMsg(recipient,this.getLoginUser(request).getUserCode());
        //DataPushSocketServer.pushMessage(recipient.getReceive(), "你有新邮件："+ recipient.getMsgTitle());
        JsonResultUtils.writeSingleDataJson(recipient, response);
    }
    
    
    
    /**
     * 获取当前登录用户
     * @param request HttpServletReqeust
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/loginuser", method = { RequestMethod.GET })
    public void getLoginUserCode(HttpServletResponse response,
            HttpServletRequest request) {
        String userCode = this.getLoginUser(request).getUserCode();
        JsonResultUtils.writeSingleDataJson(userCode, response);
    }

    /**
     * 更新消息内容
     * @param msg  InnerMsg
     * @param msgCode  消息编号
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/{msgCode}", method = { RequestMethod.PUT })
    public void mergInnerMsg(@Valid InnerMsg msg, @PathVariable String msgCode,
            HttpServletResponse response) {
        InnerMsg msgCopy = innerMsgManager.getObjectById(msgCode);
        if (null == msgCopy) {
            JsonResultUtils.writeErrorMessageJson("当前机构中无此信息", response);
            return;
        }
        innerMsgManager.mergeMInnerMsg(msgCopy, msg);
        // 需要返回msg的msgCode给前端recipient保存用
        JsonResultUtils.writeSingleDataJson(msg, response);
    }

    /**
     * 更新接受者信息
     * @param recipient  InnerMsgRecipient
     * @param id 接收者信息编号
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "recipient/{id}", method = { RequestMethod.PUT })
    public void mergInnerMsgRecipient(@Valid InnerMsgRecipient recipient,
            @PathVariable String id, HttpServletResponse response) {
        InnerMsgRecipient recipientCopy = innerMsgRecipientManager
                .getObjectById(id);
        if (null == recipientCopy) {
            JsonResultUtils.writeErrorMessageJson("当前机构中无此信息", response);
            return;
        }
        innerMsgRecipientManager.mergeRecipient(recipientCopy, recipient);
        // 需要返回msg的msgCode给前端recipient保存用
        JsonResultUtils.writeSingleDataJson(recipient, response);
    }

    /**
     * 删除消息,并没有删除该条记录，而是把msgState字段标记为D
     * @param msgCode 消息编号
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{msgCode}", method = { RequestMethod.DELETE })
    public void deleteMsg(@PathVariable String msgCode,
            HttpServletResponse response) {
        innerMsgManager.deleteMsgById(msgCode);
        JsonResultUtils.writeBlankJson(response);
    }

    /**
     * 删除接受者信息,并没有删除该条记录，而是把msgState字段标记为D
     * @param id 接受者信息编号
     * @param request HttpServletRequest
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/recipient/{id}", method = { RequestMethod.DELETE })
    public void deleteRecipient(@PathVariable String id,
    		 HttpServletRequest request,HttpServletResponse response) {
        /*InnerMsgRecipient recipient = innerMsgRecipientManager
                .getObjectById(id);*/
        innerMsgRecipientManager.deleteOneRecipientById(id);;
        JsonResultUtils.writeBlankJson(response);
        //(request, optId, optTag, optMethod, optContent, oldObject); 
        OperationLogCenter.logDeleteObject(request, "recipient", id ,OperationLog.P_OPT_LOG_METHOD_D,
                 "删除接收这信息","");

    }

    
    /**
     * 往来消息列表
     * @param sender 用户1
     * @param receiver 用户2
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/{sender}/{receiver}", method = { RequestMethod.GET })
    public void getMsgExchanges(@PathVariable String sender,
            @PathVariable String receiver, HttpServletResponse response) {
        List<InnerMsgRecipient> recipientlist = innerMsgRecipientManager
                .getExchangeMsgs(sender, receiver);
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, recipientlist);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

}
