--Oracle 
/*添加操作方法的排序功能，便于操作方法作为下拉框选择时排序*/
alter table F_OPTDEF add  optOrder number(6);
/*添加机构管理员*/
alter table F_UNITINFO add UNITMANAGER VARCHAR2(32);

--DB2 mySQL
alter table F_OPTDEF add  optOrder decimal(6);
alter table F_UNITINFO add UNITMANAGER VARCHAR(32);
