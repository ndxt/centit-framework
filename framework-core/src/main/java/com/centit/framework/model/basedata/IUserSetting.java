package com.centit.framework.model.basedata;

/**
 * create by scaffold
 *
 * @author codefan@hotmail.com
 */
public interface IUserSetting {
	/**
	 * 用户代码（用户主键）
	 * @return
	 */
	public String getUserCode();
	/**
	 * 参数代码（参数主键）
	 * @return
	 */
	public String getParamCode();
	/**
	 * 参数值
	 * @return
	 */
	public String getParamValue();
	/**
	 * 参数对应的业务Id，system 表示系统参数
	 * @return
	 */
	public String getOptId();
	/**
	 * 参数名称或者参数说明，可选
	 * @return
	 */
	public String getParamName();

}
