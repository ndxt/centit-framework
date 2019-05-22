package com.otherpackage.controller;

import com.centit.framework.common.ObjectException;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.operationlog.RecordOperationLog;
import com.centit.support.algorithm.ByteBaseOpt;
import com.centit.support.common.ParamName;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.image.CaptchaImageUtil;
import com.otherpackage.po.Student;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@Api(value="测试controller",
    tags="测试Http的新特性和ResponseData的结构")
@RequestMapping("/test")
public class TestCaseController extends BaseController {

    public String getOptId(){
        return "testCtrl";
    }

    @ApiOperation(value="hello",notes="Say hello to somebody.")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "userName", value="用户姓名，向某人说hello",
        required=true, paramType = "path", dataType= "String"
    ))
    @GetMapping("/sayhello/{userName}")
    @RecordOperationLog(content = "Say hello to {userName}.", returnValueAsOld = true)
    @WrapUpResponseBody
    public String sayHello(@PathVariable @ParamName("userName") String userName){
        return "hello "+userName+" !";
    }

    @ApiOperation(value="根据学号查询学生",notes="根据学号查询学生。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "studNo", value="学号必须是两位数",
        required=true, paramType = "path", dataType ="String"
    ))
    @GetMapping("/student/{studNo}")
    @WrapUpResponseBody
    @RecordOperationLog(content = "查询学号为{studNo}学生", returnValueAsOld = true)
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

    @ApiOperation(value="根据学号查询学生(Map)",notes="根据学号查询学生。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "studNo", value="学号必须是两位数",
        required=true, paramType = "path", dataType ="String"
    ))
    @GetMapping("/student3/{studNo}")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @RecordOperationLog(content = "查询学号为{studNo}学生", returnValueAsOld = true)
    public Student findStudent3(@PathVariable @ParamName("studNo") String studNo) {
        Student stud = new Student();
        stud.setMan(true);
        stud.setStudNo(studNo);
        stud.setStudName("小强");
        return stud;
    }

    @ApiOperation(value="返回所有学生(Map)",notes="返回所有学生(Map)")
    @GetMapping("/student")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    @RecordOperationLog(content = "返回所有学生", returnValueAsOld = true)
    public List<Student> listStudent() {
        List<Student> listStud = new ArrayList<>(4);
        Student stud = new Student();
        stud.setMan(true);
        stud.setStudNo("1");
        stud.setStudName("小强");
        listStud.add(stud);
        stud = new Student();
        stud.setMan(false);
        stud.setStudNo("2");
        stud.setStudName("小红");
        listStud.add(stud);
        return listStud;
    }

    @ApiOperation(value="返回所有学生(pageQuery)",notes="返回所有学生(pageQuery)")
    @GetMapping("/pagestudent")
    @WrapUpResponseBody
    @RecordOperationLog(content = "返回所有学生", returnValueAsOld = true)
    public PageQueryResultSample<Student> listStudentPage() {
        List<Student> listStud = new ArrayList<>(4);
        Student stud = new Student();
        stud.setMan(true);
        stud.setStudNo("1");
        stud.setStudName("小强");
        listStud.add(stud);
        stud = new Student();
        stud.setMan(false);
        stud.setStudNo("2");
        stud.setStudName("小红");
        listStud.add(stud);
        //JSONArray ja =  DictionaryMapUtils.objectsToJSONArray(listStud);
        return PageQueryResultSample.createResultMapDict(
            listStud,
            new PageDesc(1,10,2)
        );
    }

    @ApiOperation(value="测试获取js",notes="测试获取js。")
    @GetMapping("/js")
    @WrapUpResponseBody(contentType = WrapUpContentType.JAVASCRIPT)
    public String getJs() {
       return "define(function(require) {\n" +
           "    var Page = require('core/page');\n" +
           "\n" +
           "    // 删除流程定义阶段删除\n" +
           "    var FlowDefineRoleRemove = Page.extend(function() {\n" +
           "\n" +
           "        // @override\n" +
           "        this.submit = function(table, row) {\n" +
           "            var index = table.datagrid('getRowIndex', row);\n" +
           "            table.datagrid('deleteRow', index);\n" +
           "        };\n" +
           "    });\n" +
           "\n" +
           "    return FlowDefineRoleRemove;\n" +
           "});";
    }

    @ApiOperation(value="测试获取xml",notes="测试获取xml。")
    @GetMapping("/xml")
    @WrapUpResponseBody(contentType = WrapUpContentType.XML)
    public Student getXML() {
        Student stud = new Student();
        stud.setStudNo("35");
        stud.setStudName("小强2");
        return stud;
    }

    @ApiOperation(value="测试获取图像",notes="测试获取图像。")
    @GetMapping("/img")
    @WrapUpResponseBody(contentType = WrapUpContentType.IMAGE)
    public RenderedImage getImage() {
        String checkcode = CaptchaImageUtil.getRandomString();
        RenderedImage image = CaptchaImageUtil.generateCaptchaImage(checkcode);
        return image;
    }

    @ApiOperation(value="测试下载文件",notes="测试下载文件。")
    @GetMapping("/file")
    @WrapUpResponseBody(contentType = WrapUpContentType.FILE)
    public File getFile(HttpServletRequest request) {
        String url = request.getSession().getServletContext().getRealPath("/");
        File file = new File(url + "WEB-INF/classes/static_system_config.json");
        return file;
    }

    @ApiOperation(value="测试 bool型",notes="测试 bool型。")
    @GetMapping("/bool")
    @WrapUpResponseBody(contentType = WrapUpContentType.RAW)
    public boolean getBoolean() {
        return true;
    }

    @ApiOperation(value="测试整型",notes="测试 整型。")
    @GetMapping("/int")
    @WrapUpResponseBody(contentType = WrapUpContentType.RAW)
    public int getInt() {
        return 100;
    }

    @ApiOperation(value="测试Void",notes="测试 void。")
    @GetMapping("/void")
    @WrapUpResponseBody
    public void testVoid() {

    }

    @ApiOperation(value="测试url query param",notes="测试url query param。")
    @GetMapping("/urlparam")
    @WrapUpResponseBody
    public Map<String, Object> testUrlParam(HttpServletRequest request) {
        return collectRequestParameters(request);
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

    /**
     * 测试 H5 新特性 server-sent-event
     * @param response HttpServletResponse
     */
    @ApiOperation(value="测试H5 SSE",notes="测试H5 SSE。")
    @GetMapping(value = "/sse")
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

    @ApiOperation(value="测试H5 SSE 2",notes="测试H5 SSE 2。")
    //produces = "text/event-stream"
    @GetMapping(value="/sse2",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
