/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2016/4/11 9:38:17                            */
/*==============================================================*/


drop table if exists F_ADDRESS_BOOK;

drop table if exists F_DATACATALOG;

drop table if exists F_DATADICTIONARY;

drop table if exists F_OPTDATASCOPE;

-- drop index IND_OptID_OPTMETHOD on F_OPTDEF;

drop table if exists F_OPTDEF;

drop index ind_Tag_ID on F_OPT_LOG;

drop table if exists F_OPT_LOG;

drop table if exists F_OptFlowNoInfo;

drop table if exists F_OptFlowNoPool;

drop table if exists F_OptInfo;

drop table if exists F_OptInfoData;

-- drop index Ind_Filter_Table_Class_Name on F_QUERY_FILTER_CONDITION;

drop table if exists F_QUERY_FILTER_CONDITION;

drop table if exists F_RANKGRANT;

drop table if exists F_ROLEINFO;

drop table if exists F_ROLEPOWER;

drop table if exists F_STAT_MONTH;

drop table if exists F_SYS_NOTIFY;

drop table if exists F_UNITINFO;

drop index ind_regemail on F_USERINFO;

drop index Ind_loginname on F_USERINFO;

drop table if exists F_USERINFO;

drop table if exists F_USERROLE;

drop table if exists F_USERSETTING;

drop table if exists F_USERUNIT;

drop table if exists F_USER_FAVORITE;

drop index Ind_query_filter_modle_code on F_USER_QUERY_FILTER;

drop table if exists F_USER_QUERY_FILTER;

drop table if exists F_WORK_CLASS;

drop table if exists F_WORK_DAY;

drop table if exists M_InnerMsg;

drop table if exists M_InnerMsg_Recipient;

drop table if exists M_MsgAnnex;

drop table if exists P_TASK_LIST;

/*==============================================================*/
/* Table: F_ADDRESS_BOOK                                        */
/*==============================================================*/
create table F_ADDRESS_BOOK
(
   ADDRBOOKID           numeric(10,0) not null,
   BodyType             varchar(2) not null comment '用户/个人/单位',
   BodyCode             varchar(16) not null comment '用户/个人/单位 编号',
   representation       varchar(200),
   UnitName             varchar(200),
   DeptName             varchar(100),
   RankName             varchar(50),
   Email                varchar(60),
   Email2               varchar(60),
   Email3               varchar(60),
   HomePage             varchar(100),
   QQ                   varchar(20),
   MSN                  varchar(60),
   wangwang             varchar(20),
   buzPhone             varchar(20),
   buzphone2            varchar(20),
   buzfax               varchar(20),
   assiphone            varchar(20),
   callbacphone         varchar(20),
   carphone             varchar(20),
   unitphone            varchar(20),
   homephone            varchar(20),
   homephone2           varchar(20),
   homephone3           varchar(20),
   homefax              varchar(20),
   mobilephone          varchar(20),
   mobilephone2         varchar(20),
   mobilephone3         varchar(20),
   unitzip              varchar(8),
   unitProvince         varchar(20),
   unitCity             varchar(20),
   unitDistrict         varchar(20),
   unitStreet           varchar(20),
   unitAddress          varchar(60),
   homezip              varchar(8),
   homeProvince         varchar(20),
   homeCity             varchar(20),
   homeDistrict         varchar(20),
   homeStreet           varchar(20),
   homeAddress          varchar(60),
   home2zip             varchar(8),
   home2Province        varchar(20),
   home2City            varchar(20),
   home2District        varchar(20),
   home2Street          varchar(20),
   home2Address         varchar(60),
   inuseAddress         varchar(1) comment '单位/住宅/住宅2',
   SearchString         varchar(1000) comment '前面各个字段的中文首字母，数字 连接的串',
   memo                 varchar(500),
   LastModifyDate       date,
   CreateDate           date,
   primary key (ADDRBOOKID)
);

alter table F_ADDRESS_BOOK comment '系统中维持一个统一的通讯录 模块，主要目的是为了以后做 统一的接口，

比如：
  ';

/*==============================================================*/
/* Table: F_DATACATALOG                                         */
/*==============================================================*/
create table F_DATACATALOG
(
   CATALOGCODE          varchar(16) not null,
   CATALOGNAME          varchar(64) not null,
   CATALOGSTYLE         char(1) not null comment 'F : 框架固有的 U:用户 S：系统  G国标',
   CATALOGTYPE          char(1) not null comment 'T：树状表格 L:列表
            ',
   CATALOGDESC          varchar(256),
   FieldDesc            varchar(256) comment '字段描述，不同字段用分号隔开',
   updateDate           date,
   CreateDate           date,
   optID                varchar(16) comment '业务分类，使用数据字典DICTIONARYTYPE中数据',
   needCache            char(1) default '1',
   creator              varchar(32),
   updator              varchar(32),
   primary key (CATALOGCODE)
);

alter table F_DATACATALOG comment '类别状态	 U:用户 S：系统，G国标
类别形式  T：树状表格 L:列表
';


/*==============================================================*/
/* Table: F_DATADICTIONARY                                      */
/*==============================================================*/
create table F_DATADICTIONARY
(
   CATALOGCODE          varchar(16) not null,
   DATACODE             varchar(16) not null,
   EXTRACODE            varchar(16) comment '树型字典的父类代码',
   EXTRACODE2           varchar(16) comment '默认的排序字段',
   DATATAG              char(1) comment 'N正常，D已停用，用户可以自解释这个字段',
   DATAVALUE            varchar(2048),
   DATASTYLE            char(1) comment 'F : 框架固有的 U:用户 S：系统  G国标',
   DATADESC             varchar(256),
   LastModifyDate       date,
   CreateDate           date,
   DATAORDER            numeric(6,0) comment '排序字段',
   primary key (CATALOGCODE, DATACODE)
);

alter table F_DATADICTIONARY comment '数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等
';

/*==============================================================*/
/* Table: F_OPTDATASCOPE                                        */
/*==============================================================*/
create table F_OPTDATASCOPE
(
   optScopeCode         varchar(16) not null,
   OptID                varchar(16),
   scopeName            varchar(64),
   FilterCondition      varchar(1024) comment '条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码',
   scopeMemo            varchar(1024) comment '数据权限说明',
   FilterGroup          varchar(16) default 'G',
   primary key (optScopeCode)
);

/*==============================================================*/
/* Table: F_OPTDEF                                              */
/*==============================================================*/
create table F_OPTDEF
(
   OPTCODE              varchar(32) not null,
   OptID                varchar(32),
   OPTNAME              varchar(100),
   OPTMETHOD            varchar(50) comment '操作参数 方法',
   OPTURL               varchar(256),
   OPTDESC              varchar(256),
   IsInWorkflow         char(1) comment '是否为流程操作方法 F：不是  T ： 是',
   updateDate           date,
   CreateDate           date,
   OPTREQ               varchar(8),
   optOrder 			numeric(4),
   creator              varchar(32),
   updator              varchar(32),
   primary key (OPTCODE)
);

/*==============================================================*/
/* Index: IND_OptID_OPTMETHOD                                   */
/*==============================================================*/
create index IND_OptID_OPTMETHOD on F_OPTDEF
(
   OptID,
   OPTMETHOD
);

/*==============================================================*/
/* Table: F_OPT_LOG                                             */
/*==============================================================*/
create table F_OPT_LOG
(
   logId                numeric(12,0) not null,
   logLevel             varchar(2) not null,
   usercode             varchar(8) not null,
   opttime              date not null,
   OptContent           varchar(1000) not null comment '操作描述',
   NewValue             text comment '新值',
   OldValue             text comment '原值',
   OptID                varchar(64) not null comment '模块，或者表',
   OPTMethod            varchar(64) comment '方法，或者字段',
   optTag               varchar(200) comment '一般用于关联到业务主体的标识、表的主键等等',
   primary key (logId)
);

/*==============================================================*/
/* Index: ind_Tag_ID                                            */
/*==============================================================*/
create index ind_Tag_ID on F_OPT_LOG
(
   optTag
);

/*==============================================================*/
/* Table: F_OptFlowNoInfo                                       */
/*==============================================================*/
create table F_OptFlowNoInfo
(
   OwnerCode            varchar(8) not null,
   CodeCode             varchar(16) not null,
   CodeDate             date not null,
   CurNo                numeric(6,0) not null default 1,
   LastCodeDate         date,
   CreateDate           date,
   LastModifyDate       date,
   primary key (OwnerCode, CodeDate, CodeCode)
);

/*==============================================================*/
/* Table: F_OptFlowNoPool                                       */
/*==============================================================*/
create table F_OptFlowNoPool
(
   OwnerCode            varchar(8) not null,
   CodeCode             varchar(16) not null,
   CodeDate             date not null,
   CurNo                numeric(6,0) not null default 1,
   CreateDate           date,
   primary key (OwnerCode, CodeDate, CodeCode, CurNo)
);

/*==============================================================*/
/* Table: F_OptInfo                                             */
/*==============================================================*/
create table F_OptInfo
(
   OptID                varchar(32) not null,
   OptName              varchar(100) not null,
   PreOptID             varchar(32) not null,
   optRoute             varchar(256) comment '与angularjs路由匹配',
   opturl               varchar(256),
   FormCode             varchar(4),
   OptType              char(1) comment ' S:实施业务, O:普通业务, W:流程业务, I :项目业务',
   MsgNo                numeric(10,0),
   MsgPrm               varchar(256),
   IsInToolBar          char(1),
   ImgIndex             numeric(10,0),
   TopOptID             varchar(8),
   OrderInd             numeric(4,0) comment '这个顺序只需在同一个父业务下排序',
   FLOWCODE             varchar(8) comment '同一个代码的流程应该只有一个有效的版本',
   PageType             char(1) not null default 'I' comment 'D : DIV I:iFrame',
   Icon                 varchar(512),
   height               numeric(10,0),
   width                numeric(10,0),
   updateDate           date,
   CreateDate           date,
   creator              varchar(32),
   updator              varchar(32),
   primary key (OptID)
);

/*==============================================================*/
/* Table: F_OptInfoData                                         */
/*==============================================================*/
create table F_OptInfoData
(
   TBCODE               varchar(32) not null,
   OptID                varchar(8) not null,
   LastModifyDate       date,
   CreateDate           date,
   primary key (TBCODE, OptID)
);

alter table F_OptInfoData comment '业务模块和表是多对多的关系,这个表仅仅是作为数据权限设置时的一个辅助表的';

/*==============================================================*/
/* Table: F_QUERY_FILTER_CONDITION                              */
/*==============================================================*/
create table F_QUERY_FILTER_CONDITION
(
   CONDITION_NO         numeric(12,0) not null,
   Table_Class_Name     varchar(64) not null comment '数据库表代码或者po的类名',
   Param_Name           varchar(64) not null comment '参数名',
   Param_Label          varchar(120) not null comment '参数输入框提示',
   Param_Type           varchar(8) comment '参数类型：S 字符串，L 数字， N 有小数点数据， D 日期， T 时间戳， Y 年， M 月',
   Default_Value        varchar(100),
   Filter_Sql           varchar(200) comment '过滤语句，将会拼装到sql语句中',
   Select_Data_type     char(1) not null default 'N' comment '数据下拉框内容； N ：没有， D 数据字典, S 通过sql语句获得， J json数据直接获取
            ',
   Select_Data_Catalog  varchar(64) comment '数据字典',
   Select_SQL           varchar(1000) comment '有两个返回字段的sql语句',
   Select_JSON          varchar(2000) comment 'KEY,Value数值对，JSON格式',
   primary key (CONDITION_NO)
);

/*==============================================================*/
/* Index: Ind_Filter_Table_Class_Name                           */
/*==============================================================*/
create index Ind_Filter_Table_Class_Name on F_QUERY_FILTER_CONDITION
(
   Table_Class_Name
);

/*==============================================================*/
/* Table: F_RANKGRANT                                           */
/*==============================================================*/
create table F_RANKGRANT
(
   RANK_grant_ID        numeric(12,0) not null,
   granter              varchar(8) not null,
   UNITCODE             varchar(6) not null,
   UserStation          varchar(4) not null,
   UserRank             varchar(2) not null comment 'RANK 代码不是 0开头的可以进行授予',
   beginDate            date not null,
   grantee              varchar(8) not null,
   endDate              date,
   grantDesc            varchar(256),
   LastModifyDate       date,
   CreateDate           date,
   primary key (RANK_grant_ID, UserRank)
);

/*==============================================================*/
/* Table: F_ROLEINFO                                            */
/*==============================================================*/
create table F_ROLEINFO
(
   ROLECODE             varchar(32) not null,
   ROLENAME             varchar(64),
   ROLETYPE             char(1) not null comment 'S为系统功能角色 I 为项目角色 W工作量角色',
   UNITCODE             varchar(32),
   ISVALID              char(1) not null,
   ROLEDESC             varchar(256),
   updateDate           date,
   CreateDate           date,
   creator              varchar(32),
   updator              varchar(32),
   primary key (ROLECODE)
);

/*==============================================================*/
/* Table: F_ROLEPOWER                                           */
/*==============================================================*/
create table F_ROLEPOWER
(
   ROLECODE             varchar(32) not null,
   OPTCODE              varchar(32) not null,
   optScopeCodes        varchar(1000) comment '用逗号隔开的数据范围结合（空\all 表示全部）',
   updateDate           date,
   CreateDate           date,
   creator              varchar(32),
   updator              varchar(32),
   primary key (ROLECODE, OPTCODE)
);

/*==============================================================*/
/* Table: F_STAT_MONTH                                          */
/*==============================================================*/
create table F_STAT_MONTH
(
   YEARMONTH            varchar(6) not null comment 'YYYYMM',
   BeginDay             date not null,
   EendDay              date not null,
   EndSchedule          char(1) comment '这个字段忽略',
   BeginSchedule        char(1) comment '这个字段忽略',
   primary key (YEARMONTH)
);

alter table F_STAT_MONTH comment 'OA业务统计月，可以自定义统计月的起止日期';

/*==============================================================*/
/* Table: F_SYS_NOTIFY                                          */
/*==============================================================*/
create table F_SYS_NOTIFY
(
   Notify_ID            numeric(12,0) not null,
   Notify_Sender        varchar(100),
   Notify_Receiver      varchar(100) not null,
   Msg_Subject          varchar(200),
   Msg_Content          varchar(2000) not null,
   notice_Type          varchar(100),
   Notify_State         char(1) comment '0 成功， 1 失败 2 部分成功',
   Error_Msg            varchar(500),
   Notify_Time          date,
   optTag               varchar(200) comment '一般用于关联到业务主体',
   OPTMethod            varchar(64) comment '方法，或者字段',
   OptID                varchar(64) not null comment '模块，或者表',
   primary key (Notify_ID)
);

/*==============================================================*/
/* Table: F_UNITINFO                                            */
/*==============================================================*/
create table F_UNITINFO
(
   UNITCODE             varchar(32) not null,
   PARENTUNIT           varchar(32),
   UNITTYPE             char(1) comment '发布任务/ 邮电规划/组队/接收任务',
   ISVALID              char(1) not null comment 'T:生效 F:无效',
   UNITTAG              varchar(100) comment '用户第三方系统管理',
   UNITNAME             varchar(300) not null,
   englishName          varchar(300),
   depno                varchar(100) comment '组织机构代码：',
   UNITDESC             varchar(256),
   ADDRBOOKID           numeric(10,0),
   UNITSHORTNAME        varchar(32),
   unitWord             varchar(100),
   unitGrade            numeric(4,0),
   unitOrder            numeric(4,0),
   updateDate           date,
   CreateDate           date,
   extJsonInfo          varchar(1000),
   creator              varchar(32),
   updator              varchar(32),
   UNITPATH 			VARCHAR(1000),
   primary key (UNITCODE)
);

/*==============================================================*/
/* Table: F_USERINFO                                            */
/*==============================================================*/
create table F_USERINFO
(
   USERCODE             varchar(32) not null,
   USERPIN              varchar(100),
   USERTYPE             char(1) default 'U' comment '发布任务/接收任务/系统管理',
   ISVALID              char(1) not null comment 'T:生效 F:无效',
   LOGINNAME            varchar(100) not null,
   UserName             varchar(300) not null comment '昵称',
   USERTAG              varchar(100) comment '用于第三方系统关联',
   englishName          varchar(300),
   USERDESC             varchar(256),
   LoginTimes           numeric(6,0),
   ActiveTime           date,
   LoginIP              varchar(16),
   ADDRBOOKID           numeric(10,0),
   RegEmail             varchar(60) comment '注册用Email，不能重复',
   USERPWD              varchar(20) comment '如果需要可以有',
   pwdExpiredTime       date,
   REGCELLPHONE         varchar(15),
   primaryUnit          varchar(32),
   userWord             varchar(100) comment '微信号',
   userOrder            numeric(4,0),
   updateDate           date,
   CreateDate           date,
   extJsonInfo          varchar(1000),
   creator              varchar(32),
   updator              varchar(32),
   primary key (USERCODE)
);

/*==============================================================*/
/* Index: Ind_loginname                                         */
/*==============================================================*/
create unique index Ind_loginname on F_USERINFO
(
   LOGINNAME
);

/*==============================================================*/
/* Index: ind_regemail                                          */
/*==============================================================*/
create unique index ind_regemail on F_USERINFO
(
   RegEmail
);

/*==============================================================*/
/* Table: F_USERROLE                                            */
/*==============================================================*/
create table F_USERROLE
(
   USERCODE             varchar(32) not null,
   ROLECODE             varchar(32) not null,
   OBTAINDATE           date not null,
   SECEDEDATE           date,
   CHANGEDESC           varchar(256),
   updateDate           date,
   CreateDate           date,
   creator              varchar(32),
   updator              varchar(32),
   primary key (USERCODE, ROLECODE)
);

/*==============================================================*/
/* Table: F_USERSETTING                                         */
/*==============================================================*/
create table F_USERSETTING
(
   USERCODE             varchar(8) not null comment 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   ParamCode            varchar(16) not null,
   ParamValue           varchar(2048) not null,
   optID                varchar(16) not null,
   ParamName            varchar(200),
   CreateDate           date,
   primary key (USERCODE, ParamCode)
);

/*==============================================================*/
/* Table: F_USERUNIT                                            */
/*==============================================================*/
create table F_USERUNIT
(
   USERUNITID           varchar(16) not null,
   UNITCODE             varchar(6) not null,
   USERCODE             varchar(8) not null,
   IsPrimary            char(1) not null default '1' comment 'T：为主， F：兼职',
   UserStation          varchar(16) not null,
   UserRank             varchar(2) not null comment 'RANK 代码不是 0开头的可以进行授予',
   RankMemo             varchar(256) comment '任职备注',
   USERORDER            numeric(8,0) default 0,
   updateDate           date,
   CreateDate           date,
   creator              varchar(32),
   updator              varchar(32),
   primary key (USERUNITID)
);

alter table F_USERUNIT comment '同一个人可能在多个部门担任不同的职位';

/*==============================================================*/
/* Table: F_USER_FAVORITE                                       */
/*==============================================================*/
create table F_USER_FAVORITE
(
   USERCODE             varchar(8) not null comment 'DEFAULT:为默认设置
            SYS001~SYS999: 为系统设置方案
            是一个用户号,或者是系统的一个设置方案',
   OptID                varchar(16) not null,
   LastModifyDate       date,
   CreateDate           date,
   primary key (USERCODE, OptID)
);

/*==============================================================*/
/* Table: F_USER_QUERY_FILTER                                   */
/*==============================================================*/
create table F_USER_QUERY_FILTER
(
   FILTER_NO            numeric(12,0) not null,
   userCode             varchar(8) not null,
   modle_code           varchar(64) not null comment '开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法',
   filter_name          varchar(200) not null comment '用户自行定义的名称',
   filter_value         varchar(3200) not null comment '变量值，json格式，对应一个map',
   primary key (FILTER_NO)
);

/*==============================================================*/
/* Index: Ind_query_filter_modle_code                           */
/*==============================================================*/
create index Ind_query_filter_modle_code on F_USER_QUERY_FILTER
(
   modle_code
);

/*==============================================================*/
/* Table: F_WORK_CLASS                                          */
/*==============================================================*/
create table F_WORK_CLASS
(
   CLASS_ID             numeric(12,0) not null,
   CLASS_NAME           varchar(50) not null,
   SHORT_NAME           varchar(10) not null,
   begin_time           varchar(6) comment '9:00',
   end_time             varchar(6) comment '+4:00 ''+''表示第二天',
   has_break            char(1),
   break_begin_time     varchar(6) comment '9:00',
   break_end_time       varchar(6) comment '+4:00 ''+''表示第二天',
   class_desc           varchar(500),
   record_date          date,
   recorder             varchar(8),
   primary key (CLASS_ID)
);

alter table F_WORK_CLASS comment 'CLASS_ID
 为 0 的表示休息，可以不在这个表中出现
 为 1 的为默认班次信息';

/*==============================================================*/
/* Table: F_WORK_DAY                                            */
/*==============================================================*/
create table F_WORK_DAY
(
   WorkDay              date not null,
   DayType              char(1) not null comment 'A:工作日放假，B:周末调休成工作时间 C 正常上班 D正常休假',
   WorkTimeType         varchar(20),
   WorkDayDesc          varchar(255),
   primary key (WorkDay)
);

alter table F_WORK_DAY comment '非正常作业时间日
A:工作日放假 B:周末调休成工作时间  C: 正常上班  D:正常休假  
';

/*==============================================================*/
/* Table: M_InnerMsg                                            */
/*==============================================================*/
create table M_InnerMsg
(
   MsgCode              varchar(16) not null comment '消息主键自定义，通过S_M_INNERMSG序列生成',
   Sender               varchar(128),
   SendDate             date,
   MsgTitle             varchar(128),
   MsgType              char(1) comment 'P= 个人为消息  A= 机构为公告（通知）
            M=邮件',
   MailType             char(1) comment 'I=收件箱
            O=发件箱
            D=草稿箱
            T=废件箱
            
            
            ',
   MailUnDelType        char(1),
   ReceiveName          varchar(2048) comment '使用部门，个人中文名，中间使用英文分号分割',
   HoldUsers            numeric(8,0) comment '总数为发送人和接收人数量相加，发送和接收人删除消息时-1，当数量为0时真正删除此条记录
            
            消息类型为邮件时不需要设置',
   msgState             char(1) comment '未读/已读/删除',
   msgContent           longblob,
   EmailId              varchar(8) comment '用户配置多邮箱时使用',
   OptID                varchar(64) not null comment '模块，或者表',
   OPTMethod            varchar(64) comment '方法，或者字段',
   optTag               varchar(200) comment '一般用于关联到业务主体',
   primary key (MsgCode)
);

alter table M_InnerMsg comment '内部消息与公告
接受代码,  其实可以独立出来, 因为他 和发送人 是 一对多的关系

                               -&#';

/*==============================================================*/
/* Table: M_InnerMsg_Recipient                                  */
/*==============================================================*/
create table M_InnerMsg_Recipient
(
   MsgCode              varchar(16) not null,
   Receive              varchar(8) not null,
   ReplyMsgCode         int,
   ReceiveType          char(1) comment 'P=个人为消息
            A=机构为公告
            M=邮件',
   MailType             char(1) comment 'T=收件人
            C=抄送
            B=密送',
   msgState             char(1) comment '未读/已读/删除，收件人在线时弹出提示
            
            U=未读
            R=已读
            D=删除',
   ID                   varchar(16) not null,
   primary key (ID)
);

alter table M_InnerMsg_Recipient comment '内部消息（邮件）与公告收件人及消息信息';

/*==============================================================*/
/* Table: M_MsgAnnex                                            */
/*==============================================================*/
create table M_MsgAnnex
(
   MsgCode              varchar(16) not null,
   InfoCode             varchar(16) not null,
   MsgAnnexId           varchar(16) not null,
   primary key (MsgAnnexId)
);

/*==============================================================*/
/* Table: P_TASK_LIST                                           */
/*==============================================================*/
create table P_TASK_LIST
(
   taskid               numeric(12,0) not null comment '自动生成的主键，需要一个序列来配合',
   taskowner            varchar(8) not null comment '谁的任务',
   tasktag              varchar(1) not null comment '类似与outlook中的邮件标记，可以用不同的颜色的旗子图表标识',
   taskrank             varchar(1) not null comment '任务的优先级',
   taskstatus           varchar(2) not null comment '处理中、完成、取消、终止',
   tasktitle            varchar(256) not null,
   taskmemo             varchar(1000) comment '简要描述任务的具体内容',
   tasktype             varchar(8) not null comment '个人、组织活动、领导委派 等等',
   OptID                varchar(64) not null comment '模块，或者表',
   OPTMethod            varchar(64) comment '方法，或者字段',
   optTag               varchar(200) comment '一般用于关联到业务主体',
   creator              varchar(8) not null,
   created              date not null,
   planbegintime        date not null,
   planendtime          date,
   begintime            date,
   endtime              date,
   finishmemo           varchar(1000) comment '简要记录任务的执行过程和结果',
   noticeSign           varchar(1) comment '提醒标志为：禁止提醒、未提醒、已提醒',
   lastNoticeTime       date comment '最后一次提醒时间，根据提醒策略可以提醒多次',
   taskdeadline         date,
   taskvalue            varchar(2048) comment '备用，字段不够时使用',
   primary key (taskid)
);

