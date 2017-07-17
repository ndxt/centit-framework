--mysql
alter table F_DATACATALOG modify FieldDesc varchar(1024);
alter table F_UNITINFO add UNITPATH VARCHAR(1000);

DELIMITER $$

CREATE FUNCTION calcUnitPath (chrId varchar(32)) 
	RETURNS varchar(1000) 
BEGIN
   DECLARE sTemp VARCHAR(32);
   DECLARE sPreTemp VARCHAR(32);
   DECLARE path VARCHAR(1000);
   DECLARE rs VARCHAR(1000);   
   SET  sTemp = trim(chrId);
   SET  path = '';
   REPEAT
   	  SET  path = concat('/',sTemp, path);
   	  set sPreTemp = sTemp;
      SELECT unitcode INTO sTemp 
         FROM f_unitinfo  
         where unitcode = 
         		(select parentunit FROM f_unitinfo where unitcode = sTemp);
      until sTemp is null or sTemp='' or sPreTemp = sTemp
   END REPEAT;
  
   RETURN path;
END$$

DELIMITER ;

SET SQL_SAFE_UPDATES = 0;

--手动更新UnitPath值
update f_unitinfo t set t.UnitPath = calcUnitPath(t.unitcode);

--v_hi_unitinfo视图脚本

CREATE OR REPLACE VIEW v_hi_unitinfo AS
SELECT a.unitcode AS topunitcode,  b.unitcode,b.unittype, b.parentunit, b.isvalid,     b.unitname,b.unitdesc,b.unitshortname,b.addrbookid,b.unitorder,b.depno,
       b.unitword,b.unitgrade,
       LENGTH(b.UnitPath)- LENGTH(REPLACE(b.UnitPath,'/','')) - LENGTH(a.UnitPath) + LENGTH(REPLACE(a.UnitPath,'/',''))+1  AS hi_level,
       substr(b.UnitPath ,  LENGTH(a.UnitPath)+1) AS UnitPath
  FROM f_unitinfo a , f_unitinfo b
 WHERE b.UnitPath LIKE CONCAT(a.UnitPath,'%' );