package com.centit.framework.core.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.ListOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.network.HtmlFormUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sx
 * 2013年12月30日
 */

//@Controller
@SuppressWarnings("unused")
public abstract class BaseController {
    
    /**
     * 转换查询参数为字符串，用于 = 或者 like 查询
     */
    public static final String SEARCH_STRING_PREFIX = "s_";

    public static final int SEARCH_STRING_PREFIX_LEN = 2;
    
    /**
     * 转换查询参数为字符串数组，用于 in 查询
     */
    public static final String SEARCH_ARRAY_PREFIX = "a_";

    public static final int SEARCH_ARRAY_PREFIX_LEN = 2;
    
    /**
     * 转换查询参数为数字，用于 Long 类型的 = 或者 like 查询
     */
    public static final String SEARCH_NUMBER_PREFIX = "n_";
    
    public static final int SEARCH_NUMBER_PREFIX_LEN = 2;
    
    /**
     * 转换查询参数为数字数组，用于 Long 类型的 in 查询
     */
    public static final String SEARCH_NUMBER_ARRAY_PREFIX = "na_";
    
    public static final int SEARCH_NUMBER_ARRAY_PREFIX_LEN = 3;
    

    protected Logger logger = LoggerFactory.getLogger(BaseController.class);
    /**
     * 当前log4j日志是否打开Debug模式
     */
    protected boolean logDebug = logger.isDebugEnabled();

    protected static final String PAGE_DESC = "pageDesc";

    protected static final String OBJECT = "object";

    protected static final String OBJLIST = "objList";

    /*public  String getOptId(){
        return "NOT_DEFINED";
    }*/
    /**
     * 绑定所有字符串参数绑定，将Html字符转义 和日期参数转义
     *
     * @param binder {@link WebDataBinder}
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringPropertiesEditor(true));
        binder.registerCustomEditor(Date.class, new DatePropertiesEditor());
        binder.registerCustomEditor(java.sql.Date.class, new SqlDatePropertiesEditor());
        binder.registerCustomEditor(java.sql.Timestamp.class, new SqlTimestampPropertiesEditor());
    }

    /**
     * 前端统一异常处理，
     *
     * @param ex {@link BindException}
     * @param request request
     * @param response response
     * @throws IOException IOException
     */
    //(value = {BindException.class,MethodArgumentNotValidException.class})
    @ExceptionHandler
    public void exceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response)
            throws IOException {        
        logger.error(ex.getMessage(), ex);
        if (WebOptUtils.isAjax(request)) {

            BindingResult bindingResult = null;
            if(ex instanceof BindException){
                bindingResult =((BindException)ex).getBindingResult();
            }else if(ex instanceof MethodArgumentNotValidException){
                bindingResult =((MethodArgumentNotValidException)ex).getBindingResult();
            }
            
            if(bindingResult!=null){
                ResponseMapData responseData = new ResponseMapData(ResponseData.ERROR_BAD_REQUEST);
                StringBuilder errMsg = new StringBuilder();

                if (bindingResult.hasErrors()) {
                    for (FieldError fieldError : bindingResult.getFieldErrors()) {
                        responseData.addResponseData(fieldError.getField(), fieldError.getDefaultMessage());

                        errMsg.append(fieldError.getField()).append("：")
                            .append(fieldError.getDefaultMessage()).append("；");

                    }
                }
                responseData.setResponseMessage(errMsg.toString());
                JsonResultUtils.writeResponseDataAsJson(responseData, response);
            }else{
                // 如果是非绑定错误，需要显示抛出异常帮助前台调试错误
                JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_INTERNAL_SERVER_ERROR,
                        StringUtils.isNotEmpty(ex.getMessage()) ? ex.getMessage() : ex.toString(), response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/system/exception/error/500");
        }
    }

    /**
     * 获取当前用户信息
     * @param request request
     * @return 当前用户信息
     */
    protected CentitUserDetails getLoginUser(HttpServletRequest request) {
        return WebOptUtils.getLoginUser(request);
    }

    protected String getLoginUserName(HttpServletRequest request) {
        CentitUserDetails ud = getLoginUser(request);
        if(ud==null)
            return null;
        return ud.getUsername();
    }

    /**
     *
     * @param request request
     * @return 登录用户code
     */
    protected String getLoginUserCode(HttpServletRequest request) {
        CentitUserDetails ud = getLoginUser(request);
        if(ud==null)
            return null;
        return ud.getUserCode();
    }

    /**
     * 将查询条件转换为Dao中hql语句的参数变量
     * 这个中的规则是为了兼容以前的版本，新的版本不需要添加任何前缀
     * @param request request
     * @return map 将查询条件转换为Dao中hql语句的参数变量
     */
    public static Map<String, Object> convertSearchColumn(HttpServletRequest request) {
        // log.error("规则化前参数表：" + paramMap.toString());
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = new HashMap<>();
        //map.put("isValid", "T");
        for (Map.Entry<String, String[]> ent : parameterMap.entrySet()) {
            String key = ent.getKey();

            // 查询字符串 s_
            if (key.startsWith(SEARCH_STRING_PREFIX)) {
                String sKey = key.substring(SEARCH_STRING_PREFIX_LEN);
                String sValue = HtmlFormUtils.getParameterString(ent.getValue());
                if (sValue != null) {
                    if ("isAll".equals(sKey)) {
                        if ("true".equalsIgnoreCase(sValue) || "1".equals(sValue))
                            map.remove("isvalid");
                    } else
                        map.put(sKey, sValue);
                }
            }
            // 查询数组 a_
            else if (key.startsWith(SEARCH_ARRAY_PREFIX)) {
                String sKey = key.substring(SEARCH_ARRAY_PREFIX_LEN);
                Object sValue = ent.getValue();//HtmlFormUtils.getParameterObject(ent.getValue());
                map.put(sKey, sValue);
            }
            // 查询数字 n_
            else if (key.startsWith(SEARCH_NUMBER_PREFIX)) {
                String sKey = key.substring(SEARCH_NUMBER_PREFIX_LEN);
                String sValue = HtmlFormUtils.getParameterString(ent.getValue());                
                map.put(sKey, NumberBaseOpt.parseLong(sValue));
            }
            // 查询数字数组 na_
            else if (key.startsWith(SEARCH_NUMBER_ARRAY_PREFIX)) {
                String sKey = key.substring(SEARCH_NUMBER_ARRAY_PREFIX_LEN);
                
                String[] sValue = HtmlFormUtils.getParameterStringArray(ent.getValue());  
                if(sValue==null){
                    map.put(sKey,null);
                }else{
                    Long[] ll = new Long[sValue.length];                
                    for (int i=0;i<sValue.length;i++) {
                        ll[i]=NumberBaseOpt.parseLong(sValue[i]);
                    }                    
                    map.put(sKey, ll);
                }
            }
            else if (StringUtils.equals(key, CodeBook.SELF_ORDER_BY)) {
                map.put(key, HtmlFormUtils.getParameterString(ent.getValue()));
            }
            else if (StringUtils.equals(key, CodeBook.TABLE_SORT_FIELD)) {
                map.put(key, HtmlFormUtils.getParameterString(ent.getValue()));
            }
            else if (StringUtils.equals(key, CodeBook.TABLE_SORT_ORDER)) {
                map.put(key, HtmlFormUtils.getParameterString(ent.getValue()));
            }else {
                map.put(key, ent.getValue());
            }
        }
        // log.error("规则化后参数表：" + map.toString());
        // 回写查询变量
        setbackSearchColumn(map, request);

        return map;
    }
    /**
     * 将参数变量转换为查询条件
     * @param request request
     * @return Map 参数变量转换为查询条件
     */
    public static Map<String, Object> collectRequestParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = new HashMap<>();
        //map.put("isValid", "T");
        for (Map.Entry<String, String[]> ent : parameterMap.entrySet()) {
            String[] values = ListOpt.removeBlankString(ent.getValue());
            if(values==null)
                continue;
            if(values.length==1){
                map.put(ent.getKey(), values[0]);
            }else{
                map.put(ent.getKey(), values);
            }
        }
        return map;
    }

    /**
     *
     * @param filterMap Map 过滤条件
     * @param request request
     */
    public static void setbackSearchColumn(Map<String, Object> filterMap, HttpServletRequest request) {
        if (filterMap == null || filterMap.size() < 1) {
            return;
        }
        for (Map.Entry<String, Object> ent : filterMap.entrySet()) {
            Object objValue = ent.getValue();
            String skey;
            if(objValue instanceof String ){
                //和default一样，为了效率优先判断
                skey = SEARCH_STRING_PREFIX + ent.getKey();
            }else if(objValue instanceof String[]){
                skey = SEARCH_ARRAY_PREFIX + ent.getKey();
            }else if(objValue instanceof Long){
                skey = SEARCH_NUMBER_PREFIX + ent.getKey();
            }else if(objValue instanceof Long[]){
                skey = SEARCH_NUMBER_ARRAY_PREFIX + ent.getKey();
            }else{
                skey = SEARCH_STRING_PREFIX + ent.getKey();
            }
            request.setAttribute(skey, objValue);
        }
    }
}
