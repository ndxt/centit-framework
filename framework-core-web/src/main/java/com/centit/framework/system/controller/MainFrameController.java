package com.centit.framework.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.*;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUnitFilterEngine;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.impl.UserUnitMapTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IOptInfo;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.ThirdPartyCheckUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoaderListener;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.util.*;

@Api(value="框架中用户权限相关的接口，用户登录接口，第三方认证接口，安全接口",
    tags= "登录、权限、安全控制等接口")
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

    /**
     * 这一用户自定义验证，可以为null
     */
    private ThirdPartyCheckUserDetails thirdPartyCheckUserDetails;

    //实施人员入口开关
    @Value("${app.deploy.enabled:false}")
    private boolean deploy;
    //单点登录开关
    @Value("${login.cas.enable:false}")
    private boolean useCas;
    @Value("${login.cas.localHome:}")
    private String localHome ;
    @Value("${login.cas.casHome:}")
    private String casHome ;// https://productsvr.centit.com:8443/cas
    @Value("${app.local.firstpage:}")
    private String firstpage ;
    @Value("${app.menu.topoptid:}")
    private String topOptId ;
    /**
     * 登录首页链接，具体登录完成后跳转路径由spring-security-dao.xml中配置
     * @param request request
     * @param session session
     * @return 登录首页链接
     */
    @ApiOperation(value = "登录首页链接", notes = "登录首页链接，具体登录完成后跳转路径由spring-security-dao.xml中配置")
    @GetMapping(value = "/index")
    public String index(HttpServletRequest request, HttpSession session) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        return "sys/index";//"redirect:"+ firstpage;//
    }

    /**
     * 跳往cas登录链接
     * @param request request
     * @param session session
     * @return 登录后首页URL
     */
    @ApiOperation(value = "跳往cas登录链接", notes = "使用cas登录系统")
    @GetMapping(value = "/logincas")
    public String logincas(HttpServletRequest request, HttpSession session) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        return "redirect:"+firstpage;//"sys/mainframe/index";
    }

    /**
     * 登录界面入口
     * @param session session
     * @param request HttpServletRequest
     * @return 登录界面
     */
    @GetMapping("/login")
    @ApiOperation(value = "登录界面入口", notes = "登录界面入口")
    public String login(HttpServletRequest request, HttpSession session) {
        //不允许ajax强求登录页面
        if(WebOptUtils.isAjax(request)){
            return "redirect:/system/exception/error/401";
        }
        //输入实施人员链接后未登录，后直接输入 普通用户登录链接
        session.setAttribute(ENTRANCE_TYPE,NORMAL_LOGIN);
        return useCas?"redirect:/system/mainframe/logincas":"sys/login";
    }

    /**
     * 以管理员登录界面
     * @param session session
     * @param request HttpServletRequest
     * @return 登录界面
     */
    @ApiOperation(value = "以管理员登录界面", notes = "以管理员身份登录界面")
    @GetMapping("/loginasadmin")
    public String loginAsAdmin(HttpServletRequest request, HttpSession session) {
        //不允许ajax强求登录页面
        if(WebOptUtils.isAjax(request)){
            return "redirect:/system/exception/error/401";
        }
        if (deploy) {
            //实施人员入口标记
            session.setAttribute(ENTRANCE_TYPE, DEPLOY_LOGIN);
        }

        return useCas ? "redirect:/system/mainframe/logincas":"sys/login";
    }

    /**
     * 登录失败回到登录页
     * @param request HttpServletRequest
     * @param session session
     * @return 登录界面
     */
    @ApiOperation(value = "登录失败回到登录页", notes = "登录失败回到登录页")
    @GetMapping("/login/error")
    public String loginError(HttpServletRequest request,HttpSession session) {
        //在系统中设定Spring Security 相关的错误信息
        AuthenticationException authException = (AuthenticationException)
                session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        //设置错误信息
        if(authException!=null) {
            session.setAttribute(LOGIN_AUTH_ERROR_MSG, authException.getMessage());
        }
        //重新登录
        return login(request,session);
    }

    /**
     * 退出登录
     * @param session session
     * @return 登出页面
     */
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute(ENTRANCE_TYPE,NORMAL_LOGIN);
        session.removeAttribute(LOGIN_AUTH_ERROR_MSG);
        if(useCas){
            //return "sys/mainframe/index";
            session.invalidate();
            return "redirect:"+casHome+"/logout?service="+localHome+"/system/mainframe/index";
        }
        else {
            return "redirect:/logout";//j_spring_security_logout
        }
    }

    /**
     * 修改密码
     * @param password 旧密码
     * @param newPassword 新密码
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "修改密码", notes = "修改用户登录密码")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "password", value="旧密码",
        required=true, paramType = "query", dataType= "String"
    ),@ApiImplicitParam(
        name = "newPassword", value="新密码",
        required= true, paramType = "query", dataType= "String"
    )})
    @RequestMapping(value ="/changepwd",method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData changepassword(String password, String newPassword,
            HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isBlank(password)) {
            password = request.getParameter("password");
        }
        if(StringUtils.isBlank(newPassword)) {
            newPassword = request.getParameter("newPassword");
        }

        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null){
//            JsonResultUtils.writeErrorMessageJson("用户没有登录，不能修改密码！", response);
            return ResponseData.makeErrorMessage("用户没有登录，不能修改密码！");
        }else{
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), password);
            if(!bo){
//                JsonResultUtils.writeErrorMessageJson("用户输入的密码错误，不能修改密码！", response);
                return ResponseData.makeErrorMessage("用户输入的密码错误，不能修改密码！");
            }else{
                platformEnvironment.changeUserPassword(ud.getUserCode(), newPassword);
//                JsonResultUtils.writeSuccessJson(response);
                return ResponseData.makeSuccessResponse();
            }
        }
    }

    /**
     * 校验密码
     * @param password password
     * @param request request
     */
    @ApiOperation(value = "校验密码", notes = "校验密码是否正确")
    @ApiImplicitParam(
        name = "password", value="当前密码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value ="/checkpwd",method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkpassword(String password, HttpServletRequest request) {
        if(StringUtils.isBlank(password)) {
            password = request.getParameter("password");
        }
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_UNAUTHORIZED,"用户没有登录，不能修改密码！");
        }else{
            boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), password);
            return ResponseData.makeResponseData(bo);
        }
    }

    /**
     * 这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成
     * @param request request
     */
    @ApiOperation(value = "内部通讯的客户端程序使用接口", notes = "这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成")
    @RequestMapping(value="/loginasclient",method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData loginAsClient(HttpServletRequest request) {
        Map<String, Object> formValue = BaseController.collectRequestParameters(request);

        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        String userPwd = StringBaseOpt.objectToString(formValue.get("password"));

        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        if(ud==null){
//            JsonResultUtils.writeErrorMessageJson("用户： "+userCode+"不存在。", response);
            return ResponseData.makeErrorMessage("用户： "+userCode+"不存在。");
        }
        boolean bo=platformEnvironment.checkUserPassword(ud.getUserCode(), userPwd);
        if(!bo){
//            JsonResultUtils.writeErrorMessageJson("用户 名和密码不匹配。", response);
            return ResponseData.makeErrorMessage("用户 名和密码不匹配。");
        }
//        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//            new UsernamePasswordAuthenticationToken(ud, ud.getCredentials(), ud.getAuthorities()));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(ud, ud.getCredentials(), ud.getAuthorities()));

        // 如果是为了和第三方做模拟的单点登录也可以用这个函数，但是需要把下面这一行代码注释去掉
        // SecurityContextUtils.setSecurityContext(ud,request.getSession());
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(SecurityContextUtils.SecurityContextTokenName, request.getSession().getId());
//        JsonResultUtils.writeResponseDataAsJson(resData, response);
        return ResponseData.makeResponseData(resData);
    }


    /**
     * 这个方法用于和第三方对接的验证方式，需要注入名为 thirdPartyCheckUserDetails 的bean 。
     * @param formValue json格式的表单数据 {userCode:"u0000000", token:"231413241234"}
     * @param response response
     */
    @ApiOperation(value="第三方认证接口",
        notes="这时框架留的一个后门，系统如果要使用这个接口，必须配置一个名为thirdPartyCheckUserDetails的bean;" +
        "该方法使用post调用，提交的对象中必须有userCode和token两个属性。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "formValue", value="json格式的表单数据,示例：{userCode:\"u0000000\", token:\"231413241234\"}",
        required=true, paramType = "body", dataType= "String"
    ))
    @RequestMapping(value="/loginasthird",method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData loginAsThird(HttpServletRequest request,HttpServletResponse response,
                    @RequestBody JSONObject formValue) {
        try {
            if (thirdPartyCheckUserDetails == null) {
                thirdPartyCheckUserDetails = ContextLoaderListener.getCurrentWebApplicationContext()
                    .getBean("thirdPartyCheckUserDetails", ThirdPartyCheckUserDetails.class);
            }
        }catch (RuntimeException e){
            //thirdPartyCheckUserDetails = null;
            return ResponseData.makeErrorMessage(e.getLocalizedMessage());
        }
        if(thirdPartyCheckUserDetails == null){
            return ResponseData.makeErrorMessage("系统找不到名为 thirdPartyCheckUserDetails 的 bean。");
        }
        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        Object token = formValue.get("token");

        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        if(ud==null){
            return ResponseData.makeErrorMessage("用户： "+userCode+"不存在。");
        }

        boolean bo = thirdPartyCheckUserDetails.check(ud, token);
        if(!bo){
            return ResponseData.makeErrorMessage("用户："+userCode+ " token:" + StringBaseOpt.objectToString(token) +" 校验不通过");
        }
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            new UsernamePasswordAuthenticationToken(ud, ud.getCredentials(), ud.getAuthorities()));
        // 如果是为了和第三方做模拟的单点登录也可以用这个函数，但是需要把下面这一行代码注释去掉
        // SecurityContextUtils.setSecurityContext(ud,request.getSession());
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData(SecurityContextUtils.SecurityContextTokenName, request.getSession().getId());
        return ResponseData.makeResponseData(resData);
    }

    /**
     * 防跨站请求伪造
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "防跨站请求伪造", notes = "防跨站请求伪造")
    @RequestMapping(value = "/login/csrf",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getLoginCsrfToken(HttpServletRequest request,HttpServletResponse response) {
        if(csrfTokenRepository!=null){
            CsrfToken token = csrfTokenRepository.loadToken(request);
            if(token == null){
                token = csrfTokenRepository.generateToken(request);
                csrfTokenRepository.saveToken( token,  request, response);
            }
            response.setHeader("_csrf_parameter", token.getParameterName());
            response.setHeader("_csrf_header", token.getHeaderName());
            response.setHeader("_csrf", token.getToken());
            return ResponseData.makeResponseData(token);
        }else{
            return ResponseData.makeErrorMessage("Bean csrfTokenRepository not found!");
        }
    }

    /**
     * 防跨站请求伪造
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "防跨站请求伪造", notes = "防跨站请求伪造")
    @RequestMapping(value = "/csrf",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCsrfToken(HttpServletRequest request,HttpServletResponse response) {
       return getLoginCsrfToken(request, response);
    }

    /**
     * 获取验证码
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @RequestMapping(value = "/captchaimage",method = RequestMethod.GET)
    @WrapUpResponseBody(contentType = WrapUpContentType.IMAGE)
    public RenderedImage captchaImage(HttpServletRequest request, HttpServletResponse response) {

        String checkcode = CaptchaImageUtil.getRandomString();
        request.getSession().setAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE, checkcode);
        response.setHeader("Cache-Control", "no-cache");
        return CaptchaImageUtil.generateCaptchaImage(checkcode);
    }

    /**
     * 获取登录验证码
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "获取登录验证码", notes = "获取登录验证码")
    @RequestMapping(value = "/login/captchaimage",method = RequestMethod.GET)
    @WrapUpResponseBody(contentType = WrapUpContentType.IMAGE)
    public RenderedImage loginCaptchaImage( HttpServletRequest request, HttpServletResponse response) {
        return captchaImage(  request,  response);
    }

    /**
     * 校验验证码
     * @param checkcode checkcode
     * @param request request
     */
    @ApiOperation(value = "校验验证码", notes = "校验验证码")
    @ApiImplicitParam(
        name = "checkcode", value="验证码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/checkcaptcha/{checkcode}",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData checkCaptchaImage(@PathVariable String checkcode, HttpServletRequest request) {

        String sessionCode = StringBaseOpt.objectToString(
                    request.getSession().getAttribute(
                            CaptchaImageUtil.SESSIONCHECKCODE));
        Boolean checkResult = StringUtils.equals(checkcode, sessionCode);
        request.getSession().setAttribute(
                SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT,
                checkResult);
        return ResponseData.makeResponseData(checkResult);
    }

    /**
     * 当前登录用户
     * @param request request
     * @param response response
     */
    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/currentuserinfo",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null) {
            return ResponseData.makeErrorMessageWithData(
                request.getSession().getId(),ResponseData.ERROR_UNAUTHORIZED,"No user login on current session!");
        }
        else {
            return ResponseData.makeResponseData(ud.getUserInfo());
        }
    }
    /**
     * 当前登录者
     * @param request request
     */
    @ApiOperation(value = "当前登录者", notes = "当前登录者，CentitUserDetails对象信息")
    @RequestMapping(value = "/currentuser",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCurrentUserDetails(HttpServletRequest request) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null) {
            return ResponseData.makeErrorMessageWithData(
                request.getSession().getId(),ResponseData.ERROR_UNAUTHORIZED,"No user login on current session!");
        }
        else {
            return ResponseData.makeResponseData(ud);
        }
    }
    /**
     * 检验是否登录
     * @param request request
     */
    @ApiOperation(value = "检验是否登录", notes = "检验当前用户用户是否登录")
    @GetMapping("/hasLogin")
    @WrapUpResponseBody(contentType = WrapUpContentType.RAW)
    public Boolean hasLogin(HttpServletRequest request) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        return ud!=null;
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
     */
    @ApiOperation(value = "首页菜单", notes = "获取首页菜单信息")
    @RequestMapping(value = "/menu" , method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray getMenu(HttpServletRequest request) {
        CentitUserDetails userDetails = super.getLoginUser(request);
        if(userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录，请重新登录！");

        }
        Object obj = request.getSession().getAttribute(ENTRANCE_TYPE);
        boolean asAdmin = obj!=null && DEPLOY_LOGIN.equals(obj.toString());
        List<? extends IOptInfo> menuFunsByUser = null;

        if(StringUtils.isNotBlank(topOptId)) {
            menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userDetails.getUserCode(), topOptId, asAdmin);
        }

        if(menuFunsByUser == null || menuFunsByUser.size()==0 ){
            menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userDetails.getUserCode(), asAdmin);
        }

        if(menuFunsByUser==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录,或者没有给用户任何权限，请重新登录！");
        }
        return makeMenuFuncsJson(menuFunsByUser);
    }

    /**
     * 获取子菜单
     * @param optId 菜单代码
     * @param request HttpServletRequest
     */
    @ApiOperation(value = "获取子菜单", notes = "获取子菜单详情")
    @ApiImplicitParam(
        name = "optId", value="菜单代码",
        paramType = "query", dataType= "String"
    )
    @RequestMapping(value = "/submenu" , method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray getMenuUnderOptId(@RequestParam(value="optid", required=false)  String optId,
            HttpServletRequest request) {

        CentitUserDetails userDetails = super.getLoginUser(request);
        if(userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录，请重新登录！");
        }
        Object obj = request.getSession().getAttribute(ENTRANCE_TYPE);
        boolean asAdmin = obj!=null && DEPLOY_LOGIN.equals(obj.toString());
        List<? extends IOptInfo> menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userDetails.getUserCode(), optId, asAdmin);
        if(menuFunsByUser==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录,或者没有给用户任何权限，请重新登录！");
        }
        return makeMenuFuncsJson(menuFunsByUser);
    }

    /**
     * 获取用户有权限的菜单
     * @param userCode 用户代码
     */
    @ApiOperation(value = "获取用户有权限的菜单", notes = "根据用户代码获取用户有权限的菜单")
    @ApiImplicitParam(
        name = "userCode", value="用户代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/userMenu/{userCode}" , method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getMemuByUsercode(@PathVariable String userCode) {

        List<? extends IOptInfo> menuFunsByUser = null;

        if(StringUtils.isNotBlank(topOptId)) {
            menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, topOptId, false);
        }

        if(menuFunsByUser==null || menuFunsByUser.size() == 0){
            menuFunsByUser = platformEnvironment.listUserMenuOptInfos(userCode, false);
        }

        return ResponseData.makeResponseData(makeMenuFuncsJson(menuFunsByUser));
    }

    /**
     * 获取用户某个菜单下有权限的子菜单
     * @param userCode 用户代码
     * @param menuOptId 菜单代码
     */
    @ApiOperation(value = "获取用户有权限的菜单", notes = "根据用户代码和菜单代码获取用户有权限的子菜单")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "userCode", value="用户代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "menuOptId", value="菜单代码",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/useSubrMenu/{userCode}/{menuOptId}" , method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getSubMemuByUsercode(@PathVariable String userCode,@PathVariable String menuOptId) {
        List<? extends IOptInfo> menuFunsByUser  = platformEnvironment
            .listUserMenuOptInfosUnderSuperOptId(userCode, menuOptId, false);
        return ResponseData.makeResponseData(makeMenuFuncsJson(menuFunsByUser));
    }

    /**
     * 校验session是否超时
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 超时跳转异常
     */
    @ApiOperation(value = "校验session是否超时", notes = "校验session是否超时")
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
     */
    @ApiOperation(value = "查询当前用户所有职位", notes = "查询当前用户所有职位")
    @GetMapping(value = "/userpositions")
    @WrapUpResponseBody
    public JSONArray listCurrentUserUnits(HttpServletRequest request) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            throw new ObjectException("用户没有登录或者超时，请重新登录。");
        }

        return DictionaryMapUtils.mapJsonArray(currentUser.getUserUnits(), IUserUnit.class);
    }

    /**
     * 查询当前用户当前职位
     * @param request {@link HttpServletRequest}
     */
    @ApiOperation(value = "查询当前用户当前职位", notes = "查询当前用户当前职位")
    @GetMapping(value = "/usercurrposition")
    @WrapUpResponseBody
    public ResponseData getUserCurrentStaticn(HttpServletRequest request) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
        return ResponseData.makeResponseData(
            DictionaryMapUtils.objectToJSON(currentUser.getCurrentStation().toJavaObject(IUserUnit.class)), IUserUnit.class);
    }

    /**
     * 设置当前用户当前职位
     * @param userUnitId 用户机构Id
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @ApiOperation(value = "设置当前用户当前职位", notes = "根据用户机构id设置当前用户当前职位")
    @ApiImplicitParam(
        name = "userUnitId", value="用户机构Id",
        required= true, paramType = "path", dataType= "String"
    )
    @PutMapping(value = "/setuserposition/{userUnitId}")
    @WrapUpResponseBody
    public ResponseData setUserCurrentStaticn(@PathVariable String userUnitId,
            HttpServletRequest request,HttpServletResponse response) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_SESSION_TIMEOUT,"用户没有登录或者超时，请重新登录。");
        }
        currentUser.setCurrentStationId(userUnitId);
        return ResponseData.makeSuccessResponse();
    }

    /**
     * 验证当前用户是否有某个操作方法的权限
     * @param optId 业务菜单代码
     * @param method 操作方法
     * @return ResponseData
     */
    @ApiOperation(value = "验证当前用户是否有某个操作方法的权限", notes = "验证当前用户是否有某个操作方法的权限")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optId", value="系统业务代码",
            required= true,paramType = "path", dataType= "String"),
        @ApiImplicitParam(
            name = "method", value="操作方法",
            required= true,paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/checkuserpower/{optId}/{method}", method = { RequestMethod.GET })
    @WrapUpResponseBody
    public ResponseData checkUserOptPower(@PathVariable String optId,@PathVariable String method) {
        boolean s = CodeRepositoryUtil.checkUserOptPower(optId,method);
        return ResponseData.makeResponseData(s);
    }

    /**
     * 获取用户在某个职务的用户组列表
     * @param rank 职务代码
     * @return json 结果
     */
    @ApiOperation(value = "获取当前用户具有某个行政职务的任职信息", notes = "获取当前用户具有某个行政职务的任职信息")
    @ApiImplicitParam(
        name = "rank", value="职务代码",
        required= true, paramType = "path", dataType= "String"
    )
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
     * 获取用户在某个岗位的用户组列表
     * @param station 岗位代码
     * @return json结果
     */
    @ApiOperation(value = "获取当前用户具有某个岗位的任职信息", notes = "获取当前用户具有某个岗位的任职信息")
    @ApiImplicitParam(
        name = "station", value="岗位代码",
        required= true, paramType = "path", dataType= "String"
    )
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

    private static Map<String, Object> makeCalcParam(CentitUserDetails userDetails){
        Map<String, Object> dpf = new HashMap<>();
        if(userDetails == null){
            return dpf;
        }
        //当前用户信息
        dpf.put("currentUser", userDetails.getUserInfo());
        dpf.put("currentStation", userDetails.getCurrentStation());
        //当前用户主机构信息
        dpf.put("primaryUnit", CodeRepositoryUtil
            .getUnitInfoByCode(userDetails.getUserInfo().getString("primaryUnit")));
        //当前用户所有机构关联关系信息
        List<? extends IUserUnit>  userUnits = CodeRepositoryUtil
            .listUserUnits(userDetails.getUserCode());
        if(userUnits!=null) {
            dpf.put("userUnits", userUnits);
            Map<String, List<IUserUnit>> rankUnits = new HashMap<>(5);
            Map<String, List<IUserUnit>> stationUnits = new HashMap<>(5);
            for(IUserUnit uu : userUnits ){
                List<IUserUnit> rankUnit = rankUnits.get(uu.getUserRank());
                if(rankUnit==null){
                    rankUnit = new ArrayList<>(4);
                }
                rankUnit.add(uu);
                rankUnits.put(uu.getUserRank(),rankUnit);

                List<IUserUnit> stationUnit = stationUnits.get(uu.getUserStation());
                if(stationUnit==null){
                    stationUnit = new ArrayList<>(4);
                }
                stationUnit.add(uu);
                stationUnits.put(uu.getUserStation(),rankUnit);
            }
            dpf.put("rankUnits", rankUnits);
            dpf.put("stationUnits", stationUnits);
        }
        //当前用户的角色信息
        dpf.put("userRoles", userDetails.getUserRoles());
        return dpf;
    }

    @ApiOperation(value = "测试权限表达式引擎", notes = "测试权限表达式引擎")
    @ApiImplicitParam(
        name = "jsonStr", value="{formula:\"\",unitParams:{U:[]},userParams:{U:[]},rankParams:{U:[]}}",
        required= true, paramType = "body", dataType= "String"
    )
    @PostMapping(value = "testUserEngine")
    @WrapUpResponseBody
    public Set<String> testUserEngine(@RequestBody String jsonStr){
        CentitUserDetails centitUserDetails = WebOptUtils.getLoginUser();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        Object unitParams = jsonObject.getJSONObject("unitParams");
        Object userParams = jsonObject.getJSONObject("userParams");
        Object rankParams = jsonObject.getJSONObject("rankParams");
        Map<String, Integer> rankMap = null;
        if(rankParams!=null) {
            Map<String, Object> objMap = CollectionsOpt.objectToMap(rankParams);
            rankMap = new HashMap<>(objMap.size() + 1);
            for (Map.Entry<String, Object> ent : objMap.entrySet()) {
                rankMap.put(ent.getKey(), NumberBaseOpt.castObjectToInteger(ent.getValue()));
            }
        }

        return SysUserFilterEngine.calcSystemOperators(
            jsonObject.getString("formula"),
            unitParams==null?null:StringBaseOpt.objectToMapStrSet(unitParams),
            userParams==null?null:StringBaseOpt.objectToMapStrSet(userParams),
            rankMap,
            new UserUnitMapTranslate(makeCalcParam(centitUserDetails))
        );
    }

    @ApiOperation(value = "测试权限表达式引擎", notes = "测试权限表达式引擎")
    @ApiImplicitParam(
        name = "jsonStr", value="{formula:\"\",unitParams:{U:[]}}",
        required= true, paramType = "body", dataType= "String"
    )
    @PostMapping(value = "testUnitEngine")
    @WrapUpResponseBody
    public Set<String> testUnitEngine(@RequestBody String jsonStr){
        CentitUserDetails centitUserDetails = WebOptUtils.getLoginUser();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        Object unitParams = jsonObject.getJSONObject("unitParams");
        return SysUnitFilterEngine.calcSystemUnitsByExp(
            jsonObject.getString("formula"),
            unitParams==null?null:StringBaseOpt.objectToMapStrSet(unitParams),
            new UserUnitMapTranslate(makeCalcParam(centitUserDetails))
        );
    }

}
