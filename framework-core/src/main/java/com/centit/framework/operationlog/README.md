# 日志写入服务

框架提供两种方式记录操作日志。

## 注解方式 RecordOperationLog

通过在controller类的方法上添加 RecordOperationLog 注解记录日志。
1. 注解中的 content 是一个日志内容模板。模板中可以用{}嵌入方法的参数或者参数的属性。第一个参数为arg0，第二个为org1以此类推。
2. timing 表示是否记录方法的执行时间。
3. appendRequest 表示是否要把request中的参数记录到日志中。

## 直接调用 OperationLogCenter

这个工具类可以直接调用日志记录接口写入用户自定义的日志信息。参见[日志写入接口](https://github.com/ndxt/centit-framework/tree/master/framework-adapter/src/main/java/com/centit/framework/model/adapter)。

## 日志记录接口注入

```java
/**
 * Created by codefan on 17-7-6.
 * 需要在配置类中创建这个 Bean 才能是日志生效
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>
{

    @Autowired
    private OperationLogWriter optLogManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event){
        if(optLogManager!=null) {
            OperationLogCenter.registerOperationLogWriter(optLogManager);
        }
        //......
    }

}

```

## 其他注意事项

1. 操作日志记录只负责日志的写入操作，不负责日志的查询和统计工作。
2. 框架提供了两个默认的实现方式 TextOperationLogWriterImpl 和 Log4jOperationLogWriterImpl 。
3. [centit-framework-system](https://github.com/ndxt/centit-framework-system)中实现了用关系数据库记录操作日志的工作，并实现日志的统计和查询。但需注意虽然日志写入操作和业务的数据库读写操作不在一个事务中，但是当日志量很大的情况下还是不建议使用这种方式记录日志。

