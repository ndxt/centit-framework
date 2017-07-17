package com.centit.framework.model.adapter;
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
 * @author ljy
 * @create 2012-2-22
 */
public interface MessageSender {

    /**
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @return "OK" 表示成功，其他的为错误信息
     */
    String sendMessage(String sender, String receiver, String msgSubject, String msgContent);
    
    

    /**
     * 
     * 发送内部系统消息
     *
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1&b=v2
     * @return "OK" 表示成功，其他的为错误信息
     */
    String sendMessage(String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag);
   

}
