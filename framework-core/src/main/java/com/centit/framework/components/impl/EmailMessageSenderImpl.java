/**
 * 
 */
package com.centit.framework.components.impl;

import com.centit.framework.common.SysParametersUtils;
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

    private String hostName;
    private int smtpPort;
    private String userName;
    private String userPassword;
    private String serverEmail;

    public  EmailMessageSenderImpl(){
        this.hostName = SysParametersUtils.getStringValue("message.sender.email.hostName");
        this.smtpPort = SysParametersUtils.getIntValue("message.sender.email.smtpPort",25);
        this.userName = SysParametersUtils.getStringValue("message.sender.email.userName");
        this.userPassword = SysParametersUtils.getStringValue("message.sender.email.userPassword");
        this.serverEmail = SysParametersUtils.getStringValue("message.sender.email.serverEmail");
    }

    public String sendEmailMessage(String mailTo,String mailFrom,String msgSubject,String msgContent) {
        
        MultiPartEmail multMail = new MultiPartEmail();
        
        // SMTP
        multMail.setHostName(hostName);
                //CodeRepositoryUtil.getValue("SysMail", "host_name"));
        multMail.setSmtpPort(smtpPort);

        String resStr = "OK";
        // 需要提供公用的消息用户名和密码
        multMail.setAuthentication(userName, userPassword);
                //CodeRepositoryUtil.getValue("SysMail", "host_user"),
                //CodeRepositoryUtil.getValue("SysMail", "host_password"));
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
            mailFrom = serverEmail;
            //CodeRepositoryUtil.getValue("SysMail", "admin_email");
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
