package com.centit.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.centit.framework.core.common.ObjectException;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
/**
 * 常用验证工具，适用于控制器及业务层中判断主键或关键对象且需要中断执行流程
 */
public class ValidatorUtils {
	
	private static Validator validator = null;
	
	private static synchronized Validator getDefaultValidator(){
		if(validator==null){
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();    
	        validator = factory.getValidator(); 
		}
		return validator;		
	}
    /**
     * 判断空对象
     *
     * @param object    任意对象
     * @param errorText 返回错误信息
     */
    public static void validatorNullObject(Object object, String errorText) {
        if (null == object) {
            throw new ObjectException(ObjectException.NULL_EXCEPTION,errorText);
        }
    }

    /**
     * 判断空白字符串
     *
     * @param str       字符串
     * @param errorText 返回错误信息
     */
    public static void validatorBlank(String str, String errorText) {
        if (StringUtils.isBlank(str)) {
            throw new ObjectException(ObjectException.BLANK_EXCEPTION,errorText);
        }
    }
    
    /**
     * 调用Hibernate注解进行验证
     * @param po
     * @return
     */
    public static <Po>  Map<String,String> validatorEntityPo(Po po) {    	
        Validator validator = getDefaultValidator(); 
        
        Set<ConstraintViolation<Po>> constraintViolations = validator.validate(po); 
        Map<String,String> errMsg = new HashMap<String,String>();
       	for (ConstraintViolation<Po> constraintViolation : constraintViolations) {    
       		errMsg.put(String.valueOf(constraintViolation.getPropertyPath()),
       				constraintViolation.getMessage());    
        }
       	return errMsg;
    }
    
}
