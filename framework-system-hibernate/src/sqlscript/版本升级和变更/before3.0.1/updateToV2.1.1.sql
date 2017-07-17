alter table F_USERROLE
  drop constraint PK_WFUSERROLE cascade
/
  
delete from  F_USERROLE where (usercode,rolecode,OBTAINDATE) not in (
select usercode,rolecode,min(OBTAINDATE) from F_USERROLE group by usercode,rolecode)
/

alter table F_USERROLE
  add constraint PK_USERROLE_ID primary key (USERCODE, ROLECODE)
/

create or replace view f_v_userroles as
select distinct b.ROLECODE, b.ROLENAME, b.ISVALID, b.ROLEDESC, a.usercode, a.obtaindate
  from F_USERROLE a
  join F_ROLEINFO b
    on (a.ROLECODE = b.ROLECODE)
 where a.OBTAINDATE <= sysdate  
   and b.ISVALID = 'T'
union all
select d.ROLECODE, d.ROLENAME, d.ISVALID, d.ROLEDESC, c.usercode, a.obtaindate
  from f_userinfo c, F_ROLEINFO d, F_USERROLE a
 where c.usercode = a.usercode
 and d.rolecode = a.rolecode and
  d.rolecode = 'G-public'
/

alter table F_USERUNIT
  drop constraint PK_F_USERUNIT cascade
/

create sequence S_USER_UNIT_ID;

-- Add/modify columns 
alter table F_USERUNIT add USERUNITID VARCHAR2(16)
/

update F_USERUNIT set USERUNITID = S_USER_UNIT_ID.nextVal
/

alter table F_USERUNIT add USERORDER number(8) default 0
/

alter table F_USERUNIT
  add constraint PK_USERUNIT_ID primary key (USERUNITID)
/

drop table F_OPTDATASCOPE cascade constraints;

/*==============================================================*/
/* Table: F_OPTDATASCOPE                                        */
/*==============================================================*/
create table F_OPTDATASCOPE  (
   optScopeCode         VARCHAR2(16)                    not null,
   OptID                VARCHAR2(16),
   scopeName            VARCHAR2(64),
   FilterCondition      VARCHAR2(1024),
   --FilterGroup          VARCHAR2(16)                   default 'G',
   scopeMemo            VARCHAR2(1024),
   constraint PK_F_OPTDATASCOPE primary key (optScopeCode)
);

comment on column F_OPTDATASCOPE.FilterCondition is
'条件语句，可以有的参数 [mt] 业务表 [uc] 用户代码 [uu] 用户机构代码';

comment on column F_OPTDATASCOPE.scopeMemo is
'数据权限说明';

alter table f_userinfo add pwdExpiredTime       date
/

alter table f_userinfo add PRIMARYUNIT varchar2(6)
/

update  f_userinfo a set a.PRIMARYUNIT = (select min(b.unitcode) from f_userunit b where b.usercode=a.usercode)
/

/*
alter table f_userinfo add englishName varchar2(50);
alter table f_userinfo add extJsonInfo varchar2(1000);

alter table f_unitinfo add englishName varchar2(50);
alter table f_unitinfo add extJsonInfo varchar2(1000);
*/

alter table F_DATADICTIONARY modify datavalue VARCHAR2(2048)
/

alter table F_ROLEPOWER add optScopeCodes  VARCHAR2(200)
/


drop table F_OPT_LOG cascade constraints;

/*==============================================================*/
/* Table: F_OPT_LOG                                             */
/*==============================================================*/
create table F_OPT_LOG  (
   logId                NUMBER(12,0)                    not null,
   logLevel             VARCHAR2(2)                     not null,
   usercode             VARCHAR2(8)                     not null,
   opttime              DATE                            not null,
   OptContent           VARCHAR2(1000)                  not null,
   newValue             CLOB,
   OldValue             CLOB,
   OptID                VARCHAR2(64)                    not null,
   OPTMethod            VARCHAR2(64),
   optTag               VARCHAR2(200),
   constraint PK_F_OPT_LOG primary key (logId)
);




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
   Select_JSON          VARCHAR2(2000),
   CREATEDATE           date                           default sysdate,
   constraint PK_F_QUERY_FILTER_CONDITION primary key (CONDITION_NO)
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

/*==============================================================*/
/* Index: Ind_Filter_Table_Class_Name                           */
/*==============================================================*/
create index Ind_Filter_Table_Class_Name on F_QUERY_FILTER_CONDITION (
   Table_Class_Name ASC
);


drop index Ind_query_filter_modle_code;

drop table F_USER_QUERY_FILTER cascade constraints;

/*==============================================================*/
/* Table: F_USER_QUERY_FILTER                                   */
/*==============================================================*/
create table F_USER_QUERY_FILTER  (
   FILTER_NO            NUMBER(12,0)                    not null,
   userCode             varchar2(8)                     not null,
   modle_code           varchar2(64)                    not null,
   filter_name          varchar2(200)                   not null,
   filter_value         varchar2(3200)                  not null,
   is_Default       	CHAR,
   CREATEDATE           date                           default sysdate,
   constraint PK_F_USER_QUERY_FILTER primary key (FILTER_NO)
);

comment on column F_USER_QUERY_FILTER.modle_code is
'开发人员自行定义，单不能重复，建议用系统的模块名加上当前的操作方法';

comment on column F_USER_QUERY_FILTER.filter_name is
'用户自行定义的名称';

comment on column F_USER_QUERY_FILTER.filter_value is
'变量值，json格式，对应一个map';

/*==============================================================*/
/* Index: Ind_query_filter_modle_code                           */
/*==============================================================*/
create index Ind_query_filter_modle_code on F_USER_QUERY_FILTER (
   modle_code ASC
);
