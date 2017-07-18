package com.centit.framework.model.adapter;

/**
 * 
 * 系统的内置的通知中心bean名称为  notificationCenter 
 * 在使用的地方 只要添加如下代码
        /@ Resource
        /@ NotNull
        private NotificationCenter notificationCenter;
         就可以使用，注意：去掉@后的空格
         通知中心内置了两个发送消息的方法
         sendType "I"
        /@ Resource(name = "InnerMessageManager")
        /@ NotNull
        private MessageSender innerMessageSender;
        
        sendType "I"        
        /@ Resource(name = "emailMessageSender")
        /@ NotNull
        private MessageSender emailMessageSender;
 * @author codefan
 */
public interface NotificationCenter {
    /**
     * 注册新的发送消息通知
     * @param sendType String 发送类型
     * @param sender MessageSender
     * @return NotificationCenter 系统的内置的通知中心
     */
    public NotificationCenter registerMessageSender(String sendType,MessageSender sender);

    /**
     * 设置默认的发送通知内部
     * @param sendType 发送类型
     * @return NotificationCenter 系统的内置的通知中心
     */
    public MessageSender setDefaultSendType(String sendType);
    /**
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @return "OK" 表示成功，其他的为错误信息
     */
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent);

    
    /**
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param noticeType   指定发送类别
     * @return "OK" 表示成功，其他的为错误信息
     */
    public String sendMessage(
            String sender, String receiver, String msgSubject, String msgContent,
            String noticeType);
    
    
    /**
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @return "OK" 表示成功，其他的为错误信息
     */
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag);

    
    /**
     * @param sender     发送人内部用户编码
     * @param receiver   接收人内部用户编码
     * @param msgSubject 消息主题
     * @param msgContent 消息内容
     * @param noticeType   指定发送类别
     * @param optId 关联的业务编号
     * @param optMethod 管理的操作
     * @param optTag 业务主键 ，复合主键用URL方式对的格式 a=v1;b=v2
     * @return "OK" 表示成功，其他的为错误信息
     */
    public String sendMessage(
            String sender, String receiver, String msgSubject, String msgContent,
            String optId, String optMethod, String optTag,
            String noticeType);

    

}
