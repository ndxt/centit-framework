package com.centit.framework.model.adapter;

import com.centit.framework.common.ResponseData;
import com.centit.framework.model.basedata.NoticeMessage;
import com.centit.support.common.DoubleAspect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统消息发送接口
 * 系统的内置的两个发送消息的实现,可以通过以下代码引入
        Resource(name = "innerMessageManager")
        NotNull
        private MessageSender innerMessageSender;

        Resource(name = "emailMessageSender")
        NotNull
        private MessageSender emailMessageSender;
         注意：去掉@后的空格
 * @author codefan
 */
public interface MessageSender {

     /**
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
     ResponseData sendMessage(String sender, String receiver, NoticeMessage message);

    /**
     * 批量发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receivers   接收人内部用户编码
     * @param message 消息主体
     * @return "OK" 表示成功，其他的为错误信息
     */
    default ResponseData sendMessage(String sender, Collection<String> receivers, NoticeMessage message){
        int error = 0; int success = 0;
        Map<String, Object> result = new HashMap<>();
        for (String receiver : receivers){
            ResponseData response = sendMessage(sender, receiver, message);
            if(response.getCode() !=0){
                error ++;
                result.put(receiver, response.getMessage());
            } else {
                success ++;
            }
        }
        String msgStr = "一共发送" + (error+success) + "条消息，成功"+success+"条，失败"+error+"条。";
        int resCode = error == 0? 0:(success==0?2:3);
        return ResponseData.makeErrorMessageWithData(result, resCode, msgStr);
    }

    /**
     * 广播信息
     * @param sender 发送人内部用户编码
     * @param message  消息主体
     * @param userInline DoubleAspec.ON 在线用户  OFF 离线用户 BOTH 所有用户
     * @return  默认没有实现
     */
    default ResponseData broadcastMessage(String sender, NoticeMessage message, DoubleAspect userInline){
        return ResponseData.errorResponse;
    }

}
