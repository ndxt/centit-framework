
/*==============================================================*/
/* View: v_hi_unitinfo                                          */
/*==============================================================*/
/*
create or replace view v_hi_unitinfo as
select b.topunitcode as topunitcode, a.UNITCODE,a.UNITTYPE, a.PARENTUNIT, a.ISVALID,
       a.UNITNAME,a.UNITDESC,a.UNITSHORTNAME,a.ADDRBOOKID,a.unitOrder,
       a.unitWord,a.unitGrade, b.hi_level, UnitPath
  from F_UNITINFO a,
       (select level as hi_level,
               unitcode,
               CONNECT_BY_ROOT unitcode as topunitcode,
               SYS_CONNECT_BY_PATH(unitcode, '/') UnitPath
          from f_unitinfo t
        connect by prior unitcode = parentunit) b
 where a.UNITCODE = b.unitcode;
*/ 
 create view v_hi_unitinfo as
 WITH RPL (LEVEL,UNITCODE,topunitcode,PARENTUNIT,UNITTYPE,ISVALID,
 		UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
 		unitOrder,unitWord,unitGrade,UnitPath) AS 
             (
                    SELECT 1 as LEVEL,UNITCODE,PARENTUNIT as topunitcode,PARENTUNIT,UNITTYPE,ISVALID,
								UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
								 		unitOrder,unitWord,unitGrade,UNITCODE as UnitPath
                    FROM f_Unitinfo 
                    UNION ALL 
                    SELECT PARENT.LEVEL+1 as LEVEL,CHILD.UNITCODE, PARENT.topunitcode ,
                    		CHILD.PARENTUNIT,CHILD.UNITTYPE, CHILD.ISVALID,
                    CHILD.UNITNAME,CHILD.UNITSHORTNAME,CHILD.UNITDESC,CHILD.ADDRBOOKID,
                    CHILD.unitOrder,CHILD.unitWord,CHILD.unitGrade,PARENT.UnitPath + '/'+ CHILD.UNITCODE as UnitPath
                    FROM RPL PARENT, f_Unitinfo CHILD 
                    WHERE PARENT.UNITCODE = CHILD.PARENTUNIT 
             ) 
SELECT LEVEL,UNITCODE,topunitcode,PARENTUNIT,UNITTYPE,ISVALID,
 		UNITNAME,UNITSHORTNAME,UNITDESC,ADDRBOOKID,
 		unitOrder,unitWord,unitGrade,UnitPath
FROM RPL 
ORDER BY LEVEL,unitorder