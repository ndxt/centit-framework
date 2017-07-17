/*==============================================================*/
/* DBMS name:      DB2                        */
/* Created on:     2016/6/28 17:51:25                           */
/*==============================================================*/


 drop table F_ADDRESS_BOOK ;

drop table F_DATACATALOG  ;

drop table F_DATADICTIONARY  ;

drop table F_OPTDATASCOPE  ;

drop index IND_OptID_OPTMETHOD;

drop table F_OPTDEF  ;

drop index ind_Tag_ID;

drop table F_OPT_LOG  ;

drop table F_OptFlowNoInfo  ;

drop table F_OptFlowNoPool  ;

drop table F_OptInfo  ;

drop table F_OptInfoData  ;

drop index Ind_Filter_Table_Class_Name;

drop table F_QUERY_FILTER_CONDITION  ;

drop table F_RANKGRANT  ;

drop table F_ROLEINFO  ;

drop table F_ROLEPOWER  ;

drop table F_STAT_MONTH  ;

drop table F_SYS_NOTIFY  ;

drop table F_UNITINFO  ;

drop index ind_regemail;

drop index Ind_loginname;

drop table F_USERINFO  ;

drop table F_USERROLE  ;

drop table F_USERSETTING  ;

drop table F_USERUNIT  ;

drop table F_USER_FAVORITE  ;

drop index Ind_query_filter_modle_code;

drop table F_USER_QUERY_FILTER  ;

drop table F_WORK_CLASS  ;

drop table F_WORK_DAY  ;

drop table M_InnerMsg  ;

drop table M_InnerMsg_Recipient  ;

drop table M_MsgAnnex  ;

drop table P_TASK_LIST  ;

/*==============================================================*/
/* 创建 序列                                                                                                                                                                     
*/
/*==============================================================*/
/*
create sequence S_Filter_No;

create sequence s_notify_id;

create sequence s_optdefcode
start with 1100000;

create sequence s_sys_log;

create sequence s_unitcode;

create sequence s_user_unit_id;

create sequence s_usercode; */

/*==============================================================*/
/* Table: F_ADDRESS_BOOK                                        */
/*==============================================================*/
create table F_ADDRESS_BOOK  (
   ADDRBOOKID           decimal(10,0)                    not null,
   BodyType             VARCHAR(2)                     not null,
   BodyCode             VARCHAR(16)                    not null,
   representation       VARCHAR(200),
   UnitName             VARCHAR(200),
   DeptName             VARCHAR(100),
   RankName             VARCHAR(50),
   Email                VARCHAR(60),
   Email2               VARCHAR(60),
   Email3               VARCHAR(60),
   HomePage             VARCHAR(100),
   QQ                   VARCHAR(20),
   MSN                  VARCHAR(60),
   wangwang             VARCHAR(20),
   buzPhone             VARCHAR(20),
   buzphone2            VARCHAR(20),
   buzfax               VARCHAR(20),
   assiphone            VARCHAR(20),
   callbacphone         VARCHAR(20),
   carphone             VARCHAR(20),
   unitphone            VARCHAR(20),
   homephone            VARCHAR(20),
   homephone2           VARCHAR(20),
   homephone3           VARCHAR(20),
   homefax              VARCHAR(20),
   mobilephone          VARCHAR(20),
   mobilephone2         VARCHAR(20),
   mobilephone3         VARCHAR(20),
   unitzip              VARCHAR(8),
   unitProvince         VARCHAR(20),
   unitCity             VARCHAR(20),
   unitDistrict         VARCHAR(20),
   unitStreet           VARCHAR(20),
   unitAddress          VARCHAR(60),
   homezip              VARCHAR(8),
   homeProvince         VARCHAR(20),
   homeCity             VARCHAR(20),
   homeDistrict         VARCHAR(20),
   homeStreet           VARCHAR(20),
   homeAddress          VARCHAR(60),
   home2zip             VARCHAR(8),
   home2Province        VARCHAR(20),
   home2City            VARCHAR(20),
   home2District        VARCHAR(20),
   home2Street          VARCHAR(20),
   home2Address         VARCHAR(60),
   inuseAddress         VARCHAR(1),
   SearchString         VARCHAR(1000),
   memo                 VARCHAR(500),
   LastModifyDate       TIMESTAMP,
   CreateDate           TIMESTAMP
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
   CATALOGCODE          VARCHAR(16)                    not null,
   CATALOGNAME          VARCHAR(64)                    not null,
   CATALOGSTYLE         CHAR(1)                         not null,
   CATALOGTYPE          CHAR(1)                         not null,
   CATALOGDESC          VARCHAR(256),
   FieldDesc            VARCHAR(1024),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   optID                VARCHAR(16),
   needCache            CHAR(1)                        default 1,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
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



alter table F_DATACATALOG
   add constraint SYS_C003033 primary key (CATALOGCODE);

/*==============================================================*/
/* Table: F_DATADICTIONARY                                      */
/*==============================================================*/
create table F_DATADICTIONARY  (
   CATALOGCODE          VARCHAR(16)                    not null,
   DATACODE             VARCHAR(16)                    not null,
   EXTRACODE            VARCHAR(16),
   EXTRACODE2           VARCHAR(16),
   DATATAG              CHAR(1),
   DATAVALUE            VARCHAR(2048),
   DATASTYLE            CHAR(1),
   DATADESC             VARCHAR(256),
   LastModifyDate       TIMESTAMP,
   CreateDate           TIMESTAMP,
   DATAORDER            decimal(6,0)
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


alter table F_DATADICTIONARY
   add constraint PK_DATADICTIONARY primary key (CATALOGCODE, DATACODE);

/*==============================================================*/
/* Table: F_OPTDATASCOPE                                        */
/*==============================================================*/
create table F_OPTDATASCOPE  (
   optScopeCode         VARCHAR(16)                    not null,
   OptID                VARCHAR(16),
   scopeName            VARCHAR(64),
   FilterCondition      VARCHAR(1024),
   scopeMemo            VARCHAR(1024),
   FilterGroup          VARCHAR(16)                   default 'G'
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
   OPTCODE              VARCHAR(32)                    not null,
   OptID                VARCHAR(32),
   OPTNAME              VARCHAR(100),
   OPTMETHOD            VARCHAR(50),
   OPTURL               VARCHAR(256),
   OPTDESC              VARCHAR(256),
   IsInWorkflow         CHAR(1),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   OPTREQ               VARCHAR(8),
   optOrder 			numeric(4,0),
   creator              VARCHAR(32),
   updator              VARCHAR(32)
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
   logId                decimal(12,0)                    not null,
   logLevel             VARCHAR(2)                     not null,
   usercode             VARCHAR(8)                     not null,
   opttime              TIMESTAMP                            not null,
   OptContent           VARCHAR(1000)                  not null,
   NewValue             CLOB,
   OldValue             CLOB,
   OptID                VARCHAR(64)                    not null,
   OPTMethod            VARCHAR(64),
   optTag               VARCHAR(200)
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
   OwnerCode            VARCHAR(8)                     not null,
   CodeCode             VARCHAR(16)                    not null,
   CodeDate             TIMESTAMP                           default sysdate not null,
   CurNo                decimal(6,0)                    default 1 not null,
   LastCodeDate         TIMESTAMP,
   CreateDate           TIMESTAMP,
   LastModifyDate       TIMESTAMP
);

alter table F_OptFlowNoInfo
   add constraint PK_F_OPTFLOWNOINFO primary key (OwnerCode, CodeDate, CodeCode);

/*==============================================================*/
/* Table: F_OptFlowNoPool                                       */
/*==============================================================*/
create table F_OptFlowNoPool  (
   OwnerCode            VARCHAR(8)                     not null,
   CodeCode             VARCHAR(16)                    not null,
   CodeDate             TIMESTAMP                           default sysdate not null,
   CurNo                decimal(6,0)                    default 1 not null,
   CreateDate           TIMESTAMP
);

alter table F_OptFlowNoPool
   add constraint PK_F_OPTFLOWNOPOOL primary key (OwnerCode, CodeDate, CodeCode, CurNo);

/*==============================================================*/
/* Table: F_OptInfo                                             */
/*==============================================================*/
create table F_OptInfo  (
   OptID                VARCHAR(32)                    not null,
   OptName              VARCHAR(100)                   not null,
   PreOptID             VARCHAR(32)                    not null,
   optRoute             VARCHAR(256),
   opturl               VARCHAR(256),
   FormCode             VARCHAR(4),
   OptType              CHAR(1),
   MsgNo                decimal(10,0),
   MsgPrm               VARCHAR(256),
   IsInToolBar          CHAR(1),
   ImgIndex             decimal(10,0),
   TopOptID             VARCHAR(8),
   OrderInd             decimal(4,0),
   FLOWCODE             VARCHAR(8),
   PageType             CHAR(1)                        default 'I' not null,
   Icon                 VARCHAR(512),
   height               decimal(10,0),
   width                decimal(10,0),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
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
   TBCODE               VARCHAR(32)                    not null,
   OptID                VARCHAR(8)                     not null,
   LastModifyDate       TIMESTAMP,
   CreateDate           TIMESTAMP
);

comment on table F_OptInfoData is
'业务模块和表是多对多的关系,这个表仅仅是作为数据权限设置时的一个辅助表的';

alter table F_OptInfoData
   add constraint PK_F_OPTINFODATA primary key (TBCODE, OptID);

/*==============================================================*/
/* Table: F_QUERY_FILTER_CONDITION                              */
/*==============================================================*/
create table F_QUERY_FILTER_CONDITION  (
   CONDITION_NO         decimal(12,0)                    not null,
   Table_Class_Name     VARCHAR(64)                    not null,
   Param_Name           VARCHAR(64)                    not null,
   Param_Label          VARCHAR(120)                   not null,
   Param_Type           VARCHAR(8),
   Default_Value        VARCHAR(100),
   Filter_Sql           VARCHAR(200),
   Select_Data_type     CHAR                           default 'N' not null,
   Select_Data_Catalog  VARCHAR(64),
   Select_SQL           VARCHAR(1000),
   Select_JSON          VARCHAR(2000)
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
   RANK_grant_ID        decimal(12)                      not null,
   granter              VARCHAR(8)                     not null,
   UNITCODE             VARCHAR(6)                     not null,
   UserStation          VARCHAR(4)                     not null,
   UserRank             VARCHAR(2)                     not null,
   beginDate            TIMESTAMP                            not null,
   grantee              VARCHAR(8)                     not null,
   endDate              TIMESTAMP,
   grantDesc            VARCHAR(256),
   LastModifyDate       TIMESTAMP,
   CreateDate           TIMESTAMP
);

comment on column F_RANKGRANT.UserRank is
'RANK 代码不是 0开头的可以进行授予';

alter table F_RANKGRANT
   add constraint PK_F_RANKGRANT primary key (RANK_grant_ID, UserRank);

/*==============================================================*/
/* Table: F_ROLEINFO                                            */
/*==============================================================*/
create table F_ROLEINFO  (
   ROLECODE             VARCHAR(32)                    not null,
   ROLENAME             VARCHAR(64),
   ROLETYPE             CHAR(1)                         not null,
   UNITCODE             VARCHAR(32),
   ISVALID              CHAR(1)                         not null,
   ROLEDESC             VARCHAR(256),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
);

comment on column F_ROLEINFO.ROLETYPE is
'S为系统功能角色 I 为项目角色 W工作量角色';

alter table F_ROLEINFO
   add constraint PK_F_ROLEINFO primary key (ROLECODE);

/*==============================================================*/
/* Table: F_ROLEPOWER                                           */
/*==============================================================*/
create table F_ROLEPOWER  (
   ROLECODE             VARCHAR(32)                    not null,
   OPTCODE              VARCHAR(32)                    not null,
   optScopeCodes        VARCHAR(1000),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
);

comment on column F_ROLEPOWER.optScopeCodes is
'用逗号隔开的数据范围结合（空\all 表示全部）';

alter table F_ROLEPOWER
   add constraint PK_WFROLEPOWER primary key (ROLECODE, OPTCODE);

/*==============================================================*/
/* Table: F_STAT_MONTH                                          */
/*==============================================================*/
create table F_STAT_MONTH  (
   YEARMONTH            VARCHAR(6)                     not null,
   BeginDay             TIMESTAMP                            not null,
   EendDay              TIMESTAMP                            not null,
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
   Notify_ID            decimal(12,0)                    not null,
   Notify_Sender        VARCHAR(100),
   Notify_Receiver      VARCHAR(100)                   not null,
   Msg_Subject          VARCHAR(200),
   Msg_Content          VARCHAR(2000)                  not null,
   notice_Type          VARCHAR(100),
   Notify_State         CHAR(1),
   Error_Msg            VARCHAR(500),
   Notify_Time          TIMESTAMP,
   optTag               VARCHAR(200),
   OPTMethod            VARCHAR(64),
   OptID                VARCHAR(64)                    not null
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
   UNITCODE             VARCHAR(32)                    not null,
   PARENTUNIT           VARCHAR(32),
   UNITTYPE             CHAR(1),
   ISVALID              CHAR(1)                         not null,
   UNITTAG              VARCHAR(100),
   UNITNAME             VARCHAR(300)                   not null,
   englishName          VARCHAR(300),
   depno                VARCHAR(100),
   UNITDESC             VARCHAR(256),
   ADDRBOOKID           decimal(10,0),
   UNITSHORTNAME        VARCHAR(32),
   unitWord             VARCHAR(100),
   unitGrade            decimal(4,0),
   unitOrder            decimal(4,0),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   extJsonInfo          VARCHAR(1000),
   creator              VARCHAR(32),
   updator              VARCHAR(32),
   UNITPATH             VARCHAR(1000)
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
   USERCODE             VARCHAR(32)                    not null,
   USERPIN              VARCHAR(100),
   USERTYPE             CHAR(1)                        default 'U',
   ISVALID              CHAR(1)                         not null,
   LOGINNAME            VARCHAR(100)                   not null,
   UserName             VARCHAR(300)                   not null,
   USERTAG              VARCHAR(100),
   englishName          VARCHAR(300),
   USERDESC             VARCHAR(256),
   LoginTimes           decimal(6,0),
   ActiveTime           TIMESTAMP,
   LoginIP              VARCHAR(16),
   ADDRBOOKID           decimal(10,0),
   RegEmail             VARCHAR(60),
   USERPWD              VARCHAR(20),
   pwdExpiredTime       TIMESTAMP,
   REGCELLPHONE         VARCHAR(15),
   primaryUnit          VARCHAR(32),
   userWord             VARCHAR(100),
   userOrder            decimal(4,0),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   extJsonInfo          VARCHAR(1000),
   creator              VARCHAR(32),
   updator              VARCHAR(32)
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
   USERCODE             VARCHAR(32)                    not null,
   ROLECODE             VARCHAR(32)                    not null,
   OBTAINDate           TIMESTAMP                            not null,
   SECEDEDate           TIMESTAMP,
   CHANGEDESC           VARCHAR(256),
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
);

alter table F_USERROLE
   add constraint PK_WFUSERROLE primary key (USERCODE, ROLECODE);

/*==============================================================*/
/* Table: F_USERSETTING                                         */
/*==============================================================*/
create table F_USERSETTING  (
   USERCODE             VARCHAR(8)                     not null,
   ParamCode            VARCHAR(16)                    not null,
   ParamValue           VARCHAR(2048)                  not null,
   optID                VARCHAR(16)                    not null,
   ParamName            VARCHAR(200),
   CreateDate           TIMESTAMP
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
   USERUNITID           VARCHAR(16)                    not null,
   UNITCODE             VARCHAR(6)                     not null,
   USERCODE             VARCHAR(8)                     not null,
   IsPrimary            CHAR(1)                        default '1' not null,
   UserStation          VARCHAR(16)                    not null,
   UserRank             VARCHAR(2)                     not null,
   RankMemo             VARCHAR(256),
   USERORDER            decimal(8)                      default 0,
   updateDate           TIMESTAMP,
   CreateDate           TIMESTAMP,
   creator              VARCHAR(32),
   updator              VARCHAR(32)
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
   USERCODE             VARCHAR(8)                     not null,
   OptID                VARCHAR(16)                    not null,
   LastModifyDate       TIMESTAMP,
   CreateDate           TIMESTAMP
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
   FILTER_NO            decimal(12,0)                    not null,
   userCode             VARCHAR(8)                     not null,
   modle_code           VARCHAR(64)                    not null,
   filter_name          VARCHAR(200)                   not null,
   filter_value         VARCHAR(3200)                  not null
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
   CLASS_ID             decimal(12,0)                    not null,
   CLASS_NAME           VARCHAR(50)                    not null,
   SHORT_NAME           VARCHAR(10)                    not null,
   begin_time           VARCHAR(6),
   end_time             VARCHAR(6),
   has_break            CHAR(1),
   break_begin_time     VARCHAR(6),
   break_end_time       VARCHAR(6),
   class_desc           VARCHAR(500),
   record_date          TIMESTAMP,
   recorder             VARCHAR(8)
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
   WorkDay              TIMESTAMP                            not null,
   DayType              CHAR(1)                         not null,
   WorkTimeType         VARCHAR(20),
   WorkDayDesc          VARCHAR(255)
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
   MsgCode              VARCHAR(16)                    not null,
   Sender               VARCHAR(128),
   SendDate             TIMESTAMP,
   MsgTitle             VARCHAR(128),
   MsgType              CHAR(1),
   MailType             CHAR(1),
   MailUnDelType        CHAR(1),
   ReceiveName          VARCHAR(2048),
   HoldUsers            decimal(8,0),
   msgState             CHAR(1),
   msgContent           BLOB,
   EmailId              VARCHAR(8),
   OptID                VARCHAR(64)                    not null,
   OPTMethod            VARCHAR(64),
   optTag               VARCHAR(200)
);



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
   MsgCode              VARCHAR(16)                    not null,
   Receive              VARCHAR(8)                     not null,
   ReplyMsgCode         INTEGER,
   ReceiveType          CHAR(1),
   MailType             CHAR(1),
   msgState             CHAR(1),
   ID                   VARCHAR(16)                    not null
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
   MsgCode              VARCHAR(16)                    not null,
   InfoCode             VARCHAR(16)                    not null,
   MsgAnnexId           VARCHAR(16)                    not null
);

alter table M_MsgAnnex
   add constraint PK_M_MSGANNEX primary key (MsgAnnexId);

/*==============================================================*/
/* Table: P_TASK_LIST                                           */
/*==============================================================*/
create table P_TASK_LIST  (
   taskid               decimal(12,0)                    not null,
   taskowner            VARCHAR(8)                     not null,
   tasktag              VARCHAR(1)                     not null,
   taskrank             VARCHAR(1)                     not null,
   taskstatus           VARCHAR(2)                     not null,
   tasktitle            VARCHAR(256)                   not null,
   taskmemo             VARCHAR(1000),
   tasktype             VARCHAR(8)                     not null,
   OptID                VARCHAR(64)                    not null,
   OPTMethod            VARCHAR(64),
   optTag               VARCHAR(200),
   creator              VARCHAR(32)                    not null,
   created              TIMESTAMP                            not null,
   planbegintime        TIMESTAMP                            not null,
   planendtime          TIMESTAMP,
   begintime            TIMESTAMP,
   endtime              TIMESTAMP,
   finishmemo           VARCHAR(1000),
   noticeSign           VARCHAR(1),
   lastNoticeTime       TIMESTAMP,
   taskdeadline         TIMESTAMP,
   taskvalue            VARCHAR(1048)
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


/*
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
 --order by c.opturl, b.optreq, a.rolecode

;

/*==============================================================*/
/* View: F_V_USERROLES                                          */
/*==============================================================*/
create or replace view F_V_USERROLES as
select distinct b.ROLECODE,b.ROLENAME,b.ISVALID,b.ROLEDESC,b.CREATEDATE,b.UPDATEDATE ,a.usercode
    from F_USERROLE a join F_ROLEINFO b on (a.ROLECODE=b.ROLECODE)
    where a.OBTAINDATE <= CURRENT TIMESTAMP and (a.SECEDEDATE is null or a.SECEDEDATE > CURRENT TIMESTAMP) and b.ISVALID='T'
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
WITH RPL 
(hi_level,optid,topoptid,preoptid
--,optname
     )
AS
 (  SELECT 1             as hi_level,
         optid,
         preoptid as topoptid,
         preoptid
        --,optname
    FROM f_optinfo
  UNION ALL
  SELECT PARENT.hi_level + 1 as hi_level,
         CHILD.optid,
         PARENT.topoptid ,
         CHILD.preoptid
 --, optname
    FROM RPL PARENT, f_optinfo CHILD
   WHERE PARENT.optid = CHILD.preoptid)
SELECT hi_level,
       optid,
   topoptid,
   preoptid
   --,optname
  FROM RPL
;


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
WITH RPL 
(LEVEL,UNITCODE,topunitcode,PARENTUNIT
,UNITTYPE,ISVALID,
     UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
     unitOrder,unitWord,unitGrade
     ,UnitPath
     )
AS
 (SELECT 1             as LEVEL,
         UNITCODE,
         PARENTUNIT as topunitcode,
         PARENTUNIT    
         ,UNITTYPE,ISVALID,
     UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
     unitOrder,unitWord,unitGrade,UnitPath
    FROM f_Unitinfo
  UNION ALL
  SELECT PARENT.LEVEL + 1 as LEVEL,
         CHILD.UNITCODE,
         PARENT.topunitcode ,
         CHILD.PARENTUNIT
         ,CHILD.UNITTYPE, CHILD.ISVALID,
                    CHILD.UNITNAME,CHILD.UNITSHORTNAME,CHILD.UNITDESC,CHILD.ADDRBOOKID,
                    CHILD.unitOrder,CHILD.unitWord,CHILD.unitGrade
                    ,PARENT.UnitPath || '/'|| CHILD.UNITCODE as UnitPath
    FROM RPL PARENT, f_Unitinfo CHILD
   WHERE PARENT.UNITCODE = CHILD.PARENTUNIT)
SELECT LEVEL,
       UNITCODE,
   topunitcode,
       PARENTUNIT
       ,UNITTYPE,ISVALID,
     UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
     unitOrder,unitWord,unitGrade,UnitPath
  FROM RPL
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
  -- order by orderind ASC
;

*/