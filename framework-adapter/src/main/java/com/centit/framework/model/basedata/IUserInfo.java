package com.centit.framework.model.basedata;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Date;

/**
 * Userinfo entity.
 * 系统用户信息
 * @author codefan@sina.com
 */
public interface IUserInfo{
    /**
    * 用户编码，是用户的主键
    * @return 用户编码，是用户的主键
    */
    String getUserCode();

    /**
    * 用户密码的密文， 密码为加盐的散列算法
    * @return 密文
    */
    @JSONField(serialize = false)
    String getUserPin();

    /**
     * 密码有效期时间
     * @return 密码有效期时间
     */
    Date getPwdExpiredTime();

    /**
    * 用户是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
    * @return 用户是否有效 T/F/A  T 正常 ， F 禁用,A为新建可以删除
    */
    String getIsValid();

    /**
    * 用户名称  ，和 getUsername()不同后者返回的是用户登录名称
    * @return 用户名称
    */
    String getUserName();

    /**
    * 用户登录名 同 getUsername
    * @return 用户登录名
    */
    String getLoginName();

    /**
     * 用户默认的租户，每次登录后修改这个属性
     * 如果用户只能属于一个租户，这个值就是他的租户
     * 在不支持租户的系统中这个字段可以有其他的解释
     * @return 用户默认租户
     */
    String getTopUnit();

    /**
    * 用户默认机构（主机构）代码 defautl unit
    * @return 用户默认机构（主机构）代码
    */
    String getPrimaryUnit();
    /**
    * 用户类别，各个业务系统自定义类别信息
    * @return 用户类别，各个业务系统自定义类别信息
    */
    String getUserType();

    /**
    * 用户注册邮箱
    * @return 用户注册邮箱
    */
    String getRegEmail();
    /**
    * 用户注册手机号码
    * @return 用户注册手机号码
    */
    String getRegCellPhone();

    /**
    * 用户排序号
    * @return 用户排序号
    */
    Long getUserOrder();

    /**
     * 获取用户身份证号码;这个方法不是必须的可以直接返回 null
     * @return 用户身份证号码
     */
    String getIdCardNo();

    /**
     * 用户标签
     * @return 用户标签
     */
    String getUserTag();

    /**
     * 用户第三发业务中的主键, 比如：警员号
     * @return 用户第三发业务中的主键
     */
    String getUserWord();

    /**
     * @return 英文名
     */
    String getEnglishName();

    /**
     * @return 用户描述
     */
    String getUserDesc();
}
