package com.centit.framework.components.impl;

import com.centit.framework.common.ResponseData;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.basedata.NoticeMessage;

/**
 * Created by codefan on 17-7-24.
 */
public class DummyMessageSenderImpl implements MessageSender{
    private DummyMessageSenderImpl(){

    }
    public final static DummyMessageSenderImpl instance = new DummyMessageSenderImpl();

    @Override
    public ResponseData sendMessage(String sender, String receiver, NoticeMessage message) {
        return ResponseData.successResponse;
    }

}
