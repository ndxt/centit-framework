alter table F_OPTDEF modify OPTCODE varchar2(32)
/
alter table F_OPTDEF modify OPTID varchar2(32)
/
alter table F_OPTDEF modify OptName varchar2(100)
/

alter table F_OPTINFO modify PREOPTID varchar2(32)
/
alter table F_OPTINFO modify OPTID varchar2(32)
/
alter table F_OPTINFO modify OptName varchar2(100)
/


alter table F_ROLEINFO modify ROLENAME varchar2(300)
/
alter table F_ROLEINFO add ROLETYPE CHAR
/
alter table F_ROLEINFO add UNITCODE varchar2(32)
/

alter table F_ROLEPOWER modify OPTCODE varchar2(32)
/

alter table F_UNITINFO modify UNITCODE varchar2(32)
/
alter table F_UNITINFO add UNITTAG varchar2(100)
/

alter table F_UNITINFO modify PARENTUNIT varchar2(32)
/
alter table F_UNITINFO modify UNITNAME varchar2(300)
/

alter table F_UNITINFO modify DEPNO varchar2(100)
/
alter table F_UNITINFO modify UNITWORD varchar2(100)
/
alter table F_UNITINFO modify ENGLISHNAME varchar2(300)
/

alter table F_USERINFO modify USERCODE varchar2(32)
/
alter table F_USERINFO add USERTAG varchar2(100)
/
alter table F_USERINFO modify USERNAME varchar2(300)
/
alter table F_USERINFO modify LOGINNAME varchar2(100)
/
alter table F_USERINFO modify ENGLISHNAME varchar2(300)
/
alter table F_USERINFO modify primaryUnit varchar2(32)
/
alter table F_USERINFO modify userWord varchar2(100)
/


alter table F_USERROLE modify USERCODE varchar2(32)
/

alter table F_USERROLE modify ROLECODE varchar2(32)
/

