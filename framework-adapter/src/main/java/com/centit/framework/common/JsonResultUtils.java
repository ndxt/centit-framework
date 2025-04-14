package com.centit.framework.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.PropertyPreFilter;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.centit.support.algorithm.ReflectionOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.file.FileType;
import com.centit.support.xml.XMLObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用阿里提供的Json API 格式化Json数据
 *
 * @author sx
 * @author codefan@sina.com 代码全部重写
 * @version fastjson文档地址：https://github.com/alibaba/fastjson/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
 *          fastJson,jackJson,Gson性能比较 http://chenyanxi.blog.51cto.com/4599355/1543445
 */
public class JsonResultUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonResultUtils.class);
    private final static String DEFAULT_RESPONSE_CHARACTER = "UTF-8";

    /*static {
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
    }*/

    private JsonResultUtils() {

    }

    /**
     * 调用response的原始接口输出
     *
     *文件扩展名    Content-Type(Mime-Type)    文件扩展名    Content-Type(Mime-Type)
        .*（ 二进制流，不知道下载文件类型）    application/octet-stream    .tif    image/tiff
        .001    application/x-001        .301    application/x-301
        .323    text/h323                .906    application/x-906
        .907    drawing/907                .a11    application/x-a11
        .acp    audio/x-mei-aac            .ai        application/postscript
        .aif    audio/aiff                .aifc    audio/aiff
        .aiff    audio/aiff                .anv    application/x-anv
        .asa    text/asa                .asf    video/x-ms-asf
        .asp    text/asp                .asx    video/x-ms-asf
        .au        audio/basic                .avi    video/avi
        .awf    application/vnd.adobe.workflow    .biz    text/xml
        .bmp    application/x-bmp        .bot    application/x-bot
        .c4t    application/x-c4t        .c90    application/x-c90
        .cal    application/x-cals        .cat    application/vnd.ms-pki.seccat
        .cdf    application/x-netcdf    .cdr    application/x-cdr
        .cel    application/x-cel        .cer    application/x-x509-ca-cert
        .cg4    application/x-g4        .cgm    application/x-cgm
        .cit    application/x-cit        .class    java/*
        .cml    text/xml                .cmp    application/x-cmp
        .cmx    application/x-cmx        .cot    application/x-cot
        .crl    application/pkix-crl    .crt    application/x-x509-ca-cert
        .csi    application/x-csi        .css    text/css
        .cut    application/x-cut        .dbf    application/x-dbf
        .dbm    application/x-dbm        .dbx    application/x-dbx
        .dcd    text/xml                .dcx    application/x-dcx
        .der    application/x-x509-ca-cert    .dgn    application/x-dgn
        .dib    application/x-dib        .dll    application/x-msdownload
        .doc    application/msword        .dot    application/msword
        .drw    application/x-drw        .dtd    text/xml
        .dwf    Model/vnd.dwf            .dwf    application/x-dwf
        .dwg    application/x-dwg        .dxb    application/x-dxb
        .dxf    application/x-dxf        .edn    application/vnd.adobe.edn
        .emf    application/x-emf        .eml    message/rfc822
        .ent    text/xml                .epi    application/x-epi
        .eps    application/x-ps        .eps    application/postscript
        .etd    application/x-ebx        .exe    application/x-msdownload
        .fax    image/fax                .fdf    application/vnd.fdf
        .fif    application/fractals    .fo        text/xml
        .frm    application/x-frm        .g4    application/x-g4
        .gbr    application/x-gbr        .    application/x-
        .gif    image/gif                .gl2    application/x-gl2
        .gp4    application/x-gp4        .hgl    application/x-hgl
        .hmr    application/x-hmr        .hpg    application/x-hpgl
        .hpl    application/x-hpl        .hqx    application/mac-binhex40
        .hrf    application/x-hrf        .hta    application/hta
        .htc    text/x-component        .htm    text/html
        .html    text/html                .htt    text/webviewhtml
        .htx    text/html                .icb    application/x-icb
        .ico    image/x-icon            .ico    application/x-ico
        .iff    application/x-iff        .ig4    application/x-g4
        .igs    application/x-igs        .iii    application/x-iphone
        .img    application/x-img        .ins    application/x-internet-signup
        .isp    application/x-internet-signup    .IVF    video/x-ivf
        .java    java/*                    .jfif    image/jpeg
        .jpe    image/jpeg                .jpe    application/x-jpe
        .jpeg    image/jpeg                .jpg    image/jpeg
        .jpg    application/x-jpg        .js    application/x-javascript
        .jsp    text/html                .la1    audio/x-liquid-file
        .lar    application/x-laplayer-reg    .latex    application/x-latex
        .lavs    audio/x-liquid-secure    .lbm    application/x-lbm
        .lmsff    audio/x-la-lms            .ls    application/x-javascript
        .ltr    application/x-ltr        .m1v    video/x-mpeg
        .m2v    video/x-mpeg            .m3u    audio/mpegurl
        .m4e    video/mpeg4                .mac    application/x-mac
        .man    application/x-troff-man    .math    text/xml
        .mdb    application/msaccess    .mdb    application/x-mdb
        .mfp    application/x-shockwave-flash    .mht    message/rfc822
        .mhtml    message/rfc822            .mi    application/x-mi
        .mid    audio/mid                .midi    audio/mid
        .mil    application/x-mil        .mml    text/xml
        .mnd    audio/x-musicnet-download    .mns    audio/x-musicnet-stream
        .mocha    application/x-javascript    .movie    video/x-sgi-movie
        .mp1    audio/mp1                .mp2    audio/mp2
        .mp2v    video/mpeg                .mp3    audio/mp3
        .mp4    video/mpeg4                .mpa    video/x-mpg
        .mpd    application/vnd.ms-project    .mpe    video/x-mpeg
        .mpeg    video/mpg                .mpg    video/mpg
        .mpga    audio/rn-mpeg            .mpp    application/vnd.ms-project
        .mps    video/x-mpeg            .mpt    application/vnd.ms-project
        .mpv    video/mpg                .mpv2    video/mpeg
        .mpw    application/vnd.ms-project    .mpx    application/vnd.ms-project
        .mtx    text/xml                .mxp    application/x-mmxp
        .net    image/pnetvue            .nrf    application/x-nrf
        .nws    message/rfc822            .odc    text/x-ms-odc
        .out    application/x-out        .p10    application/pkcs10
        .p12    application/x-pkcs12    .p7b    application/x-pkcs7-certificates
        .p7c    application/pkcs7-mime    .p7m    application/pkcs7-mime
        .p7r    application/x-pkcs7-certreqresp    .p7s    application/pkcs7-signature
        .pc5    application/x-pc5        .pci    application/x-pci
        .pcl    application/x-pcl        .pcx    application/x-pcx
        .pdf    application/pdf            .pdf    application/pdf
        .pdx    application/vnd.adobe.pdx    .pfx    application/x-pkcs12
        .pgl    application/x-pgl        .pic    application/x-pic
        .pko    application/vnd.ms-pki.pko    .pl    application/x-perl
        .plg    text/html                .pls    audio/scpls
        .plt    application/x-plt        .png    image/png
        .png    application/x-png        .pot    application/vnd.ms-powerpoint
        .ppa    application/vnd.ms-powerpoint    .ppm    application/x-ppm
        .pps    application/vnd.ms-powerpoint    .ppt    application/vnd.ms-powerpoint
        .ppt    application/x-ppt        .pr    application/x-pr
        .prf    application/pics-rules    .prn    application/x-prn
        .prt    application/x-prt        .ps    application/x-ps
        .ps    application/postscript        .ptn    application/x-ptn
        .pwz    application/vnd.ms-powerpoint    .r3t    text/vnd.rn-realtext3d
        .ra    audio/vnd.rn-realaudio        .ram    audio/x-pn-realaudio
        .ras    application/x-ras        .rat    application/rat-file
        .rdf    text/xml                .rec    application/vnd.rn-recording
        .red    application/x-red        .rgb    application/x-rgb
        .rjs    application/vnd.rn-realsystem-rjs    .rjt    application/vnd.rn-realsystem-rjt
        .rlc    application/x-rlc        .rle    application/x-rle
        .rm    application/vnd.rn-realmedia    .rmf    application/vnd.adobe.rmf
        .rmi    audio/mid                .rmj    application/vnd.rn-realsystem-rmj
        .rmm    audio/x-pn-realaudio    .rmp    application/vnd.rn-rn_music_package
        .rms    application/vnd.rn-realmedia-secure    .rmvb    application/vnd.rn-realmedia-vbr
        .rmx    application/vnd.rn-realsystem-rmx    .rnx    application/vnd.rn-realplayer
        .rp    image/vnd.rn-realpix        .rpm    audio/x-pn-realaudio-plugin
        .rsml    application/vnd.rn-rsml    .rt    text/vnd.rn-realtext
        .rtf    application/msword        .rtf    application/x-rtf
        .rv    video/vnd.rn-realvideo        .sam    application/x-sam
        .sat    application/x-sat        .sdp    application/sdp
        .sdw    application/x-sdw        .sit    application/x-stuffit
        .slb    application/x-slb        .sld    application/x-sld
        .slk    drawing/x-slk            .smi    application/smil
        .smil    application/smil        .smk    application/x-smk
        .snd    audio/basic                .sol    text/plain
        .sor    text/plain                .spc    application/x-pkcs7-certificates
        .spl    application/futuresplash    .spp    text/xml
        .ssm    application/streamingmedia    .sst    application/vnd.ms-pki.certstore
        .stl    application/vnd.ms-pki.stl    .stm    text/html
        .sty    application/x-sty        .svg    text/xml
        .swf    application/x-shockwave-flash    .tdf    application/x-tdf
        .tg4    application/x-tg4        .tga    application/x-tga
        .tif    image/tiff                .tif    application/x-tif
        .tiff    image/tiff                .tld    text/xml
        .top    drawing/x-top            .torrent    application/x-bittorrent
        .tsd    text/xml                .txt    text/plain
        .uin    application/x-icq        .uls    text/iuls
        .vcf    text/x-vcard            .vda    application/x-vda
        .vdx    application/vnd.visio    .vml    text/xml
        .vpg    application/x-vpeg005    .vsd    application/vnd.visio
        .vsd    application/x-vsd        .vss    application/vnd.visio
        .vst    application/vnd.visio    .vst    application/x-vst
        .vsw    application/vnd.visio    .vsx    application/vnd.visio
        .vtx    application/vnd.visio    .vxml    text/xml
        .wav    audio/wav                .wax    audio/x-ms-wax
        .wb1    application/x-wb1        .wb2    application/x-wb2
        .wb3    application/x-wb3        .wbmp    image/vnd.wap.wbmp
        .wiz    application/msword        .wk3    application/x-wk3
        .wk4    application/x-wk4        .wkq    application/x-wkq
        .wks    application/x-wks        .wm    video/x-ms-wm
        .wma    audio/x-ms-wma            .wmd    application/x-ms-wmd
        .wmf    application/x-wmf        .wml    text/vnd.wap.wml
        .wmv    video/x-ms-wmv            .wmx    video/x-ms-wmx
        .wmz    application/x-ms-wmz    .wp6    application/x-wp6
        .wpd    application/x-wpd        .wpg    application/x-wpg
        .wpl    application/vnd.ms-wpl    .wq1    application/x-wq1
        .wr1    application/x-wr1        .wri    application/x-wri
        .wrk    application/x-wrk        .ws    application/x-ws
        .ws2    application/x-ws        .wsc    text/scriptlet
        .wsdl    text/xml                .wvx    video/x-ms-wvx
        .xdp    application/vnd.adobe.xdp    .xdr    text/xml
        .xfd    application/vnd.adobe.xfd    .xfdf    application/vnd.adobe.xfdf
        .xhtml    text/html                .xls    application/vnd.ms-excel
        .xls    application/x-xls        .xlw    application/x-xlw
        .xml    text/xml                .xpl    audio/scpls
        .xq    text/xml                    .xql    text/xml
        .xquery    text/xml                .xsd    text/xml
        .xsl    text/xml                .xslt    text/xml
        .xwd    application/x-xwd        .x_b    application/x-x_b
        .sis    application/vnd.symbian.install    .sisx    application/vnd.symbian.install
        .x_t    application/x-x_t        .ipa    application/vnd.iphone
        .apk    application/vnd.android.package-archive    .xap    application/x-silverlight-app
     *  @param <T> 类型通配符
     *  @param contentType String 类型类别
     *  @param objValue T
     *  @param response HttpServletResponse
     */
    public static <T> void writeOriginalResponse(String contentType, T objValue,
            HttpServletResponse response) {
        //response.setHeader("Cache-Control","no-store");
        //response.setHeader("Pragma","no-cache");
        response.setCharacterEncoding(DEFAULT_RESPONSE_CHARACTER);
        response.setContentType(contentType);//"application/json; charset=utf-8");
        try {
            String dataString = StringBaseOpt.castObjectToString(objValue);
            byte[] bytes = dataString.getBytes(); // ByteBaseOpt.castObjectToBytes(objValue);//
            ServletOutputStream outputStream = response.getOutputStream(); // 获取输出流
            outputStream.write(bytes); // 写入字节数据
            outputStream.flush();
            /*if(objValue instanceof String) {
                response.getWriter().write((String) objValue);
            } else {
                response.getWriter().print(objValue);
            }*/
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            //throw new ObjectException(objValue, ResponseData.HTTP_IO_EXCEPTION, e.getMessage());
        }
    }

    /**
     * 格式化Json数据输出
     *@param json JSON格式
     * @param response HttpServletResponse
     */
    public static void writeOriginalJson(Object json, HttpServletResponse response) {
        String objectString;
        if(json instanceof JSONObject || json instanceof JSONArray){
            objectString = json.toString();
        } else {
            objectString = JSON.toJSONString(json);
        }
        writeOriginalResponse("application/json; charset=utf-8",
            objectString , response);
    }

    /**
     * 格式化Json数据输出
     *@param jsonValue String类型
     * @param response HttpServletResponse
     */
    public static void writeOriginalJson(String jsonValue, HttpServletResponse response) {
        writeOriginalResponse("application/json; charset=utf-8",
                jsonValue,response);
    }



    /**
     * 直接文本数据输出
     * @param <T> 类型通配符
     * @param objValue T
     * @param response HttpServletResponse
     */
    public static <T> void writeOriginalHtml(T objValue, HttpServletResponse response) {
        writeOriginalResponse("text/html; charset=utf-8",
                objValue,response);
    }

    /**
     * 格式化XML数据输出
     *@param xml XML格式
     * @param response HttpServletResponse
     */
    public static void writeOriginalXml(String xml, HttpServletResponse response) {
        writeOriginalResponse("text/xml; charset=utf-8",
                xml,response);
    }


    /**
     * 直接文本数据输出
     * @param image 图片 RenderedImage
     * @param response HttpServletResponse
     */
    public static void writeOriginalImage(RenderedImage image, HttpServletResponse response) {
        response.setCharacterEncoding(DEFAULT_RESPONSE_CHARACTER);
        response.setContentType("image/gif");
        //response.setContentType("application/json; charset=utf-8");
        try(ServletOutputStream os = response.getOutputStream()) {
            ImageIO.write(image, "gif", os);
            os.flush();
        }catch(IOException e){
            logger.error(e.getLocalizedMessage(),e);
        }
    }

    /**
     * 直接文本数据输出
     * @param is 文件 InputStream
     * @param fileName 文件名称
     * @param response HttpServletResponse
     */
    public static void writeOriginalFile(InputStream is, String fileName, HttpServletResponse response) {
        response.setCharacterEncoding(DEFAULT_RESPONSE_CHARACTER);
        response.setContentType(FileType.getFileMimeType(fileName));
        response.setHeader("Content-Disposition",
                "attachment; filename=" +
                        StringEscapeUtils.escapeHtml4(fileName));
        try(ServletOutputStream out = response.getOutputStream();
            BufferedOutputStream bufferOut = new BufferedOutputStream(out)){

            byte[] buffer = new byte[64 * 1024];
            int length;
            while((length = is.read(buffer, 0, buffer.length)) != -1) {
                bufferOut.write(buffer, 0, length);
                bufferOut.flush();
            }
        } catch (IOException e){
            logger.error("客户端断开链接："+e.getLocalizedMessage(), e);
        }
    }

    /**
     * 直接文本数据输出
     * @param file 文件 InputStream
     * @param response HttpServletResponse
     */
    public static void writeOriginalFile(File file, HttpServletResponse response) {
        try(InputStream inputStream = new FileInputStream(file)){
            writeOriginalFile(inputStream,file.getName(),response);
        } catch (IOException e){
            logger.error("文件打开失败："+e.getLocalizedMessage(), e);
        }
    }

    /**
     * 直接文本数据输出
     * @param objValue T
     * @param response HttpServletResponse
     */
    public static void writeOriginalObject(Object objValue, HttpServletResponse response) {
        if(ReflectionOpt.isScalarType(objValue.getClass())){
            writeOriginalResponse("text/plain; charset=utf-8",
                    JSON.toJSONString(objValue), response);
        }else {
            String objectString;
            if(objValue instanceof JSONObject || objValue instanceof JSONArray){
                objectString = objValue.toString();
            } else {
                objectString = JSON.toJSONString(objValue);
            }
            writeOriginalResponse("application/json; charset=utf-8",
                    objectString, response);
        }
    }


    /**
     * javascript脚本输出
     * @param scriptValue javascript脚本 String类型
     * @param response HttpServletResponse
     */
    public static void writeJavaScript(String scriptValue, HttpServletResponse response) {
           writeOriginalResponse("application/javascript; charset=utf-8",
                   scriptValue,response);
     }

    /**
     * 格式化Json数据输出
     * @param code 返回码
     * @param message 返回提示信息
     * @param objValue 返回数据对象
     * @param response HttpServletResponse
     * @param simplePropertyPreFilter SerializeFilter {@link SimplePropertyPreFilter} 格式化时过滤指定的属性
     */
    public static void writeSingleDataJson(int code, String message, Object objValue, HttpServletResponse response,
                                           PropertyPreFilter simplePropertyPreFilter) {

        Map<String, Object> param = new HashMap<>();
        param.put(ResponseData.RES_CODE_FILED, code );
        param.put(ResponseData.RES_MSG_FILED,  message );
        if(objValue!=null) {
            param.put(ResponseData.RES_DATA_FILED, objValue);
        }
        String text = JSON.toJSONString(param, simplePropertyPreFilter);

        writeOriginalJson(text,response);
    }


    /**
     * 格式化XML数据输出
     * @param code 返回码
     * @param message 返回提示信息
     * @param objValue 返回数据对象
     * @param response HttpServletResponse
     */
    public static void writeSingleDataXml(int code,String message, Object objValue, HttpServletResponse response) {

        Map<String, Object> param = new HashMap<>();
        param.put(ResponseData.RES_CODE_FILED, code );
        param.put(ResponseData.RES_MSG_FILED,  message );
        if(objValue!=null) {
            param.put(ResponseData.RES_DATA_FILED, objValue);
        }
        String text = XMLObject.objectToXMLString("response", param);

        writeOriginalXml(text,response);
    }

    /**
     * 格式化Json数据输出
     * @param resData ResponseData
     * @param response HttpServletResponse
     * @param propertyPreFilter {@link SimplePropertyPreFilter} 格式化时过滤指定的属性
     */
    public static void writeResponseDataAsJson(ResponseData resData, HttpServletResponse response,
                                               PropertyPreFilter propertyPreFilter) {
        writeSingleDataJson(resData.getCode(),resData.getMessage(),
                resData.getData(), response, propertyPreFilter);
    }

    /**
     * 格式化Json数据输出
     * @param resData ResponseData http响应信息
     * @param response HttpServletResponse
     */
    public static void writeResponseDataAsJson(ResponseData resData, HttpServletResponse response) {
        writeSingleDataJson(resData.getCode(), resData.getMessage(),
                resData.getData(), response, null);
    }

    /**
     * 格式化XML数据输出
     * @param resData ResponseData http响应信息
     * @param response HttpServletResponse
     */
    public static void writeResponseDataAsXml(ResponseData resData, HttpServletResponse response) {
        writeSingleDataXml(resData.getCode(),resData.getMessage(),
                resData.getData(), response);
    }


    /**
     * Ajax 请求失败，http的状态码设置为 code
     * @param errorCode  错误返回码
     * @param errorMessage 错误返回信息
     * @param response HttpServletResponse
     */
    public static void writeHttpErrorMessage(int errorCode, String errorMessage, HttpServletResponse response) {
        try {
            response.sendError(errorCode, errorMessage);
        } catch (IOException e) {
            //writeSingleDataJson(errorCode,errorMessage,
                //null, response, null);
            logger.error(e.getMessage(),e);
        }


    }
    /**
     * 格式化Json数据输出 , 输出 业务提示的错误信息，http的状态仍然是 200 OK
     * @param errorCode  错误返回码
     * @param errorMessage 错误返回信息
     * @param response HttpServletResponse
     */
    public static void writeErrorMessageJson(int errorCode, String errorMessage, HttpServletResponse response) {
        writeSingleDataJson(errorCode,errorMessage,
                null, response, null);
    }

    /**
     * 格式化Json数据输出
     * @param message String
     * @param response HttpServletResponse
     */
    public static void writeMessageJson(String message, HttpServletResponse response) {
        writeSingleDataJson(0,message,
                null, response, null);
    }

    /**
     * 格式化Json数据输出
     * @param message String
     * @param objValue Object
     * @param response HttpServletResponse
     */
    public static void writeMessageAndData(String message, Object objValue,
            HttpServletResponse response) {
        writeSingleDataJson(0,message,
                objValue, response, null);
    }
    /**
     * 格式化Json数据输出
     * @param errorMessage 错误信息
     * @param response HttpServletResponse
     */
    public static void writeErrorMessageJson(String errorMessage, HttpServletResponse response) {
        writeSingleDataJson(500,errorMessage,
                null, response, null);
    }

    /**
     * 格式化Json数据输出
     *
     * @param response HttpServletResponse
     */
    public static void writeBlankJson(HttpServletResponse response) {
        writeSingleDataJson(0,"OK",
                null, response, null);
    }


    /**
     *
     * @param objValue Object
     * @param response HttpServletResponse
     * @param simplePropertyPreFilter {@link SimplePropertyPreFilter} 格式化时过滤指定的属性
     */
    public static void writeSingleDataJson(Object objValue, HttpServletResponse response,
                                           PropertyPreFilter simplePropertyPreFilter) {
        writeSingleDataJson(0,"OK", objValue, response, simplePropertyPreFilter);
    }
    /**
     * 格式化Json数据输出
     * @param objValue Object
     * @param response HttpServletResponse
     */
    public static void writeSingleDataJson(Object objValue, HttpServletResponse response) {
        writeSingleDataJson(0,"OK",objValue, response, null);
    }


    /**
     * 格式化XML数据输出
     * @param objValue Object
     * @param response HttpServletResponse
     */
    public static void writeSingleDataXml(Object objValue, HttpServletResponse response) {
        writeSingleDataXml(0,"OK",objValue, response);
    }

    /**
     * 格式化Json数据输出
     * @param errorCode  错误返回码
     * @param errorMessage 错误返回信息
     * @param objValue Object
     * @param response HttpServletResponse
     */
    public static void writeSingleErrorDataJson(int errorCode, String errorMessage,  Object objValue,
                                                HttpServletResponse response) {
        writeSingleDataJson(errorCode,errorMessage,objValue, response,null);
    }

    /**
     * 格式化Json数据输出
     *
     * @param response HttpServletResponse
     */
    public static void writeSuccessJson(HttpServletResponse response) {
        writeBlankJson(response);
    }

}
