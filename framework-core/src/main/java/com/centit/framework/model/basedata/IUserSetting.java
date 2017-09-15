package com.centit.framework.model.basedata;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
public interface IUserSetting {
    /**
     * 用户代码（用户主键）
     * @return 用户代码（用户主键）
     */
     String getUserCode();
    /**
     * 参数代码（参数主键）
     * @return 参数代码（参数主键）
     */
     String getParamCode();
    /**
     * 参数值
     * @return 参数值
     */
     String getParamValue();
    /**
     * 参数对应的业务Id，system 表示系统参数
     * @return 参数对应的业务Id，system 表示系统参数
     */
     String getOptId();
    /**
     * 参数名称或者参数说明，可选
     * @return 参数名称或者参数说明，可选
     */
     String getParamName();

}
