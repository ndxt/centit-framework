package com.centit.framework.common;

import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.centit.support.algorithm.CollectionsOpt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * 响应 http 请求 返回的数据，可以用Map返回多个数据
 * 作为 发送数据使用
 */
@ApiModel(description = "返回的json数据格式")
public interface ResponseData{
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
    int RESULT_OK=0;
    /**
     * 101 用户登录失败
     */
    int ERROR_USER_LOGIN_ERROR=101;

    int HTTP_CONTINUE=100;
    int HTTP_SWITCHING_PROTOCOLS=101;
    int HTTP_PROCESSING=102;

    int HTTP_OK=200;
    int HTTP_CREATED=201;
    int HTTP_ACCEPTED=202;
    int HTTP_NON_AUTHORITATIVE_INFORMATION=203;
    int HTTP_NO_CONTENT=204;
    int HTTP_RESET_CONTENT=205;
    int HTTP_PARTIAL_CONTENT=206;

    int HTTP_MULTIPLE_CHOICES=300;
    int HTTP_MOVED_PERMANENTLY=301;
    int HTTP_MOVE_TEMPORARILY=302;
    /**
     * 用户未登录引起多次302
     * 302   （临时移动）  服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。
     */
    int ERROR_USER_NOT_LOGIN=302;
    int HTTP_SEE_OTHER=303;
    int HTTP_NOT_MODIFIED=304;
    int HTTP_USE_PROXY=305;
    int HTTP_SWITCH_PROXY=306;
    int HTTP_TEMPORARY_REDIRECT=307;


    int HTTP_BAD_REQUEST=400;
    /**
     * 400 （错误请求） 服务器不理解请求的语法。
     */
    int ERROR_BAD_REQUEST=400;

    int HTTP_UNAUTHORIZED=401;
    /**
     * 401 （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
     * 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。
        2、请求参数有误。
     */
    int ERROR_UNAUTHORIZED=401;

    int HTTP_FORBIDDEN=403;
    /**
     * 403   （禁止） 服务器拒绝请求。
     */
    int ERROR_FORBIDDEN=403;
    int HTTP_NOT_FOUND=404;
    /**
     * 404   （未找到） 服务器找不到请求的网页。
     */
    int ERROR_NOT_FOUND=404;
    int HTTP_METHOD_NOT_ALLOWED=405;
    int HTTP_NOT_ACCEPTABLE=406;

    /**
     *406   （不接受） 无法使用请求的内容特性响应请求的网页。
     *XSS 攻击时也返回对应的内容
    */
    int ERROR_NOT_ACCEPTABLE=406;
    int HTTP_PROXY_AUTHENTICATION_REQUIRED=407;
    int HTTP_REQUEST_TIMEOUT=408;
    int HTTP_CONFLICT=409;
    int HTTP_GONE=410;
    int HTTP_LENGTH_REQUIRED=411;
    int HTTP_PRECONDITION_FAILED=412;
    int HTTP_REQUEST_ENTITY_TOO_LARGE=413;
    int HTTP_REQUEST_URI_TOO_LONG=414;
    int HTTP_UNSUPPORTED_MEDIA_TYPE=415;
    int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE=416;
    int HTTP_EXPECTATION_FAILED=417;
    int HTTP_UNPROCESSABLE_ENTITY=422;
    int HTTP_LOCKED=423;
    int HTTP_FAILED_DEPENDENCY=424;
    int HTTP_UNORDERED_COLLECTION=425;
    int HTTP_UPGRADE_REQUIRED=426;
    int HTTP_RETRY_WITH=449;

    int HTTP_INTERNAL_SERVER_ERROR=500;
    /**
     * 500   （服务器内部错误）  服务器遇到错误，无法完成请求。
     */
    int ERROR_INTERNAL_SERVER_ERROR=500;


    int HTTP_NOT_IMPLEMENTED=501;
    int HTTP_BAD_GATEWAY=502;
    int HTTP_SERVICE_UNAVAILABLE=503;
    int HTTP_GATEWAY_TIMEOUT=504;
    int HTTP_HTTP_VERSION_NOT_SUPPORTED=505;
    int HTTP_VARIANT_ALSO_NEGOTIATES=506;
    int HTTP_INSUFFICIENT_STORAGE=507;
    int HTTP_BANDWIDTH_LIMIT_EXCEEDED=509;
    int HTTP_NOT_EXTENDED=510;

    int HTTP_UNPARSEABLE_RESPONSE_HEADERS=600;

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
    int ERROR_PROCESS_ERROR=700;
    /**
     * 701  输入格式验证错误
     */
    int ERROR_FIELD_INPUT_NOT_VALID=701;
    /**
     * 702  输入内容冲突
     */
    int ERROR_FIELD_INPUT_CONFLICT=702;
    /**
     * 703  程序执行先前条件验证失败
     */
    int ERROR_PRECONDITION_FAILED=703;
    /**
     * 704  程序执行异常错误
     */
    int ERROR_PROCESS_FAILED=704;
    /**
     * 705  程序执行后置条件验证失败
     */
    int ERROR_POSTCONDITION_FAILED=705;
    /**
     * 706  用户没有执行程序的权限
     */
    int ERROR_BAD_PROCESS_POWER=706;
    /**
     * 707  用户没有操作对应的数据权限
     */
    int ERROR_BAD_PROCESS_DATASCOPE=707;

    /**
     * 708 方法已禁用
     */
    int ERROR_METHOD_DISABLED=708;
    /**
     * 709 session 超时
     */
    int ERROR_SESSION_TIMEOUT=709;

    /**
     * 710 用户找不到
     */
    int ERROR_USER_NOTFOUND=710;
    /**
     * 711 用户配置错误
     */
    int ERROR_USER_CONFIG=711;
    /**
     * 712 系统配置错误
     */
    int ERROR_SYSTEM_CONFIG=712;
    /**
     *  8xx 业务系统自定义错误
     */
    int ERROR_OPERATION=800;

    String RES_CODE_FILED="code";
    String RES_MSG_FILED="message";
    String RES_DATA_FILED="data";

    @ApiModelProperty(value = "结果编码，0：成功，其他：错误编码。")
    int getCode();

    @ApiModelProperty(value = "code=0：消息：code!=0:错误信息。")
    String getMessage();

    @ApiModelProperty(value = "JSON格式的数据内容，根据业务的需要定义;前端可以通过在线调试查看详细信息。")
    Object getData();

    String toJSONString(PropertyPreFilter simplePropertyPreFilter);

    default String toJSONString(){
        return toJSONString(null);
    }

    static ResponseData makeSuccessResponse(){
        return new ResponseSingleData();
    }

    static ResponseData makeErrorMessage(String message){
        return new ResponseSingleData(ERROR_INTERNAL_SERVER_ERROR, message);
    }

    static ResponseData makeErrorMessage(int errorCode, String message){
        return new ResponseSingleData(errorCode, message);
    }

    static ResponseData makeErrorMessageWithData(Object obj, int errorCode, String message){
        ResponseSingleData response = new ResponseSingleData(errorCode, message);
        response.setData(obj);
        return response;
    }

    static ResponseSingleData makeResponseData(Object objValue){
        return ResponseSingleData.makeResponseData(objValue);
    }

    static ResponseMapData makeResponseData(String key, Object objValue){
        return ResponseMapData.makeResponseData(CollectionsOpt.createHashMap(key, objValue));
    }

    static ResponseMapData makeResponseData(Map<String, Object> resMapData){
        return ResponseMapData.makeResponseData(resMapData);
    }
}
