# 框架基础平台
  
  框架基于前后端分离的理念开发，采用Spring作为后端开发框架，EasyUI、Vue作为前段框架。详情参见[框架技术路线](https://ndxt.github.io/system_design/technical_design.html)。

## 框架各模块介绍

1. framework-adapter 将框提供的架基础服务抽象为接口，便于业务开发特定的实现，同时定义前后端数据交换格式。参见[framework-adapter/common](./tree/master/framework-adapter/src/main/java/com/centit/framework/common)。
2. framework-core 框架的核心组件，包括工具类，用户、机构、权限等模型的数据抽象。平台运行依赖的数据接口（PlatformEnvironment）抽象等等。
3. framework-security 框架基于spring security实现安全框架，这个类将spring security与平台数据模型整合。
4. framework-filter 一组通用的过滤器，业务系统可以选择使用。
5. framework-config 框架的配置类。进一步了解参见[启动与配置空间](https://ndxt.github.io/system_design/product_design.html#%E5%90%AF%E5%8A%A8%E4%B8%8E%E9%85%8D%E7%BD%AE%E7%A9%BA%E9%97%B4)。
6. framework-core-web 框架提供的基础服务，如：登录、数据字典服务等等。
7. framework-system-static 框架平台接口PlatformEnvironment一个基于json数据的静态（不能修改）实现。
8. framework-system-static-jdbc 框架平台接口PlatformEnvironment一个基于jdbc数据的静态实现。
9. framework-system-static-config 框架静态bean配置类。
10. framework-web-demo 一个使用框架的示例。

## 快速入门

framework-web-demo模块是使用框架的最简示例。基于框架开发的项目可以直接复制demo中代码到自己的项目中。这个demo代码包括一下内容：

1. pom.xml 框架依赖的包。
2. 配置内在config包中。由于框架采用的spring 4.x 所以框架web项目没有web.xml配置信息，全部采用配置类的方式配置。
3. resources中的 system.properties 配置信息。
4. webapp中的jsp页面。

webapp这部分其实不是必须的，根据框架前后端分离设计原则也是不需要的，只是为了快速启动而设置的跳转页面。同样在pom.xml中的framework-base-view-easyui依赖包也是不需要的，这个包是前段代码，应该解压出来放在静态服务器上比如：nginx。