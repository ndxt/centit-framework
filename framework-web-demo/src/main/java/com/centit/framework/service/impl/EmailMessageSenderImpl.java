/**
 *
 */
package com.centit.framework.service.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.NoticeMessage;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ljy codefan
 * 2012-2-22
 */
public class EmailMessageSenderImpl implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessageSenderImpl.class);

    //@Value("${message.sender.email.hostName:}")
    //@NotNull
    private String hostName;

    //@Value("${message.sender.email.smtpPort:25}")
    //@NotNull
    private int smtpPort;

    //@Value("${message.sender.email.userName:}")
    private String userName;

    //@Value("${message.sender.email.userPassword:}")
    private String userPassword;

    //@Value("${message.sender.email.serverEmail:}")
    private String serverEmail;

    public EmailMessageSenderImpl(){
        this.smtpPort = 25;
    }

    public EmailMessageSenderImpl(String hostName, int smtpPort){
        this.hostName = hostName;
        this.smtpPort = smtpPort;
    }

    private void sendEmailMessage(String mailTo,String mailFrom,String msgSubject,String msgContent)
        throws EmailException{

        MultiPartEmail multMail = new MultiPartEmail();
        // SMTP
        multMail.setHostName(hostName);
                //CodeRepositoryUtil.getValue("SysMail", "host_name"));
        multMail.setSmtpPort(smtpPort);
         // 需要提供公用的消息用户名和密码
        multMail.setAuthentication(userName, userPassword);
                //CodeRepositoryUtil.getValue("SysMail", "host_user"),
                //CodeRepositoryUtil.getValue("SysMail", "host_password"));
        //multMail.setFrom(CodeRepositoryUtil.getValue("SysMail", "admin_email"));
        multMail.setFrom(mailFrom);
        multMail.addTo(mailTo);
        multMail.setSubject(msgSubject);
        if(msgContent.endsWith("</html>") || msgContent.endsWith("</HTML>")){
            multMail.addPart(msgContent, "text/html;charset=utf-8");
        }else{
            multMail.setMsg(msgContent);
        }
        multMail.send();
    }

    @Override
    public ResponseData sendMessage(String sender, String receiver, NoticeMessage message){
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
            return ResponseData.makeErrorMessage(
                ResponseData.ERROR_USER_NOTFOUND, "找不到用户："+receiver);
        }
        String email = userinfo.getRegEmail();

        if(email!=null && !"".equals(email)) {
            try {
                sendEmailMessage(email, mailFrom, message.getMsgSubject(), message.getMsgContent());
                return ResponseData.successResponse;
            } catch (EmailException e) {
                logger.error(e.getMessage(),e);
                return ResponseData.makeErrorMessage(e.getMessage());
            }
        } else {
            return ResponseData.makeErrorMessage(
                ResponseData.ERROR_USER_CONFIG, "用户：" + receiver + "没有设置注册邮箱");
        }
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setServerEmail(String serverEmail) {
        this.serverEmail = serverEmail;
    }
}
