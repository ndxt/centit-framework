# 框架基础服务接口

框架基础服务通过接口和实现分离的方式，将开发的使用和事项分离，开发可以面向接口编程。

adapter 包的目的就是将接口抽象出来，业务或者其他服务可以针对这些接口编写自己的实现然后通过配置文件注入到系统。

## 消息通知接口

1. MessageSender实现了消息发送
2. NotificationCenter 通知中心接口，它根据用户设定的接受消息的方式向用户发送通知。

## 操作日志接口

1. OperationLog 定义了日志的记录内容。
2. OperationLogWriter 定义日志写入接口。

日志的具体写入操作参见[日志写入操作](https://github.com/ndxt/centit-framework/tree/master/framework-core/src/main/java/com/centit/framework/operationlog)。

## 接口的实现

[代码components/impl](https://github.com/ndxt/centit-framework/tree/master/framework-core/src/main/java/com/centit/framework/components/impl)中提供了接口多种默认实现。