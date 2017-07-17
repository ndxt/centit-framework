
--v_hi_unitinfo
create or replace view v_hi_unitinfo as
select b.topunitcode as topunitcode,  a.unitcode,a.unittype, a.parentunit, a.isvalid,
       a.unitname,a.unitdesc,a.unitshortname,a.addrbookid,a.unitorder,a.depno,
       a.unitword,a.unitgrade, b.hi_level,b.UnitPath
  from f_unitinfo a,
       (select level as hi_level,
               unitcode,
               CONNECT_BY_ROOT unitcode as topunitcode,
               SYS_CONNECT_BY_PATH(unitcode, '/') UnitPath
          from f_unitinfo t
        connect by prior unitcode = parentunit) b
 where a.unitcode = b.unitcode;
 
 
create or replace view v_opt_tree  as
select
   MENU_ID,
   PARENT_ID,
   MENU_NAME,
   ORDERIND
from
   (select i.optid as MENU_ID,i.preoptid as PARENT_ID,i.optname as MENU_NAME,i.orderind from f_optinfo i where i.isintoolbar ='Y') union all (select d.optcode as MENU_ID,d.optid as PARENT_ID,d.optname as MENU_NAME,0 as orderind from f_optdef d)
order by
   orderind ASC
/

create or replace view F_V_Opt_Role_Map  as
select c.opturl || b.opturl as opturl, b.optreq, a.rolecode, c.optid, b.optcode
  from F_ROLEPOWER a
  join F_OPTDEF b
    on (a.optcode = b.optcode)
  join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...'
 order by c.opturl, b.optreq, a.rolecode
/


create or replace view f_v_optdef_url_map as
select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
       b.optdesc,b.optMethod , c.optid,b.OptName
from F_OPTDEF b join f_optinfo c
    on (b.optid = c.optid)
 where c.OptType <> 'W'
   and c.opturl <> '...' and b.optreq is not null
/

/*==============================================================*/
/* View: F_V_UserOptDataScopes                                  */
/*==============================================================*/
create or replace view F_V_UserOptDataScopes as
select  distinct a.UserCode, c. OPTID ,  c.OPTMETHOD , b.optScopeCodes
from F_V_USERROLES a  join F_ROLEPOWER   b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
--with read only;
/

create or replace view f_v_useroptmoudlelist as
select  distinct a.UserCode,d.OptID, d.OptName , d.PreOptID  ,
            d.FormCode ,d.optroute, d.opturl, d.optroute, d.MsgNo , d.MsgPrm, d.IsInToolBar ,
            d.ImgIndex,d.TopOptID ,d.OrderInd,d.PageType,d.opttype
from F_V_USERROLES a  join F_ROLEPOWER b on (a.RoleCode=b.RoleCode)
         join F_OPTDEF  c on(b.OPTCODE=c.OPTCODE)
        join F_OptInfo d on(c.OPTID=d.OptID)
where d.opturl<>'...';