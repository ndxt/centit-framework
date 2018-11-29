package com.centit.framework.config;

/**
 * Session H2 持久化
 * 已废弃，目前持久化session方法：
 * 添加 com.centit.framework/framework-session 依赖
 * 在system.properties中添加配置项：
 *      session.persistence.db.type = jdbc
 *      session.jdbc.url = 关系型数据库URL
 *      session.jdbc.username = 数据库用户名
 *      session.jdbc.password = 数据库密码
 */
@Deprecated
public class H2SessionPersistenceConfig {
}
