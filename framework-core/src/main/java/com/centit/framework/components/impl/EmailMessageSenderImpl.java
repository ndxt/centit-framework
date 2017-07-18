/**
 * 
 */
package com.centit.framework.components.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.basedata.IUserInfo;

/**
 * 
 * @author ljy codefan
 * 2012-2-22
 */
public class EmailMessageSenderImpl implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessageSenderImpl.class);
    private EmailMessageSenderImpl(){
    	
    }
    public final static EmailMessageSenderImpl instance = new EmailMessageSenderImpl();

    public static String sendEmailMessage(String mailTo,String mailFrom,String msgSubject,String msgContent) {
        
        MultiPartEmail multMail = new MultiPartEmail();
        
        // SMTP
        multMail.setHostName(CodeRepositoryUtil.getValue("SysMail", "host_name"));
        String resStr = "OK";
        // 需要提供公用的消息用户名和密码
        multMail.setAuthentication(
                CodeRepositoryUtil.getValue("SysMail", "host_user"),
                CodeRepositoryUtil.getValue("SysMail", "host_password"));
        try {
            //multMail.setFrom(CodeRepositoryUtil.getValue("SysMail", "admin_email"));
            multMail.setFrom(mailFrom);
            multMail.addTo(mailTo);
            multMail.setSubject(msgSubject);
            multMail.setMsg(msgContent);
            multMail.send();
            return "OK";
        } catch (EmailException e) {
            resStr=e.getMessage();
            logger.error(e.getMessage(),e);
            //e.printStackTrace();
        }
        return resStr;
    }
    
    @Override
    public String sendMessage(String sender,String receiver ,String msgSubject,String msgContent){
        IUserInfo userinfo = CodeRepositoryUtil.getUserInfoByCode(sender);
        String mailFrom;
        if(userinfo==null){
            mailFrom = CodeRepositoryUtil.getValue("SysMail", "admin_email");
        }else
            mailFrom =  userinfo.getRegEmail();
        
        userinfo = CodeRepositoryUtil.getUserInfoByCode(receiver);
        if(userinfo==null){
            logger.error("找不到用户："+receiver);
            return "找不到用户："+receiver;
        }
        String email = userinfo.getRegEmail();
     
        if(email!=null && !"".equals(email))
            return sendEmailMessage (email,mailFrom, msgSubject, msgContent); 
        else
            return "用户："+receiver+"没有设置注册邮箱";
    }

    @Override
    public String sendMessage(String sender, String receiver,
            String msgSubject, String msgContent, String optId,
            String optMethod, String optTag) {
        return sendMessage(sender,  receiver,
                 msgSubject,  msgContent);
    }
}
