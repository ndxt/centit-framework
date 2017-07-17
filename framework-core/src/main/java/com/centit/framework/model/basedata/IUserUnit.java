package com.centit.framework.model.basedata;

/**
 * FUserunit entity.
 * 用户机构关联关系
 * @author MyEclipse Persistence Tools
 */
public interface IUserUnit{
	/**
	 * 关联关系主键
	 * @return
	 */
	public String getUserUnitId();
	/**
	 * 用户编码，是用户的主键
	 * @return
	 */
    public String getUserCode();
    /**
	 * 机构代码 是机构的主键
	 * @return
	 */
	public String getUnitCode();
	/**
     * 是否为主机构 T:主机构 F：辅机构
     * @return
     */
    public String getIsPrimary();
    /**
     * 用户在本机构的岗位
     * @return
     */
    public String getUserStation();
    /**
     * 用户在本机构的行政职务
     * @return
     */
    public String getUserRank();
}
