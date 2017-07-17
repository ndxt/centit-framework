package com.centit.framework.hibernate.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.DatePropertiesEditor;
import com.centit.framework.core.controller.SqlDatePropertiesEditor;
import com.centit.framework.core.controller.SqlTimestampPropertiesEditor;
import com.centit.framework.core.controller.StringPropertiesEditor;
import com.centit.framework.hibernate.common.RichText;

/**
 * @author codefan@sina.com
 * @create 2016年10月12日
 */

@Controller
public class RichTextBaseController extends BaseController {
    
	@Override
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
    	binder.registerCustomEditor(RichText.class, new RichTextPropertiesEditor());
        binder.registerCustomEditor(String.class, new StringPropertiesEditor(true));
        binder.registerCustomEditor(Date.class, new DatePropertiesEditor());
        binder.registerCustomEditor(java.sql.Date.class, new SqlDatePropertiesEditor());
        binder.registerCustomEditor(java.sql.Timestamp.class, new SqlTimestampPropertiesEditor());
    }
}
