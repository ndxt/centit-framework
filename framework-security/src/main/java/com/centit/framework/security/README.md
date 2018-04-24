# 框架安全机制

框架采用[Spring security](https://docs.spring.io/spring-security/site/docs/current/guides/html5/)安全框架，实行身份验证和权限过滤。

## 身份验证

框架提供两种方式的身份验证：本地认证和基于cas的单点登录认证。

### 本地认证

spring security 提供了多中认证方式，我们一般采用用户名和密码的方式认证。spring 框架中有两种密码认证方式：
1. org.springframework.security.crypto.password.PasswordEncoder

Spring推荐的验证方式BCryptPasswordEncoder就是实现了这个接口，其加密盐是随机的并编码在密码中。我们框架中默认的加密方式StandardPasswordEncoderImpl也是采用的这个加密算法。

2. org.springframework.security.authentication.encoding.PasswordEncoder

这个加密方式是可以自定义加密盐的，CentitPasswordEncoderImpl就实现了这个接口，加密算法和BCryptPasswordEncoder一样，只是添加了自定义盐，我认为这个更好，这样不同的人的密码是不通用的。

本地验证也通过SpringSecurityDaoConfig类来配置。

### 单点登录

centit-cas在[apereo cas](https://www.apereo.org/projects/cas)的基础上开发了一套验证界面和验证方式的插件。可以无缝的和框架对接。

通过SpringSecurityCasConfig类来配置。

## 权限过滤

框架的功能权限模型参见[权限体系](https://ndxt.github.io/system_design/concept_design.html#%E6%9D%83%E9%99%90%E4%BD%93%E7%B3%BB)。


功能权限通过[Spring security](https://docs.spring.io/spring-security/site/docs/current/guides/html5/)的过滤器实现，过滤器将请求url映射到optCode，通过角色信息查找用于这个optCode所有角色，然后对比当前用户是否具有其中的一个，如果有就通过，否则提示401。

1. DaoInvocationSecurityMetadataSource 复制将url映射到业务操作，并查找对应的角色集合。
2. DaoFilterSecurityInterceptor 复制过滤器的执行，在执行获取用户的session。
3. DaoAccessDecisionManager 判断用户是否有权限访问资源。