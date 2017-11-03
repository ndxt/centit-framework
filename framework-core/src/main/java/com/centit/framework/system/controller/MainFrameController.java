package com.centit.framework.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.operationlog.RecordOperationLog;
import com.centit.framework.common.*;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mainframe")
public class MainFrameController extends BaseController {

    public static final String ENTRANCE_TYPE = "ENTRANCE_TYPE";
    public static final String NORMAL_LOGIN = "NORMAL";
    public static final String DEPLOY_LOGIN = "DEPLOY";
    public static final String LOGIN_AUTH_ERROR_MSG = "LOGIN_ERROR_MSG";
    
    @Resource
    protected CsrfTokenRepository csrfTokenRepository;
    
    @Resource
    protected PlatformEnvironment platformEnvironment;
    //实施人员入口开关
    @Value("${deploy.enabled}")
    private boolean deploy;
    //单点登录开关
    @Value("${cas.sso}")
    private boolean useCas;
    @Value("${local.home}")
    private String localHome ;
    @Value("${cas.home}")
    private String casHome ;// https://productsvr.centit.com:8443/cas
    @Value("${local.firstpage}")
    private String firstpage ;
   
    /**
     * 登录首页链接，具体登录完成后跳转路径由spring-security-dao.xml中配置
     * @param request request
     * @param session session
     * @return 登录首页链接
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpSession session) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        return "sys/index";//"redirect:"+ firstpage;//
    }

    /**
     *
     * @param request request
     * @param session session
     * @return 登录后首页URL
     */
    @RequestMapping(value = "/logincas")
    public String logincas(HttpServletRequest request, HttpSession session) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        return "redirect:"+firstpage;//"sys/mainframe/index";
    }

    /**
     * 登录界面入口
     * @param session session
     * @return 登录界面
     */
    @RequestMapping("/login")
    public String login(HttpSession session) {
        //输入实施人员链接后未登录，后直接输入 普通用户登录链接
        session.setAttribute(ENTRANCE_TYPE,NORMAL_LOGIN);
        if(useCas)
         {
             return "redirect:/system/mainframe/logincas";
         }else{
             return "sys/login";
         }
    }

    /**
     *
     * @param session session
     * @return 登录界面
     */
    @RequestMapping("/loginasadmin")
    public String loginAsAdmin(HttpSession session) {
        if (deploy) {
            //实施人员入口标记
            session.setAttribute(ENTRANCE_TYPE, DEPLOY_LOGIN);
        }
        if (useCas)
            return "redirect:/system/mainframe/logincas";
        else    
            return "sys/login";
    }

    /**
     *
     * @param session session
     * @return 登录界面
     */
    @RequestMapping("/login/error")
    public String loginError(HttpSession session) {
        //在系统中设定Spring Security 相关的错误信息
        AuthenticationException authException = (AuthenticationException)
                session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        //设置错误信息
        if(authException!=null)
            session.setAttribute(LOGIN_AUTH_ERROR_MSG, authException.getMessage());
        //重新登录
        return login(session);
    }

    /**
     *
     * @param session session
     * @return 登出页面
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute(ENTRANCE_TYPE,NORMAL_LOGIN);
        session.removeAttribute(LOGIN_AUTH_ERROR_MSG);
        if(useCas)
        {
            //return "sys/mainframe/index";
            session.invalidate();
            return "redirect:"+casHome+"/logout?service="+localHome+"/system/mainframe/index";
        }
        else
            return "redirect:/logout";//j_spring_security_logout
    }

    /**
     *
     * @param password password
     * @param newPassword newPassword
     * @param request request
     * @param response response
     */
    @RequestMapping(value ="/changepwd",method = RequestMethod.PUT)
    public void changepassword(String password, String newPassword,
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null){
            JsonResultUtils.writeErrorMessageJson("用户没有登录，不能修改密码！", response);
        }else{
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), password);
            if(!bo){
                JsonResultUtils.writeErrorMessageJson("用户输入的密码错误，不能修改密码！", response);
            }else{
                platformEnvironment.changeUserPassword(ud.getUserCode(), newPassword);
                JsonResultUtils.writeSuccessJson(response);
            }
        }
    }

    /**
     *
     * @param password password
     * @param request request
     * @param response response
     */
    @RequestMapping(value ="/checkpwd",method = RequestMethod.POST)
    public void checkpassword(String password, 
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null){
            JsonResultUtils.writeErrorMessageJson("用户没有登录，不能修改密码！", response);
        }else{
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), password);
            JsonResultUtils.writeOriginalObject(bo, response);
        }
    }
    
    /**
     * 这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成
     * @param request request
     * @param response response
     */
    @RequestMapping(value="/loginasclient",method = RequestMethod.POST)
    public void loginAsClient(HttpServletRequest request,HttpServletResponse response) {
        Map<String, Object> formValue = collectRequestParameters(request);

        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        String userPwd = StringBaseOpt.objectToString(formValue.get("password"));
    
        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        if(ud==null){
            JsonResultUtils.writeErrorMessageJson("用户： "+userCode+"不存在。", response);
            return;
        }
        boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), userPwd);
        if(!bo){
            JsonResultUtils.writeErrorMessageJson("用户 名和密码不匹配。", response);
            return;
        }
        String tokenKey = SecurityContextUtils.registerUserToken(ud);
        //request.getSession().setAttribute(SecurityContextUtils.SecurityContextTokenName, tokenKey);
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(SecurityContextUtils.SecurityContextTokenName, tokenKey);
        JsonResultUtils.writeResponseDataAsJson(resData, response);

    }

    /**
     *
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/login/csrf",method = RequestMethod.GET)
    public void getLoginCsrfToken(HttpServletRequest request,HttpServletResponse response) {
        if(csrfTokenRepository!=null){
            CsrfToken token = csrfTokenRepository.generateToken(request);

            response.setHeader("_csrf_parameter", token.getParameterName());
            response.setHeader("_csrf_header", token.getHeaderName());
            response.setHeader("_csrf", token.getToken());

            csrfTokenRepository.saveToken( token,  request,
                     response);

            JsonResultUtils.writeSingleDataJson
                (token, response);
        }else{
            JsonResultUtils.writeErrorMessageJson(
                    "Bean csrfTokenRepository not found!", response);
        }
    }

    /**
     *
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/csrf",method = RequestMethod.GET)
    public void getCsrfToken(HttpServletRequest request,HttpServletResponse response) {
        getLoginCsrfToken(request, response);
    }

    /**
     *
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/captchaimage",method = RequestMethod.GET)
    public void captchaImage( HttpServletRequest request, HttpServletResponse response) {
  
        String checkcode = CaptchaImageUtil.getRandomString();
        request.getSession().setAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE, checkcode);
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("image/gif");
        //response.setContentType("application/json; charset=utf-8");
        try(ServletOutputStream os = response.getOutputStream()) {
            BufferedImage img = CaptchaImageUtil
                    .generateCaptchaImage(checkcode);
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "gif", os);
            os.flush();
            os.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/login/captchaimage",method = RequestMethod.GET)
    public void loginCaptchaImage( HttpServletRequest request, HttpServletResponse response) {  
        captchaImage(  request,  response);
    }

    /**
     *
     * @param checkcode checkcode
     * @param request request
     * @param response response
     */
    @RequestMapping(value = "/checkcaptcha/{checkcode}",method = RequestMethod.GET)
    public void checkCaptchaImage(@PathVariable String checkcode, HttpServletRequest request, HttpServletResponse response) {
  
        String sessionCode = StringBaseOpt.objectToString(
                    request.getSession().getAttribute(
                            CaptchaImageUtil.SESSIONCHECKCODE));
        
        JsonResultUtils.writeOriginalObject(StringUtils.equals(checkcode, sessionCode), response);
    }

    /**
     * @param request request
     * @param response response
     */
    @RequestMapping("/currentuser")
    public void getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null)
            JsonResultUtils.writeMessageAndData(
                    "No user login on current session!",request.getSession().getId(), response);
        else
            JsonResultUtils.writeSingleDataJson(ud, response);
    }

    /**
     * @param request request
     * @param response response
     */
    @RequestMapping("/hasLogin")
    public void hasLogin(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null)
            JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_UNAUTHORIZED, "用户没有登录，请登录！", response);
        else
            JsonResultUtils.writeSingleDataJson(ud, response);
    }

    /*private JSONArray makeMenuFuncsJson(List<? extends IOptInfo> menuFunsByUser){
        if(menuFunsByUser == null)
            return null;
        JSONArray jsonArray = new JSONArray(menuFunsByUser.size());
        for(IOptInfo optInfo :  menuFunsByUser){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",optInfo.getOptId());
            jsonObject.put("pid",optInfo.getPreOptId());
            jsonObject.put("text",optInfo.getOptName());
            jsonObject.put("url",optInfo.getOptRoute());
            jsonObject.put("icon",optInfo.getIcon());
            Map<String, Object> map = new HashMap<>(2);
            map.put("external", !("D".equals(optInfo.getPageType())));
            jsonObject.put("attributes", map);
            jsonObject.put("isInToolbar",optInfo.getIsInToolbar());
            jsonObject.put("children",makeMenuFuncsJson(optInfo.getChildren()));

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }*/
    private JSONArray makeMenuFuncsJson(List<? extends IOptInfo> menuFunsByUser){
        return ViewDataTransform.makeTreeViewJson(menuFunsByUser,
                ViewDataTransform.createStringHashMap("id","optId",
                        "pid","preOptId",
                        "text","optName",
                        "url","optRoute",
                        "icon","icon",
                        "children","children",
                        "isInToolbar","isInToolbar"
                        //"attributes.external","pageType"
                    ), (jsonObject,obj) -> jsonObject.put("external", !("D".equals(obj.getPageType()))));
    }
    /**
     * 首页菜单
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/menu" , method = RequestMethod.GET)
    public void getMenu(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails userDetails = super.getLoginUser(request);
        if(userDetails==null){
            JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_USER_NOT_LOGIN, "用户没有登录，请登录！", response);
            return;
        }
        Object obj = request.getSession().getAttribute(ENTRANCE_TYPE);  
        boolean asAdmin = obj!=null && DEPLOY_LOGIN.equals(obj.toString());
       
        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userDetails.getUserCode(),asAdmin );

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);
    }

    @RequestMapping(value = "/submenu" , method = RequestMethod.GET)
    //@RecordOperationLog(content="请求{optId}{userInfo.userCode}菜单")
    public void getMenuUnderOptId(@RequestParam(value="optid", required=false)  String optId,
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails userDetails = super.getLoginUser(request);
        if(userDetails==null){
            JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_USER_NOT_LOGIN,
                    "用户没有登录，请登录！", response);
            return;
        }
        Object obj = request.getSession().getAttribute(ENTRANCE_TYPE);  
        boolean asAdmin = obj!=null && DEPLOY_LOGIN.equals(obj.toString());
       
        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(
                userDetails.getUserCode(),optId ,asAdmin);

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);

    }
    
    @RequestMapping(value = "/getMenu/{userCode}" , method = RequestMethod.GET)
    public void getMemuByUsercode(@PathVariable String userCode,
            HttpServletRequest request, HttpServletResponse response) {

        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userCode, false);

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);

    }
    
    @RequestMapping("/expired")
    public String sessionExpired(
            HttpServletRequest request,HttpServletResponse response) {

        if (WebOptUtils.isAjax(request)) {
            JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_SESSION_TIMEOUT,
                    "session超时，请重新登录。", response);
            return null;
        }else{
            return "exception/timeout";
        }
    }
}
