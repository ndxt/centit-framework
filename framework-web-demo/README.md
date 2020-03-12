## 框架使用示例

framework-web-demo模块是使用框架的最简示例。基于框架开发的项目可以直接复制demo中代码到自己的项目中。这个demo代码包括一下内容：

## 包依赖说明

1. framework-system-static 框架接口的最简单额实现
2. framework-core-web 框架的web服务接口，包括当前用户信息、技术数据信息的获取接口
3. framework-config 相关配置信息，这些文件独立为一个包是因为在spring boot项目中不需要这些包
4. framework-system-static-config 相关配置信息，同上
5. Swagger 相关的依赖包

## 配置类说明

1. InstantiationServiceBeanPostProcessor 初始化需要做的事情
2. NormalSpringMvcConfig 业务servlet配置信息
3. ServiceConfig  系统需要的bean，在这儿可以替换框架提供的服务的实现方式
4. WebInitializer 配置类，用来代替Web.xml 

## 配置文件说明

system.properties 开发人员需要配置的信息基本都在这儿类似于spring的application.yml
