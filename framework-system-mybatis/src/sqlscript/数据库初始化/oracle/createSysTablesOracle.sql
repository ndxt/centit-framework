/*==============================================================*/
/* DBMS name:      ORACLE Version 10gR2                         */
/* Created on:     2016/4/19 17:51:25                           */
/*==============================================================*/


drop table F_ADDRESS_BOOK cascade constraints;

drop table F_DATACATALOG cascade constraints;

drop table F_DATADICTIONARY cascade constraints;

drop table F_OPTDATASCOPE cascade constraints;

drop index IND_OptID_OPTMETHOD;

drop table F_OPTDEF cascade constraints;

drop index ind_Tag_ID;

drop table F_OPT_LOG cascade constraints;

drop table F_OptFlowNoInfo cascade constraints;

drop table F_OptFlowNoPool cascade constraints;

drop table F_OptInfo cascade constraints;

drop table F_OptInfoData cascade constraints;

drop index Ind_Filter_Table_Class_Name;

drop table F_QUERY_FILTER_CONDITION cascade constraints;

drop table F_RANKGRANT cascade constraints;

drop table F_ROLEINFO cascade constraints;

drop table F_ROLEPOWER cascade constraints;

drop table F_STAT_MONTH cascade constraints;

drop table F_SYS_NOTIFY cascade constraints;

drop table F_UNITINFO cascade constraints;

drop index ind_regemail;

drop index Ind_loginname;

drop table F_USERINFO cascade constraints;

drop table F_USERROLE cascade constraints;

drop table F_USERSETTING cascade constraints;

drop table F_USERUNIT cascade constraints;

drop table F_USER_FAVORITE cascade constraints;

drop index Ind_query_filter_modle_code;

drop table F_USER_QUERY_FILTER cascade constraints;

drop table F_WORK_CLASS cascade constraints;

drop table F_WORK_DAY cascade constraints;

drop table M_InnerMsg cascade constraints;

drop table M_InnerMsg_Recipient cascade constraints;

drop table M_MsgAnnex cascade constraints;

drop table P_TASK_LIST cascade constraints;

/*==============================================================*/
/* 创建 序列                                                                                                                                                                     */
/*==============================================================*/

create sequence S_Filter_No;

create sequence s_notify_id;

create sequence s_optdefcode
start with 1100000;

create sequence s_sys_log;

create sequence s_unitcode;

create sequence s_user_unit_id;

create sequence s_usercode;

/*==============================================================*/
/* Table: F_ADDRESS_BOOK                                        */
/*==============================================================*/
create table F_ADDRESS_BOOK  (
   ADDRBOOKID           NUMBER(10,0)                    not null,
   BodyType             VARCHAR2(2)                     not null,
   BodyCode             VARCHAR2(16)                    not null,
   representation       VARCHAR2(200),
   UnitName             VARCHAR2(200),
   DeptName             VARCHAR2(100),
   RankName             VARCHAR2(50),
   Email                VARCHAR2(60),
   Email2               VARCHAR2(60),
   Email3               VARCHAR2(60),
   HomePage             VARCHAR2(100),
   QQ                   VARCHAR2(20),
   MSN                  VARCHAR2(60),
   wangwang             VARCHAR2(20),
   buzPhone             VARCHAR2(20),
   buzphone2            VARCHAR2(20),
   buzfax               VARCHAR2(20),
   assiphone            VARCHAR2(20),
   callbacphone         VARCHAR2(20),
   carphone             VARCHAR2(20),
   unitphone            VARCHAR2(20),
   homephone            VARCHAR2(20),
   homephone2           VARCHAR2(20),
   homephone3           VARCHAR2(20),
   homefax              VARCHAR2(20),
   mobilephone          VARCHAR2(20),
   mobilephone2         VARCHAR2(20),
   mobilephone3         VARCHAR2(20),
   unitzip              VARCHAR2(8),
   unitProvince         VARCHAR2(20),
   unitCity             VARCHAR2(20),
   unitDistrict         VARCHAR2(20),
   unitStreet           VARCHAR2(20),
   unitAddress          VARCHAR2(60),
   homezip              VARCHAR2(8),
   homeProvince         VARCHAR2(20),
   homeCity             VARCHAR2(20),
   homeDistrict         VARCHAR2(20),
   homeStreet           VARCHAR2(20),
   homeAddress          VARCHAR2(60),
   home2zip             VARCHAR2(8),
   home2Province        VARCHAR2(20),
   home2City            VARCHAR2(20),
   home2District        VARCHAR2(20),
   home2Street          VARCHAR2(20),
   home2Address         VARCHAR2(60),
   inuseAddress         VARCHAR2(1),
   SearchString         VARCHAR2(1000),
   memo                 VARCHAR2(500),
   LastModifyDate       DATE,
   CreateDate           DATE
);

comment on table F_ADDRESS_BOOK is
'系统中维持一个统一的通讯录 模块，主要目的是为了以后做 统一的接口，

比如：
      语音电话，短信平台等等

字段设计参照的Outlook的联系人';

comment on column F_ADDRESS_BOOK.BodyType is
'用户/个人/单位';

comment on column F_ADDRESS_BOOK.BodyCode is
'用户/个人/单位 编号';

comment on column F_ADDRESS_BOOK.inuseAddress is
'单位/住宅/住宅2';

comment on column F_ADDRESS_BOOK.SearchString is
'前面各个字段的中文首字母，数字 连接的串';

alter table F_ADDRESS_BOOK
   add constraint PK_F_ADDRESS_BOOK primary key (ADDRBOOKID);

/*==============================================================*/
/* Table: F_DATACATALOG                                         */
/*==============================================================*/
create table F_DATACATALOG  (
   CATALOGCODE          VARCHAR2(16)                    not null,
   CATALOGNAME          VARCHAR2(64)                    not null,
   CATALOGSTYLE         CHAR(1)                         not null,
   CATALOGTYPE          CHAR(1)                         not null,
   CATALOGDESC          VARCHAR2(256),
   FieldDesc            VARCHAR2(1024),
   updateDate           DATE,
   CreateDate           DATE,
   optID                VARCHAR2(16),
   needCache            CHAR(1)                        default '1',
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on table F_DATACATALOG is
'类别状态	 U:用户 S：系统，G国标
类别形式  T：树状表格 L:列表
';

comment on column F_DATACATALOG.CATALOGSTYLE is
'F : 框架固有的 U:用户 S：系统  G国标';

comment on column F_DATACATALOG.CATALOGTYPE is
'T：树状表格 L:列表
';

comment on column F_DATACATALOG.FieldDesc is
'字段描述，不同字段用分号隔开';

comment on column F_DATACATALOG.optID is
'业务分类，使用数据字典DICTIONARYTYPE中数据';

alter table F_DATACATALOG cache
/

alter table F_DATACATALOG
   add constraint SYS_C003033 primary key (CATALOGCODE);

/*==============================================================*/
/* Table: F_DATADICTIONARY                                      */
/*==============================================================*/
create table F_DATADICTIONARY  (
   CATALOGCODE          VARCHAR2(16)                    not null,
   DATACODE             VARCHAR2(16)                    not null,
   EXTRACODE            VARCHAR2(16),
   EXTRACODE2           VARCHAR2(16),
   DATATAG              CHAR(1),
   DATAVALUE            VARCHAR2(2048),
   DATASTYLE            CHAR(1),
   DATADESC             VARCHAR2(256),
   LastModifyDate       DATE,
   CreateDate           DATE,
   DATAORDER            NUMBER(6,0)
);

comment on table F_DATADICTIONARY is
'数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等

数据字典中的每个字段用户都可以根据自己的需要自解释其意义';

comment on column F_DATADICTIONARY.EXTRACODE is
'树型字典的父类代码';

comment on column F_DATADICTIONARY.EXTRACODE2 is
'默认的排序字段';

comment on column F_DATADICTIONARY.DATATAG is
'N正常，D已停用，用户可以自解释这个字段';

comment on column F_DATADICTIONARY.DATASTYLE is
'F : 框架固有的 U:用户 S：系统  G国标';

comment on column F_DATADICTIONARY.DATAORDER is
'排序字段';

alter table F_DATADICTIONARY cache
/

alter table F_DATADICTIONARY
   add constraint PK_DATADICTIONARY primary key (CATALOGCODE, DATACODE);

/*==============================================================*/
/* Table: F_OPTDATASCOPE                                        */
/*==============================================================*/
create table F_OPTDATASCOPE  (
   optScopeCode         VARCHAR2(16)                    not null,
   OptID                VARCHAR2(16),
   scopeName            VARCHAR2(64),
   FilterCondition      VARCHAR2(1024),
   scopeMemo            VARCHAR2(1024),
   FilterGroup          VARCHAR2(16)                   default 'G'
);

comment on column F_OPTDATASCOPE.FilterCondition is
'条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码';

comment on column F_OPTDATASCOPE.scopeMemo is
'数据权限说明';

alter table F_OPTDATASCOPE
   add constraint PK_F_OPTDATASCOPE primary key (optScopeCode);

/*==============================================================*/
/* Table: F_OPTDEF                                              */
/*==============================================================*/
create table F_OPTDEF  (
   OPTCODE              VARCHAR2(32)                    not null,
   OptID                VARCHAR2(32),
   OPTNAME              VARCHAR2(100),
   OPTMETHOD            VARCHAR2(50),
   OPTURL               VARCHAR2(256),
   OPTDESC              VARCHAR2(256),
   IsInWorkflow         CHAR(1),
   updateDate           DATE,
   CreateDate           DATE,
   OPTREQ               VARCHAR2(8),
   optOrder 			number(4),
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on column F_OPTDEF.OPTMETHOD is
'操作参数 方法';

comment on column F_OPTDEF.IsInWorkflow is
'是否为流程操作方法 F：不是  T ： 是';

alter table F_OPTDEF
   add constraint PK_F_OPTDEF primary key (OPTCODE);

/*==============================================================*/
/* Index: IND_OptID_OPTMETHOD                                   */
/*==============================================================*/
create index IND_OptID_OPTMETHOD on F_OPTDEF (
   OptID ASC,
   OPTMETHOD ASC
);

/*==============================================================*/
/* Table: F_OPT_LOG                                             */
/*==============================================================*/
create table F_OPT_LOG  (
   logId                NUMBER(12,0)                    not null,
   logLevel             VARCHAR2(2)                     not null,
   usercode             VARCHAR2(8)                     not null,
   opttime              DATE                            not null,
   OptContent           VARCHAR2(1000)                  not null,
   NewValue             CLOB,
   OldValue             CLOB,
   OptID                VARCHAR2(64)                    not null,
   OPTMethod            VARCHAR2(64),
   optTag               VARCHAR2(200)
);

comment on column F_OPT_LOG.OptContent is
'操作描述';

comment on column F_OPT_LOG.NewValue is
'新值';

comment on column F_OPT_LOG.OldValue is
'原值';

comment on column F_OPT_LOG.OptID is
'模块，或者表';

comment on column F_OPT_LOG.OPTMethod is
'方法，或者字段';

comment on column F_OPT_LOG.optTag is
'一般用于关联到业务主体的标识、表的主键等等';

alter table F_OPT_LOG
   add constraint PK_F_OPT_LOG primary key (logId);

/*==============================================================*/
/* Index: ind_Tag_ID                                            */
/*==============================================================*/
create index ind_Tag_ID on F_OPT_LOG (
   optTag ASC
);

/*==============================================================*/
/* Table: F_OptFlowNoInfo                                       */
/*==============================================================*/
create table F_OptFlowNoInfo  (
   OwnerCode            VARCHAR2(8)                     not null,
   CodeCode             VARCHAR2(16)                    not null,
   CodeDate             DATE                           default sysdate not null,
   CurNo                NUMBER(6,0)                    default 1 not null,
   LastCodeDate         DATE,
   CreateDate           DATE,
   LastModifyDate       DATE
);

alter table F_OptFlowNoInfo
   add constraint PK_F_OPTFLOWNOINFO primary key (OwnerCode, CodeDate, CodeCode);

/*==============================================================*/
/* Table: F_OptFlowNoPool                                       */
/*==============================================================*/
create table F_OptFlowNoPool  (
   OwnerCode            VARCHAR2(8)                     not null,
   CodeCode             VARCHAR2(16)                    not null,
   CodeDate             DATE                           default sysdate not null,
   CurNo                NUMBER(6,0)                    default 1 not null,
   CreateDate           DATE
);

alter table F_OptFlowNoPool
   add constraint PK_F_OPTFLOWNOPOOL primary key (OwnerCode, CodeDate, CodeCode, CurNo);

/*==============================================================*/
/* Table: F_OptInfo                                             */
/*==============================================================*/
create table F_OptInfo  (
   OptID                VARCHAR2(32)                    not null,
   OptName              VARCHAR2(100)                   not null,
   PreOptID             VARCHAR2(32)                    not null,
   optRoute             VARCHAR2(256),
   opturl               VARCHAR2(256),
   FormCode             VARCHAR2(4),
   OptType              CHAR(1),
   MsgNo                NUMBER(10,0),
   MsgPrm               VARCHAR2(256),
   IsInToolBar          CHAR(1),
   ImgIndex             NUMBER(10,0),
   TopOptID             VARCHAR2(8),
   OrderInd             NUMBER(4,0),
   FLOWCODE             VARCHAR2(8),
   PageType             CHAR(1)                        default 'I' not null,
   Icon                 VARCHAR2(512),
   height               NUMBER(10,0),
   width                NUMBER(10,0),
   updateDate           DATE,
   CreateDate           DATE,
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on column F_OptInfo.optRoute is
'与angularjs路由匹配';

comment on column F_OptInfo.OptType is
' S:实施业务, O:普通业务, W:流程业务, I :项目业务';

comment on column F_OptInfo.OrderInd is
'这个顺序只需在同一个父业务下排序';

comment on column F_OptInfo.FLOWCODE is
'同一个代码的流程应该只有一个有效的版本';

comment on column F_OptInfo.PageType is
'D : DIV I:iFrame';

alter table F_OptInfo
   add constraint PK_F_OPTINFO primary key (OptID);

/*==============================================================*/
/* Table: F_OptInfoData                                         */
/*==============================================================*/
create table F_OptInfoData  (
   TBCODE               VARCHAR2(32)                    not null,
   OptID                VARCHAR2(8)                     not null,
   LastModifyDate       DATE,
   CreateDate           DATE
);

comment on table F_OptInfoData is
'业务模块和表是多对多的关系,这个表仅仅是作为数据权限设置时的一个辅助表的';

alter table F_OptInfoData
   add constraint PK_F_OPTINFODATA primary key (TBCODE, OptID);

/*==============================================================*/
/* Table: F_QUERY_FILTER_CONDITION                              */
/*==============================================================*/
create table F_QUERY_FILTER_CONDITION  (
   CONDITION_NO         NUMBER(12,0)                    not null,
   Table_Class_Name     VARCHAR2(64)                    not null,
   Param_Name           VARCHAR2(64)                    not null,
   Param_Label          VARCHAR2(120)                   not null,
   Param_Type           VARCHAR2(8),
   Default_Value        VARCHAR2(100),
   Filter_Sql           VARCHAR2(200),
   Select_Data_type     CHAR                           default 'N' not null,
   Select_Data_Catalog  VARCHAR2(64),
   Select_SQL           VARCHAR2(1000),
   Select_JSON          VARCHAR2(2000)
);

comment on column F_QUERY_FILTER_CONDITION.Table_Class_Name is
'数据库表代码或者po的类名';

comment on column F_QUERY_FILTER_CONDITION.Param_Name is
'参数名';

comment on column F_QUERY_FILTER_CONDITION.Param_Label is
'参数输入框提示';

comment on column F_QUERY_FILTER_CONDITION.Param_Type is
'参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月';

comment on column F_QUERY_FILTER_CONDITION.Filter_Sql is
'过滤语句，将会拼装到sql语句中';

comment on column F_QUERY_FILTER_CONDITION.Select_Data_type is
'数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取
';

comment on column F_QUERY_FILTER_CONDITION.Select_Data_Catalog is
'数据字典';

comment on column F_QUERY_FILTER_CONDITION.Select_SQL is
'有两个返回字段的sql语句';

comment on column F_QUERY_FILTER_CONDITION.Select_JSON is
'KEY,Value数值对，JSON格式';

alter table F_QUERY_FILTER_CONDITION
   add constraint PK_F_QUERY_FILTER_CONDITION primary key (CONDITION_NO);

/*==============================================================*/
/* Index: Ind_Filter_Table_Class_Name                           */
/*==============================================================*/
create index Ind_Filter_Table_Class_Name on F_QUERY_FILTER_CONDITION (
   Table_Class_Name ASC
);

/*==============================================================*/
/* Table: F_RANKGRANT                                           */
/*==============================================================*/
create table F_RANKGRANT  (
   RANK_grant_ID        number(12)                      not null,
   granter              VARCHAR2(8)                     not null,
   UNITCODE             VARCHAR2(6)                     not null,
   UserStation          VARCHAR2(4)                     not null,
   UserRank             VARCHAR2(2)                     not null,
   beginDate            DATE                            not null,
   grantee              VARCHAR2(8)                     not null,
   endDate              DATE,
   grantDesc            VARCHAR2(256),
   LastModifyDate       DATE,
   CreateDate           DATE
);

comment on column F_RANKGRANT.UserRank is
'RANK 代码不是 0开头的可以进行授予';

alter table F_RANKGRANT
   add constraint PK_F_RANKGRANT primary key (RANK_grant_ID, UserRank);

/*==============================================================*/
/* Table: F_ROLEINFO                                            */
/*==============================================================*/
create table F_ROLEINFO  (
   ROLECODE             VARCHAR2(32)                    not null,
   ROLENAME             VARCHAR2(64),
   ROLETYPE             CHAR(1)                         not null,
   UNITCODE             VARCHAR2(32),
   ISVALID              CHAR(1)                         not null,
   ROLEDESC             VARCHAR2(256),
   updateDate           DATE,
   CreateDate           DATE,
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on column F_ROLEINFO.ROLETYPE is
'S为系统功能角色 I 为项目角色 W工作量角色';

alter table F_ROLEINFO
   add constraint PK_F_ROLEINFO primary key (ROLECODE);

/*==============================================================*/
/* Table: F_ROLEPOWER                                           */
/*==============================================================*/
create table F_ROLEPOWER  (
   ROLECODE             VARCHAR2(32)                    not null,
   OPTCODE              VARCHAR2(32)                    not null,
   optScopeCodes        VARCHAR2(1000),
   updateDate           DATE,
   CreateDate           DATE,
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on column F_ROLEPOWER.optScopeCodes is
'用逗号隔开的数据范围结合（空\all 表示全部）';

alter table F_ROLEPOWER
   add constraint PK_WFROLEPOWER primary key (ROLECODE, OPTCODE);

/*==============================================================*/
/* Table: F_STAT_MONTH                                          */
/*==============================================================*/
create table F_STAT_MONTH  (
   YEARMONTH            VARCHAR2(6)                     not null,
   BeginDay             DATE                            not null,
   EendDay              DATE                            not null,
   EndSchedule          CHAR(1),
   BeginSchedule        CHAR(1)
);

comment on table F_STAT_MONTH is
'OA业务统计月，可以自定义统计月的起止日期';

comment on column F_STAT_MONTH.YEARMONTH is
'YYYYMM';

comment on column F_STAT_MONTH.EndSchedule is
'这个字段忽略';

comment on column F_STAT_MONTH.BeginSchedule is
'这个字段忽略';

alter table F_STAT_MONTH
   add constraint PK_F_STAT_MONTH primary key (YEARMONTH);

/*==============================================================*/
/* Table: F_SYS_NOTIFY                                          */
/*==============================================================*/
create table F_SYS_NOTIFY  (
   Notify_ID            NUMBER(12,0)                    not null,
   Notify_Sender        VARCHAR2(100),
   Notify_Receiver      VARCHAR2(100)                   not null,
   Msg_Subject          VARCHAR2(200),
   Msg_Content          VARCHAR2(2000)                  not null,
   notice_Type          VARCHAR2(100),
   Notify_State         CHAR(1),
   Error_Msg            VARCHAR2(500),
   Notify_Time          DATE,
   optTag               VARCHAR2(200),
   OPTMethod            VARCHAR2(64),
   OptID                VARCHAR2(64)                    not null
);

comment on column F_SYS_NOTIFY.Notify_State is
'0 成功， 1 失败 2 部分成功';

comment on column F_SYS_NOTIFY.optTag is
'一般用于关联到业务主体';

comment on column F_SYS_NOTIFY.OPTMethod is
'方法，或者字段';

comment on column F_SYS_NOTIFY.OptID is
'模块，或者表';

alter table F_SYS_NOTIFY
   add constraint PK_F_SYS_NOTIFY primary key (Notify_ID);

/*==============================================================*/
/* Table: F_UNITINFO                                            */
/*==============================================================*/
create table F_UNITINFO  (
   UNITCODE             VARCHAR2(32)                    not null,
   PARENTUNIT           VARCHAR2(32),
   UNITTYPE             CHAR(1),
   ISVALID              CHAR(1)                         not null,
   UNITTAG              VARCHAR2(100),
   UNITNAME             VARCHAR2(300)                   not null,
   englishName          VARCHAR2(300),
   depno                VARCHAR2(100),
   UNITDESC             VARCHAR2(256),
   ADDRBOOKID           NUMBER(10,0),
   UNITSHORTNAME        VARCHAR2(32),
   unitWord             VARCHAR2(100),
   unitGrade            NUMBER(4,0),
   unitOrder            NUMBER(4,0),
   updateDate           DATE,
   CreateDate           DATE,
   extJsonInfo          VARCHAR2(1000),
   creator              VARCHAR2(32),
   updator              VARCHAR2(32),
   UNITPATH             VARCHAR2(1000)
);

comment on column F_UNITINFO.UNITTYPE is
'发布任务/ 邮电规划/组队/接收任务';

comment on column F_UNITINFO.ISVALID is
'T:生效 F:无效';

comment on column F_UNITINFO.UNITTAG is
'用户第三方系统管理';

comment on column F_UNITINFO.depno is
'组织机构代码：';

alter table F_UNITINFO
   add constraint SYS_C006472 primary key (UNITCODE);

/*==============================================================*/
/* Table: F_USERINFO                                            */
/*==============================================================*/
create table F_USERINFO  (
   USERCODE             VARCHAR2(32)                    not null,
   USERPIN              VARCHAR2(100),
   USERTYPE             CHAR(1)                        default 'U',
   ISVALID              CHAR(1)                         not null,
   LOGINNAME            VARCHAR2(100)                   not null,
   UserName             VARCHAR2(300)                   not null,
   USERTAG              VARCHAR2(100),
   englishName          VARCHAR2(300),
   USERDESC             VARCHAR2(256),
   LoginTimes           NUMBER(6,0),
   ActiveTime           DATE,
   LoginIP              VARCHAR2(16),
   ADDRBOOKID           NUMBER(10,0),
   RegEmail             VARCHAR2(60),
   USERPWD              VARCHAR2(20),
   pwdExpiredTime       DATE,
   REGCELLPHONE         VARCHAR2(15),
   primaryUnit          VARCHAR2(32),
   userWord             VARCHAR2(100),
   userOrder            NUMBER(4,0),
   updateDate           DATE,
   CreateDate           DATE,
   extJsonInfo          VARCHAR2(1000),
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on column F_USERINFO.USERTYPE is
'发布任务/接收任务/系统管理';

comment on column F_USERINFO.ISVALID is
'T:生效 F:无效';

comment on column F_USERINFO.UserName is
'昵称';

comment on column F_USERINFO.USERTAG is
'用于第三方系统关联';

comment on column F_USERINFO.RegEmail is
'注册用Email，不能重复';

comment on column F_USERINFO.USERPWD is
'如果需要可以有';

comment on column F_USERINFO.userWord is
'微信号';

alter table F_USERINFO
   add constraint PK_F_USERINFO primary key (USERCODE);

/*==============================================================*/
/* Index: Ind_loginname                                         */
/*==============================================================*/
create unique index Ind_loginname on F_USERINFO (
   LOGINNAME ASC
);

/*==============================================================*/
/* Index: ind_regemail                                          */
/*==============================================================*/
create unique index ind_regemail on F_USERINFO (
   RegEmail ASC
);

/*==============================================================*/
/* Table: F_USERROLE                                            */
/*==============================================================*/
create table F_USERROLE  (
   USERCODE             VARCHAR2(32)                    not null,
   ROLECODE             VARCHAR2(32)                    not null,
   OBTAINDATE           DATE                            not null,
   SECEDEDATE           DATE,
   CHANGEDESC           VARCHAR2(256),
   updateDate           DATE,
   CreateDate           DATE,
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

alter table F_USERROLE
   add constraint PK_WFUSERROLE primary key (USERCODE, ROLECODE);

/*==============================================================*/
/* Table: F_USERSETTING                                         */
/*==============================================================*/
create table F_USERSETTING  (
   USERCODE             VARCHAR2(8)                     not null,
   ParamCode            VARCHAR2(16)                    not null,
   ParamValue           VARCHAR2(2048)                  not null,
   optID                VARCHAR2(16)                    not null,
   ParamName            VARCHAR2(200),
   CreateDate           DATE
);

comment on column F_USERSETTING.USERCODE is
'DEFAULT:为默认设置
SYS001~SYS999: 为系统设置方案
是一个用户号,或者是系统的一个设置方案';

alter table F_USERSETTING
   add constraint SYS_C0064714 primary key (USERCODE, ParamCode);

/*==============================================================*/
/* Table: F_USERUNIT                                            */
/*==============================================================*/
create table F_USERUNIT  (
   USERUNITID           VARCHAR2(16)                    not null,
   UNITCODE             VARCHAR2(6)                     not null,
   USERCODE             VARCHAR2(8)                     not null,
   IsPrimary            CHAR(1)                        default '1' not null,
   UserStation          VARCHAR2(16)                    not null,
   UserRank             VARCHAR2(2)                     not null,
   RankMemo             VARCHAR2(256),
   USERORDER            number(8)                      default 0,
   updateDate           DATE,
   CreateDate           DATE,
   creator              VARCHAR2(32),
   updator              VARCHAR2(32)
);

comment on table F_USERUNIT is
'同一个人可能在多个部门担任不同的职位';

comment on column F_USERUNIT.IsPrimary is
'T：为主， F：兼职';

comment on column F_USERUNIT.UserRank is
'RANK 代码不是 0开头的可以进行授予';

comment on column F_USERUNIT.RankMemo is
'任职备注';

alter table F_USERUNIT
   add constraint PK_F_USERUNIT primary key (USERUNITID);

/*==============================================================*/
/* Table: F_USER_FAVORITE                                       */
/*==============================================================*/
create table F_USER_FAVORITE  (
   USERCODE             VARCHAR2(8)                     not null,
   OptID                VARCHAR2(16)                    not null,
   LastModifyDate       DATE,
   CreateDate           DATE
);

comment on column F_USER_FAVORITE.USERCODE is
'DEFAULT:为默认设置
SYS001~SYS999: 为系统设置方案
是一个用户号,或者是系统的一个设置方案';

alter table F_USER_FAVORITE
   add constraint SYS_C0064724 primary key (USERCODE, OptID);

/*==============================================================*/
/* Table: F_USER_QUERY_FILTER                                   */
/*==============================================================*/
create table F_USER_QUERY_FILTER  (
   FILTER_NO            NUMBER(12,0)                    not null,
   userCode             varchar2(8)                     not null,
   modle_code           varchar2(64)                    not null,
   filter_name          varchar2(200)                   not null,
   filter_value         varchar2(3200)                  not null
);

comment on column F_USER_QUERY_FILTER.modle_code is
'开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法';

comment on column F_USER_QUERY_FILTER.filter_name is
'用户自行定义的名称';

comment on column F_USER_QUERY_FILTER.filter_value is
'变量值，json格式，对应一个map';

alter table F_USER_QUERY_FILTER
   add constraint PK_F_USER_QUERY_FILTER primary key (FILTER_NO);

/*==============================================================*/
/* Index: Ind_query_filter_modle_code                           */
/*==============================================================*/
create index Ind_query_filter_modle_code on F_USER_QUERY_FILTER (
   modle_code ASC
);

/*==============================================================*/
/* Table: F_WORK_CLASS                                          */
/*==============================================================*/
create table F_WORK_CLASS  (
   CLASS_ID             NUMBER(12,0)                    not null,
   CLASS_NAME           VARCHAR2(50)                    not null,
   SHORT_NAME           VARCHAR2(10)                    not null,
   begin_time           VARCHAR2(6),
   end_time             VARCHAR2(6),
   has_break            CHAR(1),
   break_begin_time     VARCHAR2(6),
   break_end_time       VARCHAR2(6),
   class_desc           VARCHAR2(500),
   record_date          DATE,
   recorder             VARCHAR2(8)
);

comment on table F_WORK_CLASS is
'CLASS_ID
 为 0 的表示休息，可以不在这个表中出现
 为 1 的为默认班次信息';

comment on column F_WORK_CLASS.begin_time is
'9:00';

comment on column F_WORK_CLASS.end_time is
'+4:00 ''+''表示第二天';

comment on column F_WORK_CLASS.break_begin_time is
'9:00';

comment on column F_WORK_CLASS.break_end_time is
'+4:00 ''+''表示第二天';

alter table F_WORK_CLASS
   add constraint PK_F_WORK_CLASS primary key (CLASS_ID);

/*==============================================================*/
/* Table: F_WORK_DAY                                            */
/*==============================================================*/
create table F_WORK_DAY  (
   WorkDay              DATE                            not null,
   DayType              CHAR(1)                         not null,
   WorkTimeType         VARCHAR2(20),
   WorkDayDesc          VARCHAR2(255)
);

comment on table F_WORK_DAY is
'非正常作业时间日
A:工作日放假 B:周末调休成工作时间  C: 正常上班  D:正常休假  
';

comment on column F_WORK_DAY.DayType is
'A:工作日放假，B:周末调休成工作时间 C 正常上班 D正常休假';

alter table F_WORK_DAY
   add constraint PK_F_WORK_DAY primary key (WorkDay);

/*==============================================================*/
/* Table: M_InnerMsg                                            */
/*==============================================================*/
create table M_InnerMsg  (
   MsgCode              VARCHAR2(16)                    not null,
   Sender               VARCHAR2(128),
   SendDate             DATE,
   MsgTitle             VARCHAR2(128),
   MsgType              CHAR(1),
   MailType             CHAR(1),
   MailUnDelType        CHAR(1),
   ReceiveName          VARCHAR2(2048),
   HoldUsers            NUMBER(8,0),
   msgState             CHAR(1),
   msgContent           BLOB,
   EmailId              VARCHAR2(8),
   OptID                VARCHAR2(64)                    not null,
   OPTMethod            VARCHAR2(64),
   optTag               VARCHAR2(200)
);

comment on table M_InnerMsg is
'内部消息与公告
接受代码,  其实可以独立出来, 因为他 和发送人 是 一对多的关系

与原即时消息合并，所有消息均为即时编写发送，暂不包含草稿及定时发送。

定时清理策略由系统统一配置，见数据字典。';

comment on column M_InnerMsg.MsgCode is
'消息主键自定义，通过S_M_INNERMSG序列生成';

comment on column M_InnerMsg.MsgType is
'P= 个人为消息  A= 机构为公告（通知）
M=邮件';

comment on column M_InnerMsg.MailType is
'I=收件箱
O=发件箱
D=草稿箱
T=废件箱


';

comment on column M_InnerMsg.ReceiveName is
'使用部门，个人中文名，中间使用英文分号分割';

comment on column M_InnerMsg.HoldUsers is
'总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录

消息类型为邮件时不需要设置';

comment on column M_InnerMsg.msgState is
'未读/已读/删除';

comment on column M_InnerMsg.EmailId is
'用户配置多邮箱时使用';

comment on column M_InnerMsg.OptID is
'模块，或者表';

comment on column M_InnerMsg.OPTMethod is
'方法，或者字段';

comment on column M_InnerMsg.optTag is
'一般用于关联到业务主体';

alter table M_InnerMsg
   add constraint PK_M_INNERMSG primary key (MsgCode);

/*==============================================================*/
/* Table: M_InnerMsg_Recipient                                  */
/*==============================================================*/
create table M_InnerMsg_Recipient  (
   MsgCode              VARCHAR2(16)                    not null,
   Receive              VARCHAR2(8)                     not null,
   ReplyMsgCode         INTEGER,
   ReceiveType          CHAR(1),
   MailType             CHAR(1),
   msgState             CHAR(1),
   ID                   VARCHAR2(16)                    not null
);

comment on table M_InnerMsg_Recipient is
'内部消息（邮件）与公告收件人及消息信息';

comment on column M_InnerMsg_Recipient.ReceiveType is
'P=个人为消息
A=机构为公告
M=邮件';

comment on column M_InnerMsg_Recipient.MailType is
'T=收件人
C=抄送
B=密送';

comment on column M_InnerMsg_Recipient.msgState is
'未读/已读/删除，收件人在线时弹出提示

U=未读
R=已读
D=删除';

alter table M_InnerMsg_Recipient
   add constraint PK_M_INNERMSG_RECIPIENT primary key (ID);

/*==============================================================*/
/* Table: M_MsgAnnex                                            */
/*==============================================================*/
create table M_MsgAnnex  (
   MsgCode              VARCHAR2(16)                    not null,
   InfoCode             VARCHAR2(16)                    not null,
   MsgAnnexId           VARCHAR2(16)                    not null
);

alter table M_MsgAnnex
   add constraint PK_M_MSGANNEX primary key (MsgAnnexId);

/*==============================================================*/
/* Table: P_TASK_LIST                                           */
/*==============================================================*/
create table P_TASK_LIST  (
   taskid               NUMBER(12,0)                    not null,
   taskowner            VARCHAR2(8)                     not null,
   tasktag              VARCHAR2(1)                     not null,
   taskrank             VARCHAR2(1)                     not null,
   taskstatus           VARCHAR2(2)                     not null,
   tasktitle            VARCHAR2(256)                   not null,
   taskmemo             VARCHAR2(1000),
   tasktype             VARCHAR2(8)                     not null,
   OptID                VARCHAR2(64)                    not null,
   OPTMethod            VARCHAR2(64),
   optTag               VARCHAR2(200),
   creator              VARCHAR2(32)                    not null,
   created              DATE                            not null,
   planbegintime        DATE                            not null,
   planendtime          DATE,
   begintime            DATE,
   endtime              DATE,
   finishmemo           VARCHAR2(1000),
   noticeSign           VARCHAR2(1),
   lastNoticeTime       DATE,
   taskdeadline         DATE,
   taskvalue            VARCHAR2(2048)
);

comment on column P_TASK_LIST.taskid is
'自动生成的主键，需要一个序列来配合';

comment on column P_TASK_LIST.taskowner is
'谁的任务';

comment on column P_TASK_LIST.tasktag is
'类似与outlook中的邮件标记，可以用不同的颜色的旗子图表标识';

comment on column P_TASK_LIST.taskrank is
'任务的优先级';

comment on column P_TASK_LIST.taskstatus is
'处理中、完成、取消、终止';

comment on column P_TASK_LIST.taskmemo is
'简要描述任务的具体内容';

comment on column P_TASK_LIST.tasktype is
'个人、组织活动、领导委派 等等';

comment on column P_TASK_LIST.OptID is
'模块，或者表';

comment on column P_TASK_LIST.OPTMethod is
'方法，或者字段';

comment on column P_TASK_LIST.optTag is
'一般用于关联到业务主体';

comment on column P_TASK_LIST.finishmemo is
'简要记录任务的执行过程和结果';

comment on column P_TASK_LIST.noticeSign is
'提醒标志为：禁止提醒、未提醒、已提醒';

comment on column P_TASK_LIST.lastNoticeTime is
'最后一次提醒时间，根据提醒策略可以提醒多次';

comment on column P_TASK_LIST.taskvalue is
'备用，字段不够时使用';

alter table P_TASK_LIST
   add constraint PK_P_TASK_LIST primary key (taskid);

/*==============================================================*/
/* View: F_V_Opt_Role_Map                                       */
/*==============================================================*/
create or replace view F_V_Opt_Role_Map as
select c.opturl || b.opturl as opturl, b.optreq, a.rolecode, c.optid, b.optcode
  from F_ROLEPOWER a
  join F_OPTDEF b
    on (a.optcode = b.optcode)
  join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...'
 order by c.opturl, b.optreq, a.rolecode

;

/*==============================================================*/
/* View: F_V_USERROLES                                          */
/*==============================================================*/
create or replace view F_V_USERROLES as
select distinct b.ROLECODE,b.ROLENAME,b.ISVALID,b.ROLEDESC,b.CREATEDATE,b.UPDATEDATE ,a.usercode
    from F_USERROLE a join F_ROLEINFO b on (a.ROLECODE=b.ROLECODE)
    where a.OBTAINDATE <= sysdate and (a.SECEDEDATE is null or a.SECEDEDATE > sysdate) and b.ISVALID='T'
union all
  select d.ROLECODE,d.ROLENAME,d.ISVALID,d.ROLEDESC,d.CREATEDATE,d.UPDATEDATE , c.usercode
   from f_userinfo c , F_ROLEINFO d
   where d.rolecode = 'G-public'
;

/*==============================================================*/
/* View: F_V_UserOptDataScopes                                  */
/*==============================================================*/
create or replace view F_V_UserOptDataScopes as
select  distinct a.UserCode, c. OPTID ,  c.OPTMETHOD , b.optScopeCodes
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
;

/*==============================================================*/
/* View: F_V_UserOptList                                        */
/*==============================================================*/
create or replace view F_V_UserOptList as
select  distinct a.UserCode,  c.OPTCODE,  c.OPTNAME  ,  c. OPTID ,  c.OPTMETHOD
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
;

/*==============================================================*/
/* View: F_V_UserOptMoudleList                                  */
/*==============================================================*/
create or replace view F_V_UserOptMoudleList as
select  distinct a.UserCode,d.OptID, d.OptName , d.PreOptID  ,
            d.FormCode, d.optroute, d.opturl, d.MsgNo , d.MsgPrm, d.IsInToolBar ,
            d.ImgIndex,d.TopOptID ,d.OrderInd,d.PageType,d.opttype
from F_V_USERROLES a  join F_ROLEPOWER b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
        join F_OptInfo d on(c.OPTID=d.OptID)
where  d.opturl<> '...'
;

/*==============================================================*/
/* View: V_Hi_Optinfo                                           */
/*==============================================================*/
create or replace view V_Hi_Optinfo as
select connect_by_root  t.optid  as topoptid, level as hi_level , t.optid , t.optname
from f_optinfo t
connect by prior t.optid = t.preoptid

;

/*==============================================================*/
/* View: f_v_optdef_url_map                                     */
/*==============================================================*/
create or replace view f_v_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...' and b.optreq is not null
;

/*==============================================================*/
/* View: f_v_wf_optdef_url_map                                  */
/*==============================================================*/
create or replace view f_v_wf_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType = 'W'
   and c.opturl <> '...' and b.optreq is not null
;

/*==============================================================*/
/* View: v_hi_unitinfo                                          */
/*==============================================================*/
create or replace view v_hi_unitinfo as
select b.topunitcode as topunitcode, a.unitcode,a.unittype, a.parentunit, a.isvalid,
       a.unitname,a.unitdesc,a.unitshortname,a.addrbookid,a.unitorder,
       a.unitword,a.unitgrade, b.hi_level,b.UnitPath
  from f_unitinfo a,
       (select level as hi_level,
               unitcode,
               CONNECT_BY_ROOT unitcode as topunitcode,
               SYS_CONNECT_BY_PATH(unitcode, '/') UnitPath
          from f_unitinfo t
        connect by prior unitcode = parentunit) b
 where a.unitcode = b.unitcode
 
;

 comment on table v_hi_unitinfo is
'下级部门可以通过语句 
select * from v_hi_unitinfo 
where topunitcode = ''机构代码来获取'' 
order by topunitcode,hi_level 

需要视图 

';

comment on column v_hi_unitinfo.unittype is
'发布任务/ 邮电规划/组队/接收任务';

comment on column v_hi_unitinfo.isvalid is
'T:生效 F:无效';

/*==============================================================*/
/* View: v_opt_tree                                             */
/*==============================================================*/
create or replace view v_opt_tree as
select
   MENU_ID,
   PARENT_ID,
   MENU_NAME,
   ORDERIND
from
   (select i.optid as MENU_ID,i.preoptid as PARENT_ID,i.optname as MENU_NAME,i.orderind 
   from f_optinfo i where i.isintoolbar ='Y')
   union all 
   (select d.optcode as MENU_ID,d.optid as PARENT_ID,d.optname as MENU_NAME,0 as orderind 
   from f_optdef d)
order by
   orderind ASC
;

