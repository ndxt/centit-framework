package com.centit.framework.components.impl;

import com.centit.framework.model.adapter.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by codefan on 17-7-24.
 */
public class DummyMessageSenderImpl implements MessageSender{
    private DummyMessageSenderImpl(){

    }
    public final static DummyMessageSenderImpl instance = new DummyMessageSenderImpl();

    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent) {
        return "ok";
    }

    @Override
    public String sendMessage(String sender, String receiver, String msgSubject, String msgContent, String optId, String optMethod, String optTag) {
        return "ok";
    }
}