package com.centit.framework.system.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitPasswordEncoder;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.framework.model.security.ThirdPartyCheckUserDetails;
import com.centit.framework.security.SecurityContextUtils;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.image.CaptchaImageUtil;
import com.centit.support.security.SecurityOptUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.util.*;

@Api(value = "框架中用户权限相关的接口，用户登录接口，第三方认证接口，安全接口",
    tags = "登录、权限、安全控制等接口")
@Controller
@RequestMapping("/mainframe")
public class MainFrameController extends BaseController {

    public static final String ENTRANCE_TYPE = "ENTRANCE_TYPE";
    public static final String NORMAL_LOGIN = "NORMAL";
    public static final String DEPLOY_LOGIN = "DEPLOY";
    public static final String LOGIN_AUTH_ERROR_MSG = "LOGIN_ERROR_MSG";

    public String getOptId() {
        return "mainframe";
    }

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;

    @Autowired
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
    private String localHome;

    @Value("${login.password.minLength:8}")
    private int passwordMinLength;

    @Value("${login.password.strength:3}")
    private int passwordStrength;

    @Value("${logout.success.targetUrl:}")
    private String logoutTargetUrl;
    @Value("${login.cas.casHome:}")
    private String casHome;// https://productsvr.centit.com:8443/cas
    @Value("${app.local.firstpage:}")
    private String firstpage;
    @Value("${app.menu.topoptid:}")
    private String topOptId;

    public void setThirdPartyCheckUserDetails(ThirdPartyCheckUserDetails thirdPartyCheckUserDetails) {
        this.thirdPartyCheckUserDetails = thirdPartyCheckUserDetails;
    }

    /**
     * 登录首页链接，具体登录完成后跳转路径由spring-security-dao.xml中配置
     * param request request
     * param session session
     *
     * @return 登录首页链接
     */
    @ApiOperation(value = "登录首页链接", notes = "登录首页链接，具体登录完成后跳转路径由spring-security-dao.xml中配置")
    @GetMapping({"", "/", "/index"})
    public String index(HttpServletRequest request) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        String redirectUrl = request.getParameter("webUrl");
        if (StringUtils.isNotBlank(redirectUrl)) {
            return "redirect:" + redirectUrl;//"sys/mainframe/index";
        } else {
            return "sys/index";//"redirect:"+ firstpage;//
        }
    }

    /**
     * 跳往cas登录链接
     *
     * @param request request
     * @return 登录后首页URL
     */
    @ApiOperation(value = "跳往cas登录链接", notes = "使用cas登录系统")
    @GetMapping(value = "/logincas")
    public String logincas(HttpServletRequest request) {
        //为了缩短普通管理员登录后首页URL，转发到 /service/
        String redirectUrl = request.getParameter("webUrl");
        if (StringUtils.isNotBlank(redirectUrl)) {
            return "redirect:" + redirectUrl;//"sys/mainframe/index";
        } else {
            return "redirect:" + firstpage;//"sys/mainframe/index";
        }
    }

    /**
     * 登录界面入口
     *
     * @return 登录界面
     */
    @GetMapping("/login")
    @ApiOperation(value = "登录界面入口", notes = "登录界面入口")
    public void login(HttpServletResponse response) {
        //不允许ajax强求登录页面
        JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录！",response);
    }

    /**
     * 以管理员登录界面
     *
     * @param session session
     * @param request HttpServletRequest
     * @return 登录界面
     */
    @ApiOperation(value = "以管理员登录界面", notes = "以管理员身份登录界面")
    @GetMapping("/loginasadmin")
    public String loginAsAdmin(HttpServletRequest request, HttpSession session) {
        //不允许ajax强求登录页面
        if (WebOptUtils.isAjax(request)) {
            return "redirect:/system/exception/error/401";
        }
        if (deploy) {
            //实施人员入口标记
            session.setAttribute(ENTRANCE_TYPE, DEPLOY_LOGIN);
        }

        return useCas ? "redirect:/system/mainframe/logincas" : "sys/login";
    }

    /**
     * 登录失败回到登录页
     *
     * @param response HttpServletResponse
     * @param session session
     * @return 登录界面
     */
    @ApiOperation(value = "登录失败回到登录页", notes = "登录失败回到登录页")
    @GetMapping("/login/error")
    @WrapUpResponseBody
    public void loginError(HttpSession session, HttpServletResponse response) {
        //在系统中设定Spring Security 相关的错误信息
        AuthenticationException authException = (AuthenticationException)
            session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        //设置错误信息
        if (authException != null) {
            session.setAttribute(LOGIN_AUTH_ERROR_MSG, authException.getMessage());
        }
        //重新登录
        login(response);
    }

    /**
     * 退出登录
     *
     * @param session session
     * @return 登出页面
     */
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute(ENTRANCE_TYPE, NORMAL_LOGIN);
        session.removeAttribute(LOGIN_AUTH_ERROR_MSG);
        if (useCas) {
            //return "sys/mainframe/index";
            session.invalidate();
            if (StringUtils.isBlank(logoutTargetUrl)) {
                return "redirect:" + casHome + "/logout?service=" + localHome + "/system/mainframe/logincas";
            } else {
                return "redirect:" + casHome + "/logout?service=" + logoutTargetUrl;
            }
        } else {
            return "redirect:/logout"; //j_spring_security_logout
        }
    }

    @ApiOperation(value = "locode退出登录", notes = "locode退出登录")
    @GetMapping("/logoutlocode")
    @WrapUpResponseBody
    public String logoutLocode(HttpServletRequest request, HttpServletResponse response,
                             Authentication auth){
        CookieClearingLogoutHandler cookieClearingLogoutHandler=new CookieClearingLogoutHandler("JSESSIONID","remember-me");
        cookieClearingLogoutHandler.logout(request,response,auth);
        SecurityContextLogoutHandler securityContextLogoutHandler=new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response,auth);
        return "ok";
    }

    /**
     * 修改密码
     * @param request     request
     * @return ResponseData
     */
    @ApiOperation(value = "修改密码", notes = "修改用户登录密码")
    @RequestMapping(value = "/changepwd", method = RequestMethod.PUT)
    @WrapUpResponseBody
    public ResponseData changepassword(@RequestBody String jsonBody, HttpServletRequest request) {
        JSONObject objBody = JSONObject.parseObject(jsonBody);
        String password = SecurityOptUtils.decodeSecurityString(objBody.getString("password"));
        String newPassword = SecurityOptUtils.decodeSecurityString(objBody.getString("newPassword"));

        if (CentitPasswordEncoder.checkPasswordStrength(newPassword, passwordMinLength ) < passwordStrength) {
            return ResponseData.makeErrorMessage("新的密码强度太低，请输入符合要求的密码！");
        }

        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage("用户没有登录，不能修改密码！");
        } else {
            boolean bo = platformEnvironment.checkUserPassword(userCode, password);
            if (bo) {
                platformEnvironment.changeUserPassword(userCode, newPassword);
                return ResponseData.successResponse;
            } else {
                return ResponseData.makeErrorMessage("用户输入的密码错误，不能修改密码！");
            }
        }
    }

    /**
     * 校验密码
     *
     * @param password password
     * @param request  request
     * @return ResponseData
     */
    @ApiOperation(value = "校验密码", notes = "校验密码是否正确")
    @ApiImplicitParam(
        name = "password", value = "当前密码",
        required = true, paramType = "path", dataType = "String"
    )
    @RequestMapping(value = "/checkpwd", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData checkpassword(String password, HttpServletRequest request) {
        if (StringUtils.isBlank(password)) {
            password = request.getParameter("password");
        }
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_UNAUTHORIZED, "用户没有登录，不能修改密码！");
        } else {
            boolean bo = platformEnvironment.checkUserPassword(userCode, password);
            return ResponseData.makeResponseData(bo);
        }
    }

    /**
     * 这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成
     *
     * @param request request
     * @return ResponseData
     */
    @ApiOperation(value = "内部通讯的客户端程序使用接口", notes = "这个方法是个内部通讯的客户端程序使用的，客户端程序通过用户代码（注意不是用户名）和密码登录，这个密码建议随机生成")
    @RequestMapping(value = "/loginasclient", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData loginAsClient(HttpServletRequest request) {
        Map<String, Object> formValue = BaseController.collectRequestParameters(request);

        String userCode = StringBaseOpt.objectToString(formValue.get("userCode"));
        String userPwd = StringBaseOpt.objectToString(formValue.get("password"));
        boolean bo = platformEnvironment.checkUserPassword(userCode, userPwd);
        if (!bo) {
            return ResponseData.makeErrorMessage("用户 名和密码不匹配。");
        }
        CentitUserDetails ud = platformEnvironment.loadUserDetailsByUserCode(userCode);
        SecurityContextHolder.getContext().setAuthentication(ud);
        SecurityContextUtils.fetchAndSetLocalParams(ud, request, platformEnvironment);
        //new UsernamePasswordAuthenticationToken(ud, ud.getCredentials(), ud.getAuthorities()));
        // 如果是为了和第三方做模拟的单点登录也可以用这个函数，但是需要把下面这一行代码注释去掉
        return SecurityContextUtils.makeLoginSuccessResponse(ud, request);
    }

    /**
     * 这个方法用于和第三方对接的验证方式，需要注入名为 thirdPartyCheckUserDetails 的bean 。
     *
     * @param formJson json格式的表单数据 {userCode:"u0000000", token:"231413241234"}
     * @param request  HttpServletRequest
     * @return ResponseData
     */
    @ApiOperation(value = "第三方认证接口",
        notes = "这时框架留的一个后门，系统如果要使用这个接口，必须配置一个名为thirdPartyCheckUserDetails的bean;" +
            "该方法使用post调用，提交的对象中必须有userCode和token两个属性。")
    @ApiImplicitParams(@ApiImplicitParam(
        name = "formValue", value = "json格式的表单数据,示例：{userCode:\"u0000000\", token:\"231413241234\"}",
        required = true, paramType = "body", dataType = "String"
    ))
    @RequestMapping(value = "/loginasthird", method = RequestMethod.POST)
    @WrapUpResponseBody
    public ResponseData loginAsThird(HttpServletRequest request,
                                     @RequestBody String formJson) {
        try {
            if (thirdPartyCheckUserDetails == null) {
                thirdPartyCheckUserDetails = ContextLoaderListener.getCurrentWebApplicationContext()
                    .getBean("thirdPartyCheckUserDetails", ThirdPartyCheckUserDetails.class);
            }
        } catch (RuntimeException e) {
            //thirdPartyCheckUserDetails = null;
            return ResponseData.makeErrorMessage(e.getLocalizedMessage());
        }
        if (thirdPartyCheckUserDetails == null) {
            return ResponseData.makeErrorMessage("系统找不到名为 thirdPartyCheckUserDetails 的 bean。");
        }

        CentitUserDetails ud = thirdPartyCheckUserDetails.check(platformEnvironment, JSON.parseObject(formJson));
        if (ud == null) {
            return ResponseData.makeErrorMessage("第三方验证失败: " + formJson);
        }
        SecurityContextUtils.fetchAndSetLocalParams(ud, request, platformEnvironment);
        SecurityContextHolder.getContext().setAuthentication(ud);
        /*request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            new UsernamePasswordAuthenticationToken(ud, ud.getCredentials(), ud.getAuthorities()));*/
        // 如果是为了和第三方做模拟的单点登录也可以用这个函数，但是需要把下面这一行代码注释去掉
        // SecurityContextUtils.setSecurityContext(ud,request.getSession());
        return SecurityContextUtils.makeLoginSuccessResponse(ud, request);
    }

    /**
     * 防跨站请求伪造
     *
     * @param request  request
     * @param response response
     * @return ResponseData
     */
    @ApiOperation(value = "防跨站请求伪造", notes = "防跨站请求伪造")
    @RequestMapping(value = "/login/csrf", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getLoginCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        if (csrfTokenRepository != null) {
            CsrfToken token = csrfTokenRepository.loadToken(request);
            if (token == null) {
                token = csrfTokenRepository.generateToken(request);
                csrfTokenRepository.saveToken(token, request, response);
            }
            response.setHeader("_csrf_parameter", token.getParameterName());
            response.setHeader("_csrf_header", token.getHeaderName());
            response.setHeader("_csrf", token.getToken());
            return ResponseData.makeResponseData(token);
        } else {
            return ResponseData.makeErrorMessage("Bean csrfTokenRepository not found!");
        }
    }

    /**
     * 防跨站请求伪造
     *
     * @param request  request
     * @param response response
     * @return ResponseData
     */
    @ApiOperation(value = "防跨站请求伪造", notes = "防跨站请求伪造")
    @RequestMapping(value = "/csrf", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        return getLoginCsrfToken(request, response);
    }

    /**
     * 获取验证码
     *
     * @param request  request
     * @param response response
     * @return RenderedImage
     */
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @RequestMapping(value = "/captchaimage", method = RequestMethod.GET)
    @WrapUpResponseBody(contentType = WrapUpContentType.IMAGE)
    public RenderedImage captchaImage(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        String sType = request.getParameter("imageType");

        request.getSession().setAttribute(
            SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT, false);

        if("formula".equals(sType)){
            Random random = new Random();
            int a = random.nextInt(100);
            int b = random.nextInt(100);
            boolean c = (random.nextInt(100) % 2) == 1;
            String code, value;
            if(c){
                if(a<b){
                    int d=a;
                    a=b;
                    b=d;
                }
                code = String.valueOf(a) + "-" + String.valueOf(b);
                value = String.valueOf(a-b);
            } else {
                code = String.valueOf(a) + "+" + String.valueOf(b);
                value = String.valueOf(a+b);
            }
            request.getSession().setAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE, value);
            return CaptchaImageUtil.generateCaptchaImage(code);
        } else {
            String checkcode = CaptchaImageUtil.getRandomString();
            request.getSession().setAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE, checkcode);
            return CaptchaImageUtil.generateCaptchaImage(checkcode);
        }
    }

    /**
     * 获取登录验证码
     *
     * @param request  request
     * @param response response
     * @return RenderedImage
     */
    @ApiOperation(value = "获取登录验证码", notes = "获取登录验证码")
    @RequestMapping(value = "/login/captchaimage", method = RequestMethod.GET)
    @WrapUpResponseBody(contentType = WrapUpContentType.IMAGE)
    public RenderedImage loginCaptchaImage(HttpServletRequest request, HttpServletResponse response) {
        return captchaImage(request, response);
    }

    /**
     * 校验验证码
     *
     * @param checkcode checkcode
     * @param request   request
     * @return ResponseData
     */
    @ApiOperation(value = "校验验证码", notes = "校验验证码")
    @ApiImplicitParam(
        name = "checkcode", value = "验证码",
        required = true, paramType = "path", dataType = "String"
    )
    @RequestMapping(value = "/checkcaptcha/{checkcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData checkCaptchaImage(@PathVariable String checkcode, HttpServletRequest request) {

        String sessionCode = StringBaseOpt.objectToString(
            request.getSession().getAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE));
        Boolean checkResult = CaptchaImageUtil.checkcodeMatch(sessionCode, checkcode);

        //清除临时验证码，避免多次重复验证
        request.getSession().setAttribute(
            CaptchaImageUtil.SESSIONCHECKCODE, CaptchaImageUtil.getRandomString(6));

        request.getSession().setAttribute(
            SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT,
            checkResult);
        return ResponseData.makeResponseData(checkResult);
    }

    /**
     * 当前登录用户
     *
     * @param request  request
     * @param response response
     * @return ResponseData
     */
    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/currentuserinfo", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCurrentUserInfo(HttpServletRequest request, HttpServletResponse response) {
        UserInfo userInfo = WebOptUtils.getCurrentUserInfo(request);
        if (userInfo == null) {
            return ResponseData.makeErrorMessageWithData(
                request.getSession().getId(), ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！");
        } else {
            return ResponseData.makeResponseData(userInfo.toJsonWithoutSensitive());
        }
    }

    /**
     * 当前登录者
     *
     * @param request request
     * @return ResponseData
     */
    @ApiOperation(value = "当前登录者信息（可能是userInfo也可能是userDetails）", notes = "当前登录者，CentitUser对象信息")
    @RequestMapping(value = "/currentuser", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Object getCurrentUser(HttpServletRequest request) {
        Object ud = WebOptUtils.getLoginUser(request);
        if (ud == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！");
        } else {
            if(ud instanceof CentitUserDetails){
                return ((CentitUserDetails) ud).toJsonWithoutSensitive();
            }
            return ud;
        }
    }

    @ApiOperation(value = "当前登录者详细信息", notes = "当前登录者，CentitUserDetails对象信息")
    @RequestMapping(value = "/currentuserdetails", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Object getCurrentUserDetails(HttpServletRequest request) {
        CentitUserDetails ud = WebOptUtils.getCurrentUserDetails(request);
        if (ud == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！");
        } else {
            return ud.toJsonWithoutSensitive();
        }
    }

    /**
     * 检验是否登录
     *
     * @param request request
     * @return Boolean
     */
    @ApiOperation(value = "检验是否登录", notes = "检验当前用户用户是否登录")
    @GetMapping("/hasLogin")
    @WrapUpResponseBody(contentType = WrapUpContentType.RAW)
    public Boolean hasLogin(HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        return StringUtils.isNotBlank(userCode);
    }

    private JSONArray makeMenuFuncsJson(List<OptInfo> menuFunsByUser) {
        return ViewDataTransform.makeTreeViewJson(menuFunsByUser,
            ViewDataTransform.createStringHashMap("id", "optId",
                "pid", "preOptId",
                "text", "localOptName",
                "url", "optRoute",
                "icon", "icon",
                "children", "children",
                "isInToolbar", "isInToolbar",
                "orderInd","orderInd"
                //"attributes.external","pageType"
            ), (jsonObject, obj) -> jsonObject.put("external", !("D".equals(obj.getPageType()))));
    }

    /**
     * 首页菜单
     *
     * @param osId    系统id
     * @param request HttpServletRequest
     * @return JSONArray
     */
    @ApiOperation(value = "首页菜单", notes = "获取首页菜单信息")
    @RequestMapping(value = "/menu/{osId}", method = RequestMethod.GET)
    @ApiImplicitParam(
        name = "osId", value = "应用主键applicationID",
        required = true, paramType = "path", dataType = "String"
    )
    @WrapUpResponseBody
    public JSONArray getMenu(@PathVariable String osId, HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！");

        }
        Object obj = request.getSession().getAttribute(ENTRANCE_TYPE);
        boolean asAdmin = obj != null && DEPLOY_LOGIN.equals(obj.toString());
        List<OptInfo> menuFunsByUser = null;

        menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, osId, asAdmin);

        if ((menuFunsByUser == null || menuFunsByUser.size() == 0) && StringUtils.isNotBlank(topOptId)) {
            menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, topOptId, asAdmin);
        }

        if (menuFunsByUser == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录,或者没有给用户任何权限，请重新登录！");
        }
        return makeMenuFuncsJson(menuFunsByUser);
    }

    /**
     * 获取子菜单
     *
     * @param optid   菜单代码
     * @param asadmin 作为管理员
     * @param request HttpServletRequest
     * @return JSONArray
     */
    @ApiOperation(value = "获取子菜单", notes = "获取子菜单详情")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optid", value = "菜单代码",
            paramType = "query", dataType = "String"
        ),
        @ApiImplicitParam(
            name = "asadmin", value = "作为管理员 t/f",
            paramType = "query", dataType = "String"
        )})
    @RequestMapping(value = "/submenu", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray getMenuUnderOptId(@RequestParam(value = "optid", required = false) String optid,
                                       @RequestParam(value = "asadmin", required = false) String asadmin,
                                       HttpServletRequest request) {

        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！");
        }
        if(WebOptUtils.isTenantTopUnit(request)){
            String topUnit=WebOptUtils.getCurrentTopUnit(request);
            List<OsInfo> osInfos = platformEnvironment.listOsInfos(topUnit);
            boolean findOsId= osInfos.stream().anyMatch(osInfo -> osInfo.getOsId().equals(optid));
            if(!findOsId) {
                throw new ObjectException(ResponseData.ERROR_FORBIDDEN,
                    "用户不属于当前租户，请切换租户！");
            }
        }
        List<OptInfo> menuFunsByUser = platformEnvironment
            .listUserMenuOptInfosUnderSuperOptId(userCode,optid,BooleanBaseOpt.castObjectToBoolean(asadmin, false));
        if (menuFunsByUser == null) {
            throw new ObjectException(ResponseData.ERROR_BAD_PROCESS_POWER,
                "该用户没有任何权限！");
        }
        return makeMenuFuncsJson(menuFunsByUser);
    }

    /**
     * 获取用户有权限的菜单
     *
     * @param userCode 用户代码
     * @return ResponseData
     */
    @ApiOperation(value = "获取用户有权限的菜单", notes = "根据用户代码获取用户有权限的菜单")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "osId", value = "应用主键applicationID",
            required = true, paramType = "path", dataType = "String"
        ), @ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String"
    )
    })
    @RequestMapping(value = "/userMenu/{osId}/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getMemuByUsercode(@PathVariable String osId, @PathVariable String userCode) {

        List<OptInfo> menuFunsByUser = null;

        menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, osId, false);

        if ((menuFunsByUser == null || menuFunsByUser.size() == 0) && StringUtils.isNotBlank(topOptId)) {
            menuFunsByUser = platformEnvironment.listUserMenuOptInfosUnderSuperOptId(userCode, topOptId, false);
        }

        return ResponseData.makeResponseData(makeMenuFuncsJson(menuFunsByUser));
    }

    /**
     * 获取用户某个菜单下有权限的子菜单
     *
     * @param userCode  用户代码
     * @param menuOptId 菜单代码
     * @return ResponseData
     */
    @ApiOperation(value = "获取用户有权限的菜单", notes = "根据用户代码和菜单代码获取用户有权限的子菜单")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "userCode", value = "用户代码",
        required = true, paramType = "path", dataType = "String"
    ), @ApiImplicitParam(
        name = "menuOptId", value = "菜单代码",
        required = true, paramType = "path", dataType = "String"
    )})
    @RequestMapping(value = "/useSubrMenu/{userCode}/{menuOptId}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getSubMemuByUsercode(@PathVariable String userCode, @PathVariable String menuOptId) {
        List<OptInfo> menuFunsByUser = platformEnvironment
            .listUserMenuOptInfosUnderSuperOptId(userCode, menuOptId, false);
        return ResponseData.makeResponseData(makeMenuFuncsJson(menuFunsByUser));
    }

    @ApiOperation(value = "获取当前session", notes = "获取当前session")
    @RequestMapping(value = "/session", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String getSession(HttpServletRequest request) {
        return request.getSession().getId();
    }

    /**
     * 查询当前用户所有职位
     *
     * @param request {@link HttpServletRequest}
     * @return JSONArray
     */
    @ApiOperation(value = "查询当前用户所有职位", notes = "查询当前用户所有职位")
    @GetMapping(value = "/userstations")
    @WrapUpResponseBody
    public JSONArray listCurrentUserUnits(HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if (currentUser == null) {
            throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
        if (currentUser instanceof CentitUserDetails) {
            return DictionaryMapUtils.objectsToJSONArray(
                ((CentitUserDetails) currentUser).getUserUnits());
        }
        return null;
    }

    @ApiOperation(value = "查询当前用户所属租户", notes = "查询当前用户所属租户")
    @GetMapping(value = {"/topUnit", "/tenant"})
    @WrapUpResponseBody
    public List<UnitInfo> listCurrentTopUnits(HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        //String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
        return platformEnvironment.listUserTopUnits(userCode);

    }

    @GetMapping(value = "/userroles")
    @WrapUpResponseBody
    public List<UserRole> listCurrentUserRoles(HttpServletRequest request) {
        return platformEnvironment
            .listUserRoles(WebOptUtils.getCurrentTopUnit(request), WebOptUtils.getCurrentUserCode(request));
    }

    /**
     * 查询当前用户当前职位
     *
     * @param request {@link HttpServletRequest}
     * @return ResponseData
     */
    @ApiOperation(value = "查询当前用户当前职位", notes = "查询当前用户当前职位")
    @GetMapping(value = "/usercurrstation")
    @WrapUpResponseBody
    public Map<String, Object> getUserCurrentStaticn(HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if (currentUser instanceof CentitUserDetails) {
            return (JSONObject) DictionaryMapUtils.objectToJSON(
                ((CentitUserDetails) currentUser).getCurrentStation());
        }
        throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT,
            "用户没有登录或者超时，请重新登录。");
    }

    /**
     * 设置当前用户当前职位
     *
     * @param userUnitId 用户机构Id
     * @param request    {@link HttpServletRequest}
     */
    @ApiOperation(value = "设置当前用户当前职位", notes = "根据用户机构id设置当前用户当前职位")
    @ApiImplicitParam(
        name = "userUnitId", value = "用户机构Id",
        required = true, paramType = "path", dataType = "String"
    )
    @PutMapping(value = "/setuserstation/{userUnitId}")
    @WrapUpResponseBody
    public void setUserCurrentStaticn(@PathVariable String userUnitId,
                                      HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if (currentUser instanceof CentitUserDetails) {
            ((CentitUserDetails) currentUser).setCurrentStationId(userUnitId);
        } else {
            throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
    }

    /**
     * 验证当前用户是否有某个操作方法的权限
     *
     * @param optId  业务菜单代码
     * @param method 操作方法
     * @return ResponseData
     */
    @ApiOperation(value = "验证当前用户是否有某个操作方法的权限", notes = "验证当前用户是否有某个操作方法的权限")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optId", value = "系统业务代码",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "method", value = "操作方法",
            required = true, paramType = "path", dataType = "String")
    })
    @RequestMapping(value = "/checkuserpower/{optId}/{method}", method = {RequestMethod.GET})
    @WrapUpResponseBody
    public ResponseData checkUserOptPower(@PathVariable String optId, @PathVariable String method) {
        boolean s = CodeRepositoryUtil.checkUserOptPower(optId, method);
        return ResponseData.makeResponseData(s);
    }

    /**
     * 获取用户在某个职务的用户组列表
     *
     * @param rank    职务代码
     * @param request HttpServletRequest
     * @return json 结果
     */
    @ApiOperation(value = "获取当前用户具有某个行政职务的任职信息", notes = "获取当前用户具有某个行政职务的任职信息")
    @ApiImplicitParam(
        name = "rank", value = "职务代码",
        required = true, paramType = "path", dataType = "String"
    )
    @GetMapping(value = "/userranks/{rank}")
    @WrapUpResponseBody
    public JSONArray listUserUnitsByRank(@PathVariable String rank, HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException("用户没有登录或者超时，请重新登录。");
        }
        return DictionaryMapUtils.objectsToJSONArray(
            CodeRepositoryUtil.listUserUnitsByRank(topUnit, userCode, rank));
    }

    /**
     * 获取用户在某个岗位的用户组列表
     *
     * @param station 岗位代码
     * @param request HttpServletRequest
     * @return json结果
     */
    @ApiOperation(value = "获取当前用户具有某个岗位的任职信息", notes = "获取当前用户具有某个岗位的任职信息")
    @ApiImplicitParam(
        name = "station", value = "岗位代码",
        required = true, paramType = "path", dataType = "String"
    )
    @GetMapping(value = "/userstations/{station}")
    @WrapUpResponseBody
    public ResponseData listUserUnitsByStation(@PathVariable String station, HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if (StringUtils.isBlank(userCode)) {
            return new ResponseSingleData("用户没有登录或者超时，请重新登录");
        }
        return ResponseSingleData.makeResponseData(
            DictionaryMapUtils.objectsToJSONArray(
                CodeRepositoryUtil.listUserUnitsByStation(topUnit, userCode, station)));
    }

    @ApiOperation(value = "机构树", notes = "获取当前租户下的指定机构下面的所有机构，并以树形形式提供。")
    @ApiImplicitParam(
        name = "unitCode", value = "起始机构，空就用topUnit代替",  paramType = "query", dataType = "String"
    )
    @RequestMapping(value = "/unitTree", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> listUnitTree(String unitCode,  HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,"您未登录!");
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if(StringUtils.isBlank(unitCode)){
            unitCode = topUnit;
        }
        return CodeRepositoryUtil.fetchAllSubUnits(topUnit, unitCode, true);
    }

    @ApiOperation(value = "机构用户树", notes = "获取当前租户下的指定机构下面的所有用户，并以树形形式提供。")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "unitCode", value = "起始机构， 空就用topUnit代替", paramType = "query", dataType = "String"
        ), @ApiImplicitParam(
            name = "relType", value = "用户关联关系：归属部门 T 工作部门 F 借出部门 O 借入部门 I，所有 A或者 空",
            paramType = "query", dataType = "String"
        )})
    @RequestMapping(value = "/unitUserTree", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray listUnitUserTree(String unitCode, String relType, HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN,"您未登录!");
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        if(StringUtils.isBlank(unitCode)){
            unitCode = topUnit;
        }

        JSONArray allUnits = new JSONArray();
        List<UnitInfo> unitInfos = CodeRepositoryUtil.fetchAllSubUnits(topUnit, unitCode, true);
        for(UnitInfo unitInfo : unitInfos){
            List<UserUnit> userUnits = CodeRepositoryUtil.listUnitUsers(unitInfo.getUnitCode());

            JSONArray allSubUser = new JSONArray();
            for(UserUnit uc : userUnits) {
                if(StringUtils.isBlank(relType) || "A".equalsIgnoreCase(relType) || relType.equalsIgnoreCase(uc.getRelType())) {
                    UserInfo tempUi = CodeRepositoryUtil.getUserInfoByCode(topUnit, uc.getUserCode());
                    if(tempUi != null) {
                        allSubUser.add(JSON.toJSON(tempUi));
                    }
                }
            }
            JSONObject jsonObject = JSONObject.from(unitInfo);
            jsonObject.put("users", allSubUser);
            allUnits.add(jsonObject);
        }
        return allUnits;
    }

    @ApiOperation(value = "预览权限表达式对应用户", notes =
        "表达式为itemExp ([或| itemExp][与& itemExp][非! itemExp])的形式，itemExp为下列形式\n" +
            "D()P()DT()DL()GW()XZ()R()UT()UL()U()RO()\n" +
            "* D 根据机构代码过滤 D(机构表达式)\n" +
            "* P 根据机构代码过滤主要机构\n" +
            "* DT 根据机构类型过滤 DT(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* DL 根据机构标签过滤 DL(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* GW 根据岗位过滤 GW(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* XZ 根据行政职务过滤 XZ(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* R 根据行政职务等级过滤 R(U) / R(U-) / R(U-1) / R(U--) /R(U-1--)\n" +
            "* U 根据用户代码过滤 U(用户变量|\"用户代码常量\" [,用户变量|\"用户代码常量])\n" +
            "* UT 根据用户类型过滤 UT(\"用户类型常量\" [,\"用户类型常量\"])\n" +
            "* UL 根据用户标签过滤 UL(\"用户标记常量\" [,\"用户标记常量\"])\n" +
            "* RO 根据用户角色过滤 RO(\"系统角色代码常量\" [,\"系统角色代码常量\"])")
    @ApiImplicitParam(
        name = "jsonStr", value = "参数格式josn示例: \u007B formula:unitParams:\u007BU: \u005B \u005D \u007D,userParams:\u007BU:\u005B \u005D\u007D,rankParams:\u007BU:\u005B \u005D\u007D\u007D",
        required = true, paramType = "body", dataType = "String"
    )
    @PostMapping(value = "/userEngine")
    @WrapUpResponseBody
    public JSONArray viewFormulaUsers(@RequestBody String jsonStr, HttpServletRequest request) {
        if(StringBaseOpt.isNvl(jsonStr)){
            return null;
        }
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Object centitUserDetails = WebOptUtils.getLoginUser(request);
        JSONObject jsonObject = JSONObject.parse(jsonStr);
        Object unitParams = jsonObject.getJSONObject("unitParams");
        Object userParams = jsonObject.getJSONObject("userParams");
        Object rankParams = jsonObject.getJSONObject("rankParams");
        Map<String, String> rankMap = null;
        if (rankParams != null) {
            Map<String, Object> objMap = CollectionsOpt.objectToMap(rankParams);
            rankMap = new HashMap<>(objMap.size() + 1);
            for (Map.Entry<String, Object> ent : objMap.entrySet()) {
                rankMap.put(ent.getKey(), StringBaseOpt.castObjectToString(ent.getValue()));
            }
        }
        Set<String> sUsers = SysUserFilterEngine.calcSystemOperators(
            jsonObject.getString("formula"), topUnit,
            unitParams == null ? null : StringBaseOpt.objectToMapStrSet(unitParams),
            userParams == null ? null : StringBaseOpt.objectToMapStrSet(userParams),
            rankMap,
            new UserUnitMapTranslate(CacheController.makeCalcParam(centitUserDetails))
        );

        List<UserInfo> userInfos = new ArrayList<>();
        for (String uc : sUsers) {
            UserInfo tempUi = CodeRepositoryUtil.getUserInfoByCode(topUnit, uc);
            if(tempUi != null) {
                userInfos.add(tempUi);
            }
        }
        Collections.sort(userInfos, (o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.getUserOrder(), o2.getUserOrder(), false));
        return JSONArray.copyOf(userInfos);
    }

    @ApiOperation(value = "预览权限表达式对应机构", notes =
        "表达式为itemExp ([或| itemExp][与& itemExp][非! itemExp])的形式，itemExp为下列形式\n" +
            "D()P()DT()DL()\n" +
            "* D 根据机构代码过滤 D(机构表达式)\n" +
            "* P 根据机构代码过滤主要机构\n" +
            "* DT 根据机构类型过滤 DT(\"角色代码常量\" [,\"角色代码常量\"])\n" +
            "* DL 根据机构标签过滤 DL(\"角色代码常量\" [,\"角色代码常量\"])])")
    @ApiImplicitParam(
        name = "jsonStr", value = "参数格式josn示例: \u007Bformula:\"\",unitParams:\u007BU:\u005B \u005D\u007D\u007D",
        required = true, paramType = "body", dataType = "String"
    )
    @WrapUpResponseBody
    @PostMapping(value = "/unitEngine")
    public JSONArray viewFormulaUnits(@RequestBody String jsonStr, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Object centitUserDetails = WebOptUtils.getLoginUser(request);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        Object unitParams = jsonObject.getJSONObject("unitParams");
        Set<String> sUnits = SysUnitFilterEngine.calcSystemUnitsByExp(
            jsonObject.getString("formula"), topUnit,
            unitParams == null ? null : StringBaseOpt.objectToMapStrSet(unitParams),
            new UserUnitMapTranslate(CacheController.makeCalcParam(centitUserDetails))
        );
        List<UnitInfo> unitInfos = new ArrayList<>();
        for (String uc : sUnits) {
            UnitInfo tempUi = CodeRepositoryUtil.getUnitInfoByCode(topUnit, uc);
            if(tempUi != null) {
                unitInfos.add(tempUi);
            }
        }
        unitInfos.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.getUnitOrder(), o2.getUnitOrder(), false));
        return JSONArray.copyOf(unitInfos);
    }

    @ApiOperation(value = "获取当前租户下所有的机构", notes = "获取当前租户下所有的机构。")
    @RequestMapping(value = "/currentunits", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> listCurrentUnits(HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录!");
        }
        return platformEnvironment.listAllUnits(WebOptUtils.getCurrentTopUnit(request));
    }

    @ApiOperation(value = "获取当前租户下的所有的用户", notes = "获取当前租户下所有的用户。")
    @RequestMapping(value = "/currentusers", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UserInfo> listCurrentUsers(HttpServletRequest request) {
        String userCode = WebOptUtils.getCurrentUserCode(request);
        if (StringUtils.isBlank(userCode)) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录!");
        }
        String topUnit=WebOptUtils.getCurrentTopUnit(request);
        List<UserUnit> userUnits = CodeRepositoryUtil.listAllUserUnits(topUnit);
        List<UserInfo> result = new ArrayList<>();
        for(UserUnit un : userUnits){
            if(Objects.equals(un.getRelType(),"T")){
                UserInfo userInfo= CodeRepositoryUtil.getUserInfoByCode(topUnit, un.getUserCode());
                if(userInfo != null) {
                    //userInfo.setPrimaryUnit(un.getUnitCode());
                    result.add(userInfo);
                }
            }
        }
        return result;
    }

}
