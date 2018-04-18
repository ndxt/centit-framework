package com.centit.framework.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.*;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.DictionaryMapUtils;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mainframe")
public class MainFrameController extends BaseController {

    public static final String ENTRANCE_TYPE = "ENTRANCE_TYPE";
    public static final String NORMAL_LOGIN = "NORMAL";
    public static final String DEPLOY_LOGIN = "DEPLOY";
    public static final String LOGIN_AUTH_ERROR_MSG = "LOGIN_ERROR_MSG";

    private static String optId = "mainframe";
    @Resource
    protected CsrfTokenRepository csrfTokenRepository;
    
    @Resource
    protected PlatformEnvironment platformEnvironment;
    //实施人员入口开关
    @Value("${deploy.enabled}")
    private boolean deploy;
    //单点登录开关
    @Value("${login.cas.enable}")
    private boolean useCas;
    @Value("${login.cas.localHome}")
    private String localHome ;
    @Value("${login.casHome}")
    private String casHome ;// https://productsvr.centit.com:8443/cas
    @Value("${app.local.firstpage}")
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
    public String login(HttpServletRequest request, HttpSession session) {
        //不允许ajax强求登录页面
        if(WebOptUtils.isAjax(request)){
            return "redirect:/system/exception/error/401";
        }
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
    public String loginAsAdmin(HttpServletRequest request, HttpSession session) {
        //不允许ajax强求登录页面
        if(WebOptUtils.isAjax(request)){
            return "redirect:/system/exception/error/401";
        }
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
    public String loginError(HttpServletRequest request,HttpSession session) {
        //在系统中设定Spring Security 相关的错误信息
        AuthenticationException authException = (AuthenticationException)
                session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        //设置错误信息
        if(authException!=null)
            session.setAttribute(LOGIN_AUTH_ERROR_MSG, authException.getMessage());
        //重新登录
        return login(request,session);
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
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserInfo().getUserCode(), password);
            if(!bo){
                JsonResultUtils.writeErrorMessageJson("用户输入的密码错误，不能修改密码！", response);
            }else{
                platformEnvironment.changeUserPassword(ud.getUserInfo().getUserCode(), newPassword);
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
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserInfo().getUserCode(), password);
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
        Map<String, Object> formValue = BaseController.collectRequestParameters(request);

        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        String userPwd = StringBaseOpt.objectToString(formValue.get("password"));
    
        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        if(ud==null){
            JsonResultUtils.writeErrorMessageJson("用户： "+userCode+"不存在。", response);
            return;
        }
        boolean bo=platformEnvironment.checkUserPassword(ud.getUserInfo().getUserCode(), userPwd);
        if(!bo){
            JsonResultUtils.writeErrorMessageJson("用户 名和密码不匹配。", response);
            return;
        }
        String tokenKey = SecurityContextUtils.registerUserToken(ud);
        // 如果是为了和第三方做模拟的单点登录也可以用这个函数，但是需要把下面这一行代码注释去掉
        // SecurityContextUtils.setSecurityContext(ud,request.getSession());
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
            CsrfToken token = csrfTokenRepository.loadToken(request);
            if(token == null){
                token = csrfTokenRepository.generateToken(request);
                csrfTokenRepository.saveToken( token,  request,
                        response);
            }
            response.setHeader("_csrf_parameter", token.getParameterName());
            response.setHeader("_csrf_header", token.getHeaderName());
            response.setHeader("_csrf", token.getToken());
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
        response.setHeader("Cache-Control", "no-cache");
        JsonResultUtils.writeOriginalImage(
                CaptchaImageUtil.generateCaptchaImage(checkcode), response );
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
        Boolean checkResult = StringUtils.equals(checkcode, sessionCode);
        request.getSession().setAttribute(
                SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT,
                checkResult);
        JsonResultUtils.writeOriginalObject(checkResult, response);
    }

    /**
     * @param request request
     * @param response response
     */
    @RequestMapping("/currentuserinfo")
    public void getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null)
            JsonResultUtils.writeMessageAndData(
                    "No user login on current session!",request.getSession().getId(), response);
        else
            JsonResultUtils.writeSingleDataJson(ud.getUserInfo(), response);
    }
    /**
     * @param request request
     * @param response response
     */
    @RequestMapping("/currentuser")
    public void getCurrentUserDetails(HttpServletRequest request, HttpServletResponse response) {
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
       
//        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userDetails.getUserInfo().getUserCode(),asAdmin );
        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userDetails.getUserInfo().getUserCode(),"CENTIT",asAdmin );

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);
    }

    @RequestMapping(value = "/submenu" , method = RequestMethod.GET)
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
                userDetails.getUserInfo().getUserCode(),optId ,asAdmin);

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);

    }
    
    @RequestMapping(value = "/getMenu/{userCode}" , method = RequestMethod.GET)
    public void getMemuByUsercode(@PathVariable String userCode,
            HttpServletRequest request, HttpServletResponse response) {

        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userCode, false);

        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(menuFunsByUser), response);

    }
    
    @RequestMapping(value = "/expired" , method = RequestMethod.GET)
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

    /**
     * 查询当前用户所有职位
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @GetMapping(value = "/userpositions")
    public void listCurrentUserUnits(
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_SESSION_TIMEOUT,
                    "用户没有登录或者超时，请重新登录。", response);
            return;
        }
        JsonResultUtils.writeSingleDataJson(
                DictionaryMapUtils.objectsToJSONArray(
                    currentUser.getUserInfo().getUserUnits()),
                response);
    }

    /**
     * 查询当前用户当前职位
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @GetMapping(value = "/usercurrposition")
    public void getUserCurrentStaticn(
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_SESSION_TIMEOUT,
                    "用户没有登录或者超时，请重新登录。", response);
            return;
        }
        JsonResultUtils.writeSingleDataJson(
                DictionaryMapUtils.objectToJSON(
                        currentUser.getCurrentStation()),
                response);
    }

    /**
     * 设置当前用户当前职位
     * @param userUnitId 用户机构Id
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @PutMapping(value = "/setuserposition/{userUnitId}")
    public void setUserCurrentStaticn(@PathVariable String userUnitId,
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_SESSION_TIMEOUT,
                    "用户没有登录或者超时，请重新登录。", response);
            return;
        }
        currentUser.setCurrentStationId(userUnitId);
        JsonResultUtils.writeSuccessJson(response);
    }

    @RequestMapping(value = "/checkuserpower/{optId}/{method}", method = { RequestMethod.GET })
    public void checkUserOptPower(@PathVariable String optId,
                                  @PathVariable String method, HttpServletResponse response) {
        boolean s = CodeRepositoryUtil
                .checkUserOptPower(optId,method);
        JsonResultUtils.writeSingleDataJson(s, response);
    }

    /**
     * 查询当前用户在某岗位的所有职位信息
     * @param rank 岗位代码
     */
    @GetMapping(value = "userranks/{rank}")
    @ResponseBody
    public ResponseData listUserUnitsByRank(@PathVariable String rank){
        CentitUserDetails centitUserDetails = WebOptUtils.getLoginUser();
        if(centitUserDetails == null){
            return new ResponseSingleData(ResponseData.ERROR_UNAUTHORIZED, "用户没有登录或者超时，请重新登录");
        }
        return ResponseSingleData.makeResponseData(
                DictionaryMapUtils.objectsToJSONArray(
                CodeRepositoryUtil.listUserUnitsByRank(centitUserDetails.getUserCode(), rank)));
    }
    /**
     * 查询当前用户在某职务的所有职位信息
     * @param station 职务代码
     */
    @GetMapping(value = "userstations/{station}")
    @ResponseBody
    public ResponseData listUserUnitsByStation(@PathVariable String station){
        CentitUserDetails centitUserDetails = WebOptUtils.getLoginUser();
        if(centitUserDetails == null){
            return new ResponseSingleData("用户没有登录或者超时，请重新登录");
        }
        return ResponseSingleData.makeResponseData(
                DictionaryMapUtils.objectsToJSONArray(
                CodeRepositoryUtil.listUserUnitsByStation(centitUserDetails.getUserCode(), station)));
    }

}
