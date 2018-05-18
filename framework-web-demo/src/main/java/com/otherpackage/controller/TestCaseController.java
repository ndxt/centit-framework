package com.otherpackage.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.core.controller.BaseController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Random;

@RestController
@RequestMapping("/test")
public class TestCaseController extends BaseController {


    @GetMapping("/sayhello/{userName}")
    public ResponseData getLsh(@PathVariable String userName){
        return ResponseSingleData.makeResponseData("hello "+userName+" !");
    }

    /**
     * 测试 H5 新特性 server-sent-event
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/sse")
    public void serverSentEvent(HttpServletResponse response)
    {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        try {
            for(int i=0;i<10; i++) {
                PrintWriter writer = response.getWriter();
                //这里需要\n\n，必须要，不然前台接收不到值,键必须为data
                writer.write("data "+ i + " : 中文测试 \n\n");
                writer.flush();
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //produces = "text/event-stream"
    @RequestMapping(value="/sse2",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody String serverSentEvent2() {
        Random r = new Random();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //这里需要\n\n，必须要，不然前台接收不到值,键必须为data
        return "data:Testing 1,2,3" + r.nextInt() +"\n\n";
    }
}
