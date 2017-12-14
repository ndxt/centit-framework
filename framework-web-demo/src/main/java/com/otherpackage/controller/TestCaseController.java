package com.otherpackage.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.core.controller.BaseController;
import com.centit.msgpusher.msgpusher.po.SimplePushMessage;
import com.centit.msgpusher.msgpusher.po.SimplePushMsgPoint;
import com.centit.msgpusher.msgpusher.websocket.SocketMsgPusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
public class TestCaseController extends BaseController {

    @Resource
    protected SocketMsgPusher socketMsgPusher;

    @GetMapping("/sayhello/{userName}")
    public ResponseData getLsh(@PathVariable String userName){
        return ResponseSingleData.makeResponseData("hello "+userName+" !");
    }

    @GetMapping("/push/{userCode}/{message}")
    public ResponseData pushMsgToUser(@PathVariable String userCode,HttpServletRequest request){
        String uri = request.getRequestURI();
        String message = uri.substring(uri.lastIndexOf('/')+1);
        try {
            SimplePushMessage msg = new SimplePushMessage(message);
            msg.setMsgReceiver(userCode);
            socketMsgPusher.pushMessage(msg,
                    new SimplePushMsgPoint(userCode));
            return ResponseSingleData.makeResponseData("hello "+userCode+" !");
        } catch (Exception e) {
            return ResponseSingleData.makeResponseData("Error:" + e.getLocalizedMessage());
        }

    }
}
