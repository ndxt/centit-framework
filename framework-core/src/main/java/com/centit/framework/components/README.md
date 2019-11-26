# 公共组件

## 数据字典工具类 CodeRepositoryUtil

这个工具类通过静态方法可以访问框架中所有数据资源，包括：
1. 组织、人员、人员组织关系信息。
2. 操作、权限、角色信息。
3. 所有数据字典信息。
4. 配置参数信息。

## 机构过滤引擎 SysUnitFilterEngine

机构过滤引擎通过机构表达式过滤机构，机构表达式形式如下：

1. 常量：empty （空）| all （所有）| "机构代码" ；empty和all其实意义上是一样的，因为在节点机构中只能填写一个机构代码，如果空就表示所有，在权限表达式中如果要表示所有就要用all
2. 变量：C（当前操作用户所在机构）| P（上个节点机构）| F （流程所在机构）|N（节点所属机构）| L 相同节点上一个实例所在机构 (Last Same Node Instance UnitCode)
3. 数值：层次
4. 操作符：+ （下层机构，层次由后面的数值决定）| - （上层机构）| * （最上层机构的下层机构 ,+- 的层次是相对当前的机构来计算的，* 的层次是相对当前机构的最上层机构来计算的）
5. 简单表达式：变量|常量 | 常量 + 数值|  变量 *|+|- 数值 |  变量 - 数值 + 数值
6. 机构表达式： 简单表达式 | （机构表达式） |  机构表达式 || 机构表达式 | 机构表达式 && 机构表达式 | 机构表达式 ! 机构表达式 | S(机构表达式[,机构表达式]+)

机构表达式的结构是一个机构集合，||、&&、！均为集合运算符，||是取两个集合的并集，&&取两个集合的交集，！是在前一个集合中减去后一个集合。
* S(机构表达式[,机构表达式]+) ，这个表达式表示返回多个表达式中的 第一个结果不为空的集合

表达式示例，示例中的表达式变量，无论是 U、P、F、L假设其值都是下图中的D111，红色框表示的。

![机构示意图](https://github.com/ndxt/ndxt.github.io/blob/master/docs/assets/jigoushiyitu.jpg)

简单表达式就一下10种形式

1. empty=>
2. all => D1,D2,D11,D12,D111,D112,D1111,D1112
3. “D12” =>D12
4. empty+1 =>D1,D2
5. all+1 => D11,D12,D111,D112,D1111,D1112
6. A => D111
7. U+1 => D1111,D1112
8. U-1 => D11
9. P-1+1 => D111,D112
11. W*1 => D1

机构表达式就是 简单表达式的基础上做集合运算。 节点的机构表达式只记录表达式返回的第一个机构，如果表达式返回的是空就记录空。

## 人员过滤引擎 SysUserFilterEngine

权限表达式组成主要由 机构、人员、岗位角色、行政角色或者行政角色等级来决定。

1. 机构表达式：参见上节
2. 人员表达式：("人员代码"| 变量，变量可以 C 当前操作人员即提交节点的人员 F 流程的所属人员 L 相同节点上一个实例所属人员 P 上一个节点操作人员)[, "人员代码"| 变量]*
3. 岗位角色：岗位角色代码[,岗位角色代码] *
4. 行政角色：行政角色代码[,行政角色代码] *
5. 行政角色等级 ： R（等级常量）|  R（变量，变量可以 C 业务的创建人比如申请人等级 O 当前操作人员即提交节点的人员等级）| R（变量+|-等级）; 等级计算，+1，比表示当前等级低一等的最高级，比如，当前等级为3 ，+1范围4，如果没有4 只有5则返回5，-也一样
7. 权限简单表达式：[D/P(机构表达式,[机构表达式,]*)][gw(岗位角色)][xz(行政角色)|R(行政角色等级)] | RO(系统操作角色)| U(人员表达式)
8. 权限表达式 ：权限简单表达式 | S(权限表达式[,权限表达式]+) | （权限表达式） |  权限表达式 || 权限表达式 | 权限表达式 && 权限表达式 | 权限表达式 ! 权限表达式

权限表达式的结构是一个人员集合，||、&&、！均为集合运算符，||是取两个集合的并集，&&取两个集合的交集，！是在前一个集合中减去后一个集合。 

* S(权限表达式[,权限表达式]+)，这个表达式表示返回多个表达式中的 第一个结果不为空的集合。

工作流权限引擎中的机构表达式中有一个特有的变量N，N代表节点的所属机构及引用节点机构引擎的结果。

D/P：D和P对应的都是机构表达式，不同的是D是选择所有的用户P只选择主机构用户，所以一个【权限简单表达式】中只能有一个D或者P，不能同时存在。

## OperationLogCenter

日志记录操作工具类，参见[日志写入操作](https://github.com/ndxt/centit-framework/tree/master/framework-core/src/main/java/com/centit/framework/operationlog)。

## impl/NotificationCenterImpl

通知中心的实现方式和操作日志不同，通知中心是也业务系统在同一个事务中的，所有没有提供工具类，而是通过bean的形式调用。

1. 开发人员通过通知中心调用消息的发送操作，并不关系具体的发送方式。
2. 通知中心可以注册多种发送方式，比如：邮件、短信等等，用户可以设置自己的接收方式，可以设置多种接收方式。
3. 业务系统可以开发自己的消息发送[MessageSender](https://github.com/ndxt/centit-framework/tree/master/framework-adapter/src/main/java/com/centit/framework/model/adapter)接口,并注册到通知中心中。 

通知中心的配置方式如下：

```java
/**
 * Created by codefan on 17-7-6.
 * 需要在配置类中创建这个 Bean 才能是日志生效
 */
public class InstantiationServiceBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>
{

    @Autowired
    protected NotificationCenter notificationCenter;
    
    @Autowired(required = false)
    private MessageSender innerMessageManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if(innerMessageManager!=null) {
            //注入消息发送接口可以是多个
            notificationCenter.registerMessageSender("innerMsg", innerMessageManager);
        }
    }

}

```
