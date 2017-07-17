delete from f_datadictionary where CATALOGCODE='OptType'
/

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'S', null, null, 'T', '实施业务', 'F', '实施业务', to_date('2015-1-1','YYYY-HH-DD'), to_date('2015-1-1','YYYY-HH-DD'), 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'O', null, null, 'T', '普通业务', 'F', '普通业务', to_date('2015-1-1','YYYY-HH-DD'), to_date('2015-1-1','YYYY-HH-DD'), 1);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'I', null, null, 'T', '项目业务', 'F', '项目业务', to_date('2015-1-1','YYYY-HH-DD'), to_date('2015-1-1','YYYY-HH-DD'), 3);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'W', null, null, 'T', '工作流业务', 'F', '工作流业务', to_date('2015-1-1','YYYY-HH-DD'), to_date('2015-1-1','YYYY-HH-DD'), 4);


update f_optinfo set opttype='O' where opttype='S';
update f_optinfo set opttype='S' where opttype='I';

commit
/
