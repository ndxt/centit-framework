--oracle
---方案一
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
 
---方案二
alter table F_DATACATALOG modify FieldDesc varchar2(1024);
alter table F_UNITINFO add UNITPATH VARCHAR2(1000);

update f_unitinfo t set t.unitpath = (
select b.UnitPath from (
select level as hi_level,
               t.unitcode,
               t.parentunit,
               CONNECT_BY_ROOT t.unitcode as topunitcode,
               SYS_CONNECT_BY_PATH(t.unitcode, '/') UnitPath
          from f_unitinfo t 
          start with t.parentunit is null or not exists (select f.* from f_unitinfo f where f.unitcode=t.parentunit)
connect by prior unitcode = parentunit) b 
where b.unitcode=t.unitcode);


create or replace view v_hi_unitinfo as
select b.unitcode as topunitcode,  a.unitcode,a.unittype, a.parentunit, a.isvalid,
       a.unitname,a.unitdesc,a.unitshortname,a.addrbookid,a.unitorder,a.depno,
       a.unitword,a.unitgrade,
       LENGTHB(b.UnitPath)- LENGTHB(REPLACE(b.UnitPath,'/','')) - LENGTHB(a.UnitPath) + LENGTHB(REPLACE(a.UnitPath,'/',''))  as hi_level,
       substrb(b.UnitPath ,  LENGTHB(a.UnitPath)) as UnitPath
  from f_unitinfo a , f_unitinfo b
 where b.UnitPath like a.UnitPath||'/%'; 
 
 
 
