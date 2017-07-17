package com.centit.framework.core.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

public class ResponseData {
    /**
     * HTTP协议状态码详解（HTTP Status Code）

	使用ASP.NET/PHP/JSP 或者javascript都会用到http的不同状态，一些常见的状态码为： 
	200 – 服务器成功返回网页 404 – 请求的网页不存在 503 – 服务不可用 
	1xx（临时响应） 
	表示临时响应并需要请求者继续执行操作的状态代码。
	
	代码   说明 
	100   （继续） 请求者应当继续提出请求。 服务器返回此代码表示已收到请求的第一部分，正在等待其余部分。  
	101   （切换协议） 请求者已要求服务器切换协议，服务器已确认并准备切换。
	
	2xx （成功） 
	表示成功处理了请求的状态代码。
	
	代码   说明 
	200   （成功）  服务器已成功处理了请求。 通常，这表示服务器提供了请求的网页。 
	201   （已创建）  请求成功并且服务器创建了新的资源。 
	202   （已接受）  服务器已接受请求，但尚未处理。 
	203   （非授权信息）  服务器已成功处理了请求，但返回的信息可能来自另一来源。 
	204   （无内容）  服务器成功处理了请求，但没有返回任何内容。 
	205   （重置内容） 服务器成功处理了请求，但没有返回任何内容。 
	206   （部分内容）  服务器成功处理了部分 GET 请求。
	
	3xx （重定向） 
	表示要完成请求，需要进一步操作。 通常，这些状态代码用来重定向。
	
	代码   说明 
	300   （多种选择）  针对请求，服务器可执行多种操作。 服务器可根据请求者 (user agent) 选择一项操作，或提供操作列表供请求者选择。 
	301   （永久移动）  请求的网页已永久移动到新位置。 服务器返回此响应（对 GET 或 HEAD 请求的响应）时，会自动将请求者转到新位置。 
	302   （临时移动）  服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。 
	303   （查看其他位置） 请求者应当对不同的位置使用单独的 GET 请求来检索响应时，服务器返回此代码。 
	304   （未修改） 自从上次请求后，请求的网页未修改过。 服务器返回此响应时，不会返回网页内容。 
	305   （使用代理） 请求者只能使用代理访问请求的网页。 如果服务器返回此响应，还表示请求者应使用代理。 
	307   （临时重定向）  服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。
	
	4xx（请求错误） 
	这些状态代码表示请求可能出错，妨碍了服务器的处理。
	
	代码   说明 
	400   （错误请求） 服务器不理解请求的语法。 
	401   （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。 
	403   （禁止） 服务器拒绝请求。 
	404   （未找到） 服务器找不到请求的网页。 
	405   （方法禁用） 禁用请求中指定的方法。 
	406   （不接受） 无法使用请求的内容特性响应请求的网页。 
	407   （需要代理授权） 此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。 
	408   （请求超时）  服务器等候请求时发生超时。 
	409   （冲突）  服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。 
	410   （已删除）  如果请求的资源已永久删除，服务器就会返回此响应。 
	411   （需要有效长度） 服务器不接受不含有效内容长度标头字段的请求。 
	412   （未满足前提条件） 服务器未满足请求者在请求中设置的其中一个前提条件。 
	413   （请求实体过大） 服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。 
	414   （请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。 
	415   （不支持的媒体类型） 请求的格式不受请求页面的支持。 
	416   （请求范围不符合要求） 如果页面无法提供请求的范围，则服务器会返回此状态代码。 
	417   （未满足期望值） 服务器未满足”期望”请求标头字段的要求。
	428 Precondition Required (要求先决条件)
	429 Too Many Requests (太多请求)
	431 Request Header Fields Too Large (请求头字段太大)
	
	5xx（服务器错误） 
	这些状态代码表示服务器在尝试处理请求时发生内部错误。 这些错误可能是服务器本身的错误，而不是请求出错。
	
	代码   说明 
	500   （服务器内部错误）  服务器遇到错误，无法完成请求。 
	501   （尚未实施） 服务器不具备完成请求的功能。 例如，服务器无法识别请求方法时可能会返回此代码。 
	502   （错误网关） 服务器作为网关或代理，从上游服务器收到无效响应。 
	503   （服务不可用） 服务器目前无法使用（由于超载或停机维护）。 通常，这只是暂时状态。 
	504   （网关超时）  服务器作为网关或代理，但是没有及时从上游服务器收到请求。 
	505   （HTTP 版本不受支持） 服务器不支持请求中所用的 HTTP 协议版本。
	511 Network Authentication Required (要求网络认证)
     */
	/**
	 * 0 成功
	 */
	public static final int RESULT_OK=0;
	/**
	 * 101 用户登录失败
	 */
	public static final int ERROR_USER_LOGIN_ERROR=101;
	
	public static final int HTTP_CONTINUE=100;
	public static final int HTTP_SWITCHING_PROTOCOLS=101;
	public static final int HTTP_PROCESSING=102;
	
	public static final int HTTP_OK=200;
	public static final int HTTP_CREATED=201;
	public static final int HTTP_ACCEPTED=202;
	public static final int HTTP_NON_AUTHORITATIVE_INFORMATION=203;
	public static final int HTTP_NO_CONTENT=204;
	public static final int HTTP_RESET_CONTENT=205;
	public static final int HTTP_PARTIAL_CONTENT=206;	
	
	public static final int HTTP_MULTIPLE_CHOICES=300;
	public static final int HTTP_MOVED_PERMANENTLY=301;
	public static final int HTTP_MOVE_TEMPORARILY=302;
	/**
	 * 用户未登录引起多次302
	 * 302   （临时移动）  服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。 
	 */
	public static final int ERROR_USER_NOT_LOGIN=302;
	public static final int HTTP_SEE_OTHER=303;
	public static final int HTTP_NOT_MODIFIED=304;
	public static final int HTTP_USE_PROXY=305;
	public static final int HTTP_SWITCH_PROXY=306;
	public static final int HTTP_TEMPORARY_REDIRECT=307;
	
	
	public static final int HTTP_BAD_REQUEST=400;
	/**
	 * 400 （错误请求） 服务器不理解请求的语法。 
	 */
	public static final int ERROR_BAD_REQUEST=400;
	
	public static final int HTTP_UNAUTHORIZED=401;
	/**
	 * 401 （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。 
	 * 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。
		2、请求参数有误。
	 */
	public static final int ERROR_UNAUTHORIZED=401;
	
	public static final int HTTP_FORBIDDEN=403;
	/**
	 * 403   （禁止） 服务器拒绝请求。 
	 */
	public static final int ERROR_FORBIDDEN=403;
	public static final int HTTP_NOT_FOUND=404;
	/**
	 * 404   （未找到） 服务器找不到请求的网页。 
	 */
	public static final int ERROR_NOT_FOUND=404;
	public static final int HTTP_METHOD_NOT_ALLOWED=405;
	public static final int HTTP_NOT_ACCEPTABLE=406;
	
	/**
	 *406   （不接受） 无法使用请求的内容特性响应请求的网页。 
	 *XSS 攻击时也返回对应的内容
	*/
	public static final int ERROR_NOT_ACCEPTABLE=406;
	public static final int HTTP_PROXY_AUTHENTICATION_REQUIRED=407;
	public static final int HTTP_REQUEST_TIMEOUT=408;
	public static final int HTTP_CONFLICT=409;
	public static final int HTTP_GONE=410;
	public static final int HTTP_LENGTH_REQUIRED=411;
	public static final int HTTP_PRECONDITION_FAILED=412;
	public static final int HTTP_REQUEST_ENTITY_TOO_LARGE=413;
	public static final int HTTP_REQUEST_URI_TOO_LONG=414;
	public static final int HTTP_UNSUPPORTED_MEDIA_TYPE=415;
	public static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE=416;
	public static final int HTTP_EXPECTATION_FAILED=417;
	public static final int HTTP_UNPROCESSABLE_ENTITY=422;
	public static final int HTTP_LOCKED=423;
	public static final int HTTP_FAILED_DEPENDENCY=424;
	public static final int HTTP_UNORDERED_COLLECTION=425;
	public static final int HTTP_UPGRADE_REQUIRED=426;
	public static final int HTTP_RETRY_WITH=449;
		
	public static final int HTTP_INTERNAL_SERVER_ERROR=500;
	/**
	 * 500   （服务器内部错误）  服务器遇到错误，无法完成请求。 
	 */
	public static final int ERROR_INTERNAL_SERVER_ERROR=500;
	
	
	public static final int HTTP_NOT_IMPLEMENTED=501;
	public static final int HTTP_BAD_GATEWAY=502;
	public static final int HTTP_SERVICE_UNAVAILABLE=503;
	public static final int HTTP_GATEWAY_TIMEOUT=504;
	public static final int HTTP_HTTP_VERSION_NOT_SUPPORTED=505;
	public static final int HTTP_VARIANT_ALSO_NEGOTIATES=506;
	public static final int HTTP_INSUFFICIENT_STORAGE=507;
	public static final int HTTP_BANDWIDTH_LIMIT_EXCEEDED=509;
	public static final int HTTP_NOT_EXTENDED=510;
	
	public static final int HTTP_UNPARSEABLE_RESPONSE_HEADERS=600;

	/**
	 * 7xx 后台程序错误
	 * 700  后台程序位置错误 或者 类型为细分错误
	 * 701  输入格式验证错误
	 * 702  输入内容冲突
	 * 703  程序执行先前条件验证失败
	 * 704  程序执行异常错误
	 * 705  程序执行后置条件验证失败
	 * 706  用户没有执行程序的权限
	 * 707  用户没有操作对应的数据权限
	 * 708  方法已禁用
	 * 709 session 超时
	 * 8xx 业务系统自定义错误
	 */
	/**
	 * 700  后台程序位置错误 或者 类型为细分错误
	 */
	public static final int ERROR_PROCESS_ERROR=700;
	/**
	 * 701  输入格式验证错误
	 */
	public static final int ERROR_FIELD_INPUT_NOT_VALID=701;	
	/**
	 * 702  输入内容冲突
	 */
	public static final int ERROR_FIELD_INPUT_CONFLICT=702;
	/**
	 * 703  程序执行先前条件验证失败
	 */
	public static final int ERROR_PRECONDITION_FAILED=703;
	/**
	 * 704  程序执行异常错误
	 */
	public static final int ERROR_PROCESS_FAILED=704;
	/**
	 * 705  程序执行后置条件验证失败
	 */
	public static final int ERROR_POSTCONDITION_FAILED=705;
	/**
	 * 706  用户没有执行程序的权限
	 */
	public static final int ERROR_BAD_PROCESS_POWER_=706;
	/**
	 * 707  用户没有操作对应的数据权限
	 */
	public static final int ERROR_BAD_PROCESS_DATASCOPE=707;
	
	/**
	 * 708 方法已禁用
	 */
	public static final int ERROR_METHOD_DISABLED=708;
	
	public static final int ERROR_SESSION_TIMEOUT=709;
	/**
	 *  8xx 业务系统自定义错误
	 */
	public static final int ERROR_OPERATION=800;
			
    public static final String RES_CODE_FILED="code";
    
    public static final String RES_MSG_FILED="message";
    public static final String RES_DATA_FILED="data";
    
    /**
     * 返回代码，0 表示正确，其他的为错误代码
     */
    private int resCode;

    /**
     * 返回消息提示 ，code为0时是提示，其他为 错误提示
     */
    private String resMessage;

    /**
     * 返回的详细数据， 可能是需要回显的参数，也可能是验证的错误提示
     */
    private Map<String, Object> resMapData;

    public ResponseData() {
        resCode = 0;
        resMessage = "OK";
        resMapData = new LinkedHashMap<String, Object>();
    }

    public ResponseData(int nCode) {
        resCode = nCode;
        resMessage = nCode==0?"OK":"ERROR";
        resMapData = new LinkedHashMap<String, Object>();
    }

    public ResponseData(String message) {
        resCode = 0;
        resMessage = message;
        resMapData = new LinkedHashMap<String, Object>();
    }
    
    public ResponseData(int nCode, String message) {
        resCode = nCode;
        resMessage = message;
        resMapData = new LinkedHashMap<String, Object>();
    }

    public int getCode() {
        return resCode;
    }

    public void setCode(int resCode) {
        this.resCode = resCode;
    }

	public String getMessage() {
		return resMessage;
	}

    /*public String getResponseMessage() {
        return resMessage;
    }
	*/
	public void setMessage(String resMessage) {
		this.resMessage = resMessage;
	}

    public void setResponseMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public Map<String, Object> getData() {
        return resMapData;
    }

    public void addResponseData(String sKey, Object objValue) {
        resMapData.put(sKey, objValue);
    }
    
    public Object getResponseData(String sKey) {
        return resMapData.get(sKey);
    }

    public String toJSONString(PropertyPreFilter simplePropertyPreFilter){
        Map<String, Object> param = new HashMap<>();
        param.put(RES_CODE_FILED, resCode );
        param.put(RES_MSG_FILED,  resMessage );
        if(resMapData!=null)
            param.put(RES_DATA_FILED, resMapData);
		if(simplePropertyPreFilter==null)
			return JSONObject.toJSONString(param);
		return JSONObject.toJSONString(param,simplePropertyPreFilter);
    }

	public String toJSONString(){
		return toJSONString(null);
	}

	@Override
	public String toString(){
		return toJSONString();
	}
}
