package com.centit.framework.security;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.common.WebOptUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAccessDeniedHandlerImpl implements AccessDeniedHandler {
    // ~ Static fields/initializers
    // =====================================================================================

    protected static final Log logger = LogFactory.getLog(AjaxAccessDeniedHandlerImpl.class);

    // ~ Instance fields
    // ================================================================================================

    private String errorPage;

    // ~ Methods
    // ========================================================================================================

    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException,
        ServletException {
        if(WebOptUtils.isAjax(request)){
            if(!WebOptUtils.exceptionNotAsHttpError){
                JsonResultUtils.writeHttpErrorMessage(ResponseData.ERROR_UNAUTHORIZED,
                    "无权限访问！", response);
            }else {
                ResponseSingleData responseData =
                    new ResponseSingleData(ResponseData.ERROR_UNAUTHORIZED,
                        "无权限访问！");
                responseData.setData("无权限访问！");
                JsonResultUtils.writeResponseDataAsJson(responseData, response);
            }
        } else {
            if (!response.isCommitted()) {
                if (errorPage != null) {
                    // Put exception into request scope (perhaps of use to a view)
                    request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                        accessDeniedException);

                    // Set the 403 status code.
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                    // forward to error page.
                    RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
                    dispatcher.forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        accessDeniedException.getMessage());
                }
            }
        }
    }

    /**
     * The error page to use. Must begin with a "/" and is interpreted relative to the
     * current context root.
     *
     * @param errorPage the dispatcher path to display
     *
     * @throws IllegalArgumentException if the argument doesn't comply with the above
     * limitations
     */
    public void setErrorPage(String errorPage) {
        if ((errorPage != null) && !errorPage.startsWith("/")) {
            throw new IllegalArgumentException("errorPage must begin with '/'");
        }

        this.errorPage = errorPage;
    }
}

