/*==============================================================*/
/* DBMS name:      MySql Version 5.7                            */
/* Created on:     2016/4/11 18:13:44                           */
/*==============================================================*/

/*==============================================================*/
/* View: F_V_Opt_Role_Map                                       */
/*==============================================================*/

create or replace view F_V_Opt_Role_Map  as
select concat(c.opturl, b.opturl) as opturl, b.optreq, a.rolecode, c.optid, b.optcode
  from F_ROLEPOWER a
  join F_OPTDEF b
    on (a.optcode = b.optcode)
  join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...'
 order by c.opturl, b.optreq, a.rolecode;



/*==============================================================*/
/* View: F_V_USERROLES                                          */
/*==============================================================*/
create or replace view F_V_USERROLES as
select distinct b.ROLECODE,b.ROLENAME,b.ISVALID,b.ROLEDESC,b.CREATEDATE,b.UPDATEDATE ,a.usercode
    from F_USERROLE a join F_ROLEINFO b on (a.ROLECODE=b.ROLECODE)
    where a.OBTAINDATE <=  now() and (a.SECEDEDATE is null or a.SECEDEDATE > now()) and b.ISVALID='T'
union all
  select d.ROLECODE,d.ROLENAME,d.ISVALID,d.ROLEDESC,d.CREATEDATE,d.UPDATEDATE , c.usercode
   from f_userinfo c , F_ROLEINFO d
   where d.rolecode = 'G-public';

/*==============================================================*/
/* View: F_V_UserOptDataScopes                                  */
/*==============================================================*/
create or replace view F_V_UserOptDataScopes as
select  distinct a.UserCode, c. OPTID ,  c.OPTMETHOD , b.optScopeCodes
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE);
/*==============================================================*/
/* View: F_V_UserOptList                                        */
/*==============================================================*/
create or replace view F_V_UserOptList as
select  distinct a.UserCode,  c.OPTCODE,  c.OPTNAME  ,  c. OPTID ,  c.OPTMETHOD
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE);

/*==============================================================*/
/* View: F_V_UserOptMoudleList                                  */
/*==============================================================*/

create or replace view f_v_useroptmoudlelist as
select  distinct a.UserCode,d.OptID, d.OptName , d.PreOptID  ,
            d.FormCode  , d.opturl, d.optroute, d.MsgNo , d.MsgPrm, d.IsInToolBar ,
            d.ImgIndex,d.TopOptID ,d.OrderInd,d.PageType,d.opttype
from F_V_USERROLES a  join F_ROLEPOWER b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
        join F_OptInfo d on(c.OPTID=d.OptID)
where d.opturl<>'...';

/*==============================================================*/
/* View: f_v_optdef_url_map                                     */
/*==============================================================*/

create or replace view f_v_optdef_url_map as
select concat(c.opturl, b.opturl) as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...' and b.optreq is not null
/

/*==============================================================*/
/* View: v_opt_tree                                             */
/*==============================================================*/
create or replace view v_opt_tree as
   select i.optid as MENU_ID,i.preoptid as PARENT_ID,i.optname as MENU_NAME,i.orderind 
   from f_optinfo i where i.isintoolbar ='Y'
   union all 
   select d.optcode as MENU_ID,d.optid as PARENT_ID,d.optname as MENU_NAME,0 as orderind 
   from f_optdef d
;


/*==============================================================*/
/* View: v_hi_unitinfo  ERROR                                   */
/*==============================================================*/
/*
create or replace view v_hi_unitinfo as
select b.topunitcode as topunitcode, a.UNITCODE,a.UNITTYPE, a.PARENTUNIT, a.ISVALID,
       a.UNITNAME,a.UNITDESC,a.UNITSHORTNAME,a.ADDRBOOKID,a.unitOrder,
       a.unitWord,a.unitGrade, b.hi_level, b.UnitPath
  from F_UNITINFO a,
       (select level as hi_level,
               unitcode,
               CONNECT_BY_ROOT unitcode as topunitcode,
               SYS_CONNECT_BY_PATH(unitcode, '/') UnitPath
          from f_unitinfo t
        connect by prior unitcode = parentunit) b
 where a.UNITCODE = b.unitcode;
*/