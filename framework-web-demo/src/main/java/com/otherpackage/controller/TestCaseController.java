package com.otherpackage.controller;

import com.centit.framework.common.*;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.operationlog.RecordOperationLog;
import com.centit.support.algorithm.ByteBaseOpt;
import com.centit.support.common.ParamName;
import com.otherpackage.po.Student;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Random;

@RestController
@Api(value="测试controller",
    tags="测试Http的新特性和ResponseData的结构")
@RequestMapping("/test")
public class TestCaseController extends BaseController {

    @ApiOperation(value="hello",notes="Say hello to somebody.")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "userName", value="用户姓名，向某人说hello",
        required=true, paramType = "path", dataType= "String"
    ))
    @GetMapping("/sayhello/{userName}")
    public ResponseData getLsh(@PathVariable String userName){
        return ResponseSingleData.makeResponseData("hello "+userName+" !");
    }

    @ApiOperation(value="测试16进制编码",notes="将一个字符串按照16进制编码。")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "rawhex", value="用于编码的字符串",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "hf", value="是否式高位在前面，high first",
        required= false, paramType = "query", dataType= "Boolean"
    )})
    @GetMapping("/hex/{rawhex}")
    public ResponseData hexConvert(@PathVariable String rawhex,Boolean hf){

        try {
            String hexStr = rawhex.toUpperCase();
            if (hexStr.length() % 2 == 1) {
                hexStr = "0" + hexStr;
            }
            int len = hexStr.length() > 8 ? 8 : 4;

            byte[] buf = new byte[len];
            //hex = hex.toUpperCase();
            for (int i = 0; i < len; i++) {
                buf[i] = 0;
            }

            byte[] result = Hex.decode(hexStr);
            int rlen = result.length;
            if (hf != null && !hf) {
                for (int i = 0; i < rlen && i < len; i++) {
                    buf[len - 1 - i] = result[i];
                }
            } else {
                for (int i = 0; i < rlen && i < len; i++) {
                    buf[len - rlen + i] = result[i];
                }
            }

            ResponseMapData responseData = new ResponseMapData();

            if (len < 8) {
                responseData.addResponseData("int", ByteBaseOpt.readInt32(buf, 0));
                responseData.addResponseData("float", ByteBaseOpt.readFloat(buf, 0));
                responseData.addResponseData("Hex20180612", Hex.encode(ByteBaseOpt.castObjectToBytes(20180612)));
            } else {
                responseData.addResponseData("long", ByteBaseOpt.readLong(buf, 0));
                responseData.addResponseData("double", ByteBaseOpt.readDouble(buf, 0));
                responseData.addResponseData("date", ByteBaseOpt.readDate(buf, 0));
                responseData.addResponseData("Hex20180612105706", Hex.encode(ByteBaseOpt.castObjectToBytes(20180612105706L)));
            }
            return responseData;
        }catch (RuntimeException re){
            return ResponseData.makeErrorMessage(re.getLocalizedMessage());
        }
    }

    @ApiOperation(value="根据学号查询学生",notes="根据学号查询学生。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "studNo", value="学号必须是两位数",
        required=true, paramType = "path", dataType ="String"
    ))
    @GetMapping("/student/{studNo}")
    @WrapUpResponseBody
    @RecordOperationLog(content = "查询学号为{studNo}学生")
    public Student findStudent(@PathVariable @ParamName("studNo") String studNo) {
        if(StringUtils.length(studNo)!=2){
            throw new ObjectException(studNo,
                ObjectException.DATA_NOT_FOUND_EXCEPTION,
                "找不到对应学号的学生");
        }
        Student stud = new Student();
        stud.setStudNo(studNo);
        stud.setStudName("小强");
        return stud;
    }

    @ApiOperation(value="根据学号查询学生2",notes="根据学号查询学生。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "studNo", value="学号必须是两位数",
        required=true, paramType = "path", dataType ="String"
    ))
    @GetMapping("/student2/{studNo}")
    @ResponseBody
    public ResponseData findStudent2(@PathVariable String studNo) {
        if(StringUtils.length(studNo)!=2){
            /*throw new ObjectException(studNo,
                ObjectException.DATA_NOT_FOUND_EXCEPTION,
                "找不到对应学号的学生");*/
            return ResponseData.makeErrorMessageWithData(studNo,
                ObjectException.DATA_NOT_FOUND_EXCEPTION,
                "找不到对应学号的学生");
        }
        Student stud = new Student();
        stud.setStudNo(studNo);
        stud.setStudName("小强");
        return ResponseData.makeResponseData(stud);
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
