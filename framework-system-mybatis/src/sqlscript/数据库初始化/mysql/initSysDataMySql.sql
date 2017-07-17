-- mysql -u tfzc -D tfzcpt -p <D:\Projects\j2eews\tfzcpt\src\sqlScript\sysDataInit.sql

--创建用户
delete from f_userinfo;

insert into f_userinfo (USERCODE, USERPIN, ISVALID, LOGINNAME, USERNAME, USERDESC, LOGINTIMES, ACTIVETIME, LOGINIP, ADDRBOOKID, REGEMAIL, USERORDER, USERPWD, REGCELLPHONE, CREATEDATE,CREATOR,UPDATOR,UPDATEDATE)
values ('noname', '$2a$11$xLOqxWXU6laDFfbiHP/vmOCEHGXzawFJ5ZSRARTvA1ipUwS5m9lPS', 'F', 'noname', '匿名用户', '匿名用户', null, null, '', null, 'noname@centit.com', 1, '', '', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000',now());
insert into f_userinfo (USERCODE, USERPIN, ISVALID, LOGINNAME, USERNAME, USERDESC, LOGINTIMES, ACTIVETIME, LOGINIP, ADDRBOOKID, REGEMAIL, USERORDER, USERPWD, REGCELLPHONE, CREATEDATE ,CREATOR,UPDATOR,UPDATEDATE)
values ('u0000000', '$2a$11$DbyFNhHeCES5CKoMuM5sXepY7GM35sZkUSqQbjYJnFTzJ2GDIYGLK', 'T', 'admin', '管理员', '', null, null, '', null, 'codefan@centit.com', 1, '', '18017458877', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000',now());

--初始化数据字典
--str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s')
insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('MsgType', '消息类型', 'U', 'L', '测试测试', null, str_to_date('25-02-2016 17:55:21', '%d-%m-%Y %H:%i:%s'), null, 'innermsg', '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('CatalogStyle', '字典类型', 'S', 'L', 'F : 框架固有的 U:用户 S：系统', null, null, null, 'dictionary', '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('CatalogType', '字典结构', 'S', 'L', 'L:列表T:树形 测试修改', null, str_to_date('01-12-2015 11:41:23', '%d-%m-%Y %H:%i:%s'), null, 'dictionary', '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('UnitType', '单位类型', 'U', 'L', '单位类型', null, null, null, null, '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('OptType', '业务类别', 'S', 'L', '业务类别', '业务类别', null, null, '1', '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('StationType', '岗位角色', 'U', 'L', '业务岗位类别，类别代码长度为4', '业务类别xx', null, null, '0', '0','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('RankType', '行政职务类别', 'U', 'L', '业务职务类别，类别代码长度为2。数值越低等级越高', '职位代码;等级;未用;职位名称', null, null, null, '0','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('SUPPORT_LANG', '系统支持的语言', 'U', 'L', '系统支持的语言,需要在system.properties中把参数sys.multi_lang设置为true才会生效', null, str_to_date('28-01-2016 20:33:23', '%d-%m-%Y %H:%i:%s'), null, 'DICTSET', '1','u0000000','u0000000');

insert into f_datacatalog (CATALOGCODE, CATALOGNAME, CATALOGSTYLE, CATALOGTYPE, CATALOGDESC, FIELDDESC, UPDATEDATE, CREATEDATE, OPTID, NEEDCACHE,CREATOR,UPDATOR)
values ('LogLevel', '日志类型', 'F', 'L', '日志类型', '日志类型', str_to_date('07-04-2016', '%d-%m-%Y'), str_to_date('07-04-2016', '%d-%m-%Y'), 'OptLog', '1','u0000000','u0000000');


insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('MsgType', 'P', null, null, 'T', '个人消息', 'U', null, str_to_date('25-02-2016 17:55:21', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('MsgType', 'A', null, null, 'T', '公告', 'U', '给部门群发的消息', str_to_date('25-02-2016 17:55:21', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogStyle', 'F', null, null, 'T', '框架固有', 'S', '任何地方都不允许编辑，只能有开发人员给出更新脚本添加、更改和删除', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogStyle', 'G', null, null, 'T', '国标', 'S', '这个暂时不考虑可以在字典类别中进行描述', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogStyle', 'S', null, null, 'T', '系统参数', 'S', '实施人员可以在实施入口对数据字典的类别和字典条目进行CRUD操作', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogStyle', 'U', null, null, 'T', '用户参数', 'S', '管理员入口 和 实施人员入口 都 对这类别字典类别和条目进行CRUD', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogType', 'L', null, null, 'T', '列表', 'S', '列表', str_to_date('01-12-2015 11:41:23', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('CatalogType', 'T', null, null, 'T', '树形', 'S', '树形', str_to_date('01-12-2015 11:41:23', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('UnitType', 'A', 'CCCC', null, 'T', '管理', 'S', 'administrator', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('UnitType', 'L', 'BBB', null, 'T', '后勤', 'S', 'logistics', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('UnitType', 'O', 'DDD', null, 'T', '业务', 'S', 'operator', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('UnitType', 'R', 'A', null, 'T', '研发', 'S', 'Rearch', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'IM', '3', null, 'T', '项目经理', 'U', '项目经理', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'GM', '1', null, 'T', '总经理', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'DM', '2', null, 'T', '部门经理', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'EM', '9', null, 'T', '员工', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'KZ', '10', null, 'T', '科长', null, '科长', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'ZR', '10', null, 'T', '主任', null, '主任', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'KY', '10', null, 'T', '办公室科员', null, '办公室科员', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'CZ', '10', null, 'T', '处长', null, '处长', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'WDZ', '4', null, 'T', '委党组', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'WLD', '5', null, 'T', '委领导', null, '委领导', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'FC', '6', null, 'T', '副处长', null, '副处长', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'JJZ', '7', null, 'T', '纪检组', null, '纪检组', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('RankType', 'JGDW', '8', null, 'T', '机关党委', null, '机关党委', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'slbmfzr', null, null, 'T', '受理部门负责人', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'fgjz', null, null, 'T', '分管局长', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'sfy', null, null, 'T', '收费员', null, null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'xbr', null, null, 'T', '协办处室负责人', null, '协办处室负责人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'bgszr', null, null, 'T', '办公室主任', null, '办公室主任', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'xkdj', null, null, 'T', '许可登记', 'S', '许可登记', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'xkbl', null, null, 'T', '许可办理', 'S', '许可办理', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'dcdb_jcry', null, null, 'T', '督查督办_监察人员', 'S', '发起督办的监察人员', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'dcdb_jbrld', null, null, 'T', '督查督办_经办人领导', 'S', '被督办人的分管领导', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'dcdb_jcld', null, null, 'T', '督查督办_监察领导', 'S', '发起督办的监察人员分管领导', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'dcdb_jbr', null, null, 'T', '督查督办_经办人', 'S', '被督办人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'fzr', null, null, 'T', '主办处室负责人', null, '主办处室负责人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'sjr', null, null, 'T', '办公室收件人', null, '办公室收件人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'pf', null, null, 'T', '办公室批分人', null, '办公室批分人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'cbr', null, null, 'T', '主办承办人', null, '主办承办人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'syr', null, null, 'T', '办公室审阅人', null, '办公室审阅人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'sr', null, null, 'T', '办公室人员', null, '办公室人员', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'fgzr', null, null, 'T', '分管主任', null, '分管主任', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'fw_nw', null, null, 'T', '发文拟文', 'S', null, null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'wysry', null, null, 'T', '文印室人员', null, '文印室人员', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'wyszr', null, null, 'T', '文印室主任', null, '文印室主任', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'spcjbr', null, null, 'T', '审批处经办人', 'S', '审批处经办人', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'spccz', null, null, 'T', '审批处处长', 'S', '审批处处长', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'bgsms', null, null, 'T', '办公室秘书', 'S', '办公室秘书', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'bgsfgzr', null, null, 'T', '办公室分管主任', 'S', '办公室分管主任', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'wld', null, null, 'T', '委领导签发', 'S', '委领导签发', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('StationType', 'bgsfwh', null, null, 'T', '办公室文秘室文号', 'S', '办公室文秘室文号', null, null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('SUPPORT_LANG', 'zh_CN', null, null, 'T', '中文', 'U', null, str_to_date('28-01-2016 20:33:23', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('SUPPORT_LANG', 'en_US', null, null, 'T', 'English', 'U', null, str_to_date('28-01-2016 20:33:23', '%d-%m-%Y %H:%i:%s'), null, null);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'S', null, null, 'T', '实施业务', 'F', '实施业务', str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'O', null, null, 'T', '普通业务', 'F', '普通业务', str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), 1);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'I', null, null, 'T', '项目业务', 'F', '项目业务', str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), 3);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('OptType', 'W', null, null, 'T', '工作流业务', 'F', '工作流业务', str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), str_to_date('01-04-2015 01:00:00', '%d-%m-%Y %H:%i:%s'), 4);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LogLevel', '1', null, null, 'T', '错误提示', 'F', null, str_to_date('07-04-2016', '%d-%m-%Y'), str_to_date('07-04-2016', '%d-%m-%Y'), 2);

insert into f_datadictionary (CATALOGCODE, DATACODE, EXTRACODE, EXTRACODE2, DATATAG, DATAVALUE, DATASTYLE, DATADESC, LASTMODIFYDATE, CREATEDATE, DATAORDER)
values ('LogLevel', '0', null, null, 'T', '操作日志', 'F', null, str_to_date('07-04-2016', '%d-%m-%Y'), str_to_date('07-04-2016', '%d-%m-%Y'), 1);


--初始化业务菜单
insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DEPTMAG', '部门管理', '0', '...', '...', null, 'O', null, null, 'Y', null, null, null, null, 'I', 'icon-base icon-base-computer', null, null, str_to_date('12-01-2016 17:04:01', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DEPTPOW', '下属部门管理', 'DEPTMAG', 'modules/sys/deptpow/deptpow.html', '/system/deptManager', null, 'O', null, null, 'Y', null, null, 0, null, 'D', 'icon-base icon-base-user', null, null, str_to_date('12-01-2016 17:10:45', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DEPTROLE', '部门角色定义', 'DEPTMAG', 'modules/sys/deptrole/deptrole.html', '/system/deptManager!', null, 'O', null, null, 'Y', null, null, 0, null, 'D', 'icon-base icon-base-gear', null, null, str_to_date('12-01-2016 17:10:41', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DEPTUSERINFO', '部门用户管理', 'DEPTMAG', 'modules/sys/deptuserinfo/deptuserinfo.html', '/system/userDef', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, str_to_date('12-01-2016 17:11:02', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('UNITMAG', '机构管理', 'ORGMAG', 'modules/sys/unitinfo/unitinfo.html', '/system/unitinfo', null, 'O', null, null, 'Y', null, null, 2, null, 'D', null, null, null, str_to_date('14-03-2016 14:41:07', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('USERMAG', '用户管理', 'ORGMAG', 'modules/sys/userinfo/userinfo.html', '/system/userinfo', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, str_to_date('18-02-2016 17:46:43', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('USERROLE', '用户角色', 'ORGMAG', '/modules/sys/userrole.html', '/system/userrole', null, 'O', null, null, 'N', null, null, null, null, 'D', null, null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('USERUNIT', '用户机构', 'ORGMAG', '/modules/sys/userunit.html', '/system/userunit', null, 'O', null, null, 'N', null, null, null, null, 'D', null, null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DICTSET_M', '数据字典', 'SYS_CONFIG', 'modules/sys/dictionary/dictionary.html', '/system/dictionary', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), str_to_date('23-12-2014 14:04:59', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('OPT_LOG_QUERY', '系统日志', 'SYS_CONFIG', 'modules/sys/loginfo/loginfo.html', '/system/optlog', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, str_to_date('27-11-2015 11:19:09', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('USER_SETTING', '设置中心', 'SYS_CONFIG', '/modules/sys/usersetting.html', '/system/usersetting', null, 'N', null, null, 'N', null, null, null, null, 'D', null, null, null, str_to_date('23-12-2014 16:52:40', '%d-%m-%Y %H:%i:%s'), str_to_date('23-12-2014 16:52:40', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('CALENDAR', '日历', 'SYS_CONFIG', '/modules/sys/schedule/schedule.html', '/system/calendar', null, 'O', null, null, 'Y', null, null, null, null, 'D', 'icon-base icon-base-calendar', null, null, str_to_date('04-03-2015 09:55:31', '%d-%m-%Y %H:%i:%s'), str_to_date('04-03-2015 09:55:31', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DEPLOY', '实施菜单', '0', '...', '...', null, 'S', null, null, 'Y', null, null, null, null, 'D', '444', null, null, str_to_date('15-12-2014 14:10:08', '%d-%m-%Y %H:%i:%s'), str_to_date('15-12-2014 14:10:08', '%d-%m-%Y %H:%i:%s'),'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('SYSCONF', '系统配置', 'DEPLOY', '...', '...', null, 'O', null, null, 'Y', null, null, null, null, 'I', 'icon-base icon-base-gear', null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('DICTSET', '数据字典管理', 'SYSCONF', 'modules/sys/dictionary/dictionary.admin.html', '/system/dictionary', null, 'S', null, null, 'Y', null, null, 0, null, 'D', 'icon-base icon-base-gear', null, null, str_to_date('18-02-2016 17:48:18', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('OPTINFO', '系统业务', 'SYSCONF', 'modules/sys/optinfo/optinfo.html', '/system/optinfo', null, 'S', null, null, 'Y', null, null, 4, null, 'D', null, null, null, str_to_date('30-01-2016 19:50:37', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('OPTLOG', '系统日志', 'SYSCONF', 'modules/sys/loginfo/loginfo.admin.html', '/system/optlog', null, 'S', null, null, 'Y', null, null, null, null, 'D', null, null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('ROLEMAG', '角色定义', 'ORGMAG', 'modules/sys/roleinfo/roleinfo.html', '/system/roleinfo', null, 'O', null, null, 'Y', null, null, null, null, 'D', null, null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('ORGMAG', '组织管理', '0', '...', '...', null, 'O', null, null, 'Y', null, null, 3, null, 'I', 'icon-base icon-base-user', null, null, str_to_date('31-01-2016 15:55:53', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('SYS_CONFIG', '系统配置', '0', '...', '...', null, 'O', null, null, 'Y', null, null, null, null, 'D', 'icon-base icon-base-gear', null, null, null, null,'u0000000','u0000000');

insert into f_optinfo (OPTID, OPTNAME, PREOPTID, OPTROUTE, OPTURL, FORMCODE, OPTTYPE, MSGNO, MSGPRM, ISINTOOLBAR, IMGINDEX, TOPOPTID, ORDERIND, FLOWCODE, PAGETYPE, ICON, HEIGHT, WIDTH, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('LOGINCAS', 'CAS登录入口', '0', '/system/mainframe/logincas', '/system/mainframe', null, 'O', null, null, 'N', null, null, null, null, 'D', null, null, null, str_to_date('07-04-2016 15:06:08', '%d-%m-%Y %H:%i:%s'), null,'u0000000','u0000000');


insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000038', 'USER_SETTING', '新增或更新用户设置', null, null, null, null, null, '/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000037', 'USER_SETTING', '获取用户设置参数', null, '获取当前用户设置的参数', null, null, null, '/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000039', 'USER_SETTING', '删除用户设置参数', null, '删除用户设置参数', null, null, null, '/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000040', 'USER_SETTING', '导出用户设置参数', null, '导出用户设置参数', null, null, null, '/export', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000051', 'USERUNIT', '获取单个用户机构关联信息', null, '获取单个用户机构关联信息', null, null, null, '/*/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000020', 'OPTLOG', '查询', 'list', '查询系统日志', null, null, null, '/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000041', 'OPT_LOG_QUERY', '查看日志详情', null, '查看单条日志', null, str_to_date('27-11-2015 11:19:09', '%d-%m-%Y %H:%i:%s'), null, '/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000070', 'USERMAG', '用户列表', null, '用户列表', null, str_to_date('18-02-2016 17:46:43', '%d-%m-%Y %H:%i:%s'), null, '/', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000029', 'USERUNIT', '新增用户机构关联', null, '添加用户关联机构', null, null, null, '/', 'C','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000032', 'USERROLE', '新增用户角色关联', null, '添加用户关联角色', null, null, null, '/', 'C','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000030', 'USERUNIT', '编辑用户机构关联', null, '更新用户机构关联信息', null, null, null, '/*/*', 'U','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000031', 'USERUNIT', '删除用户机构关联', null, '删除用户关联机构关联', null, null, null, '/*/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000033', 'USERROLE', '编辑用户角色关联', null, '更新用户关联角色信息', null, null, null, '/*/*', 'U','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000034', 'USERROLE', '删除用户角色关联', null, '删除用户关联角色', null, null, null, '/*/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000071', 'USERMAG', '创建用户', null, '创建用户', null, str_to_date('18-02-2016 17:46:44', '%d-%m-%Y %H:%i:%s'), null, '/', 'C','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000072', 'USERMAG', '更新用户', null, '更新用户', null, str_to_date('18-02-2016 17:46:44', '%d-%m-%Y %H:%i:%s'), null, '/*', 'U','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000073', 'USERMAG', '删除用户', null, '删除用户', null, str_to_date('18-02-2016 17:46:44', '%d-%m-%Y %H:%i:%s'), null, '/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000052', 'DICTSET', '查询单个数据目录', null, '查询单个数据目录', null, str_to_date('18-02-2016 17:48:18', '%d-%m-%Y %H:%i:%s'), null, '/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000053', 'DICTSET', '查询单个数据字典', null, '查询单个数据字典', null, str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/dictionary/*/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000054', 'DICTSET', '获取缓存中所有数据字典', null, '获取缓存中所有数据字典', null, str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/cache/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000009', 'DICTSET', '删除数据字典', 'DELTE', '删除数据字典', 'F', str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/dictionary/*/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000007', 'DICTSET', '列举字典', 'LIST', '初始页面', 'F', str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/data', 'U','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000008', 'DICTSET', '新增/编辑数据字典', 'EDIT', '新增/编辑数据字典', 'F', str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/dictionary/*/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000010', 'DICTSET', '新增/编辑数据目录', 'editDetail', '编辑/新建数据目录', 'F', str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000011', 'DICTSET', '删除数据目录', 'deleteDetail', '删除字典目录', 'F', str_to_date('18-02-2016 17:48:19', '%d-%m-%Y %H:%i:%s'), null, '/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000042', 'DICTSET_M', '查询单个数据目录', null, '查询单个数据目录', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000043', 'DICTSET_M', '查询单个数据字典', null, '查询单个数据字典', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*/dictionary/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000044', 'DICTSET_M', '新增/编辑数据目录', null, '新增/编辑数据目录', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000045', 'DICTSET_M', '新增/编辑数据字典', null, '新增/编辑数据字典', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*/dictionary/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000046', 'DICTSET_M', '删除数据目录', null, '删除数据目录', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000047', 'DICTSET_M', '删除数据字典', null, '删除数据字典', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/*/dictionary/*', 'D','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000048', 'DICTSET_M', '获取缓存中所有数据字典', null, '获取缓存中所有数据字典', null, str_to_date('30-01-2016 19:53:38', '%d-%m-%Y %H:%i:%s'), null, '/cache/*', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000059', 'UNITMAG', '查看所有机构', null, '查看所有机构', null, str_to_date('14-03-2016 14:41:07', '%d-%m-%Y %H:%i:%s'), null, '/', 'R','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000001', 'UNITMAG', '新建/编辑机构', 'EDIT', '新建和更新机构', 'F', str_to_date('14-03-2016 14:41:07', '%d-%m-%Y %H:%i:%s'), null, '/*', 'CU','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000002', 'UNITMAG', '禁用/恢复机构', 'DELETE', '更新机构状态', 'F', str_to_date('14-03-2016 14:41:07', '%d-%m-%Y %H:%i:%s'), null, '/*/status/*', 'U','u0000000','u0000000');

insert into f_optdef (OPTCODE, OPTID, OPTNAME, OPTMETHOD, OPTDESC, ISINWORKFLOW, UPDATEDATE, CREATEDATE, OPTURL, OPTREQ,CREATOR,UPDATOR)
values ('1000080', 'LOGINCAS', 'CAS登录入口', null, null, null, null, null, '/logincas', 'RCU','u0000000','u0000000');

-- 用户、角色、权限初始化


--初始化角色信息
--初始化角色信息
insert into f_roleinfo (ROLECODE, ROLENAME,ROLETYPE, ISVALID, ROLEDESC, CREATEDATE, UPDATEDATE,CREATOR,UPDATOR)
values ('G-DEPLOY', '实施人员','S','T', '实施人员角色', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'), now(),'u0000000','u0000000');

insert into f_roleinfo (ROLECODE, ROLENAME,ROLETYPE,  ISVALID, ROLEDESC, CREATEDATE, UPDATEDATE,CREATOR,UPDATOR)
values ('G-SYSADMIN', '系统管理员','S', 'T', '所有系统配置功能', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'), now(),'u0000000','u0000000');

insert into f_roleinfo (ROLECODE, ROLENAME,ROLETYPE,  ISVALID, ROLEDESC, CREATEDATE, UPDATEDATE,CREATOR,UPDATOR)
values ('G-anonymous', '匿名角色','S', 'T', '匿名用户角色', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'), now(),'u0000000','u0000000');

insert into f_roleinfo (ROLECODE, ROLENAME,ROLETYPE,  ISVALID, ROLEDESC, CREATEDATE, UPDATEDATE,CREATOR,UPDATOR)
values ('G-public', '公共角色','S', 'F', '公共角色权限会默认给不包括匿名用户的所有人', str_to_date('12-12-2014 16:05:46', '%d-%m-%Y %H:%i:%s'), now(),'u0000000','u0000000');


------
insert into f_rolepower (ROLECODE, OPTCODE, UPDATEDATE, CREATEDATE, OPTSCOPECODES,CREATOR,UPDATOR)
values ('G-public', '1000080', str_to_date('11-04-2016 10:21:17', '%d-%m-%Y %H:%i:%s'), str_to_date('11-04-2016 10:21:17', '%d-%m-%Y %H:%i:%s'), '','u0000000','u0000000');

SET SQL_SAFE_UPDATES = 0;

insert into f_optdef(optcode,optid,optname,optmethod,optdesc,
			isinworkflow,UPDATEDATE,createdate,opturl,optreq,CREATOR,UPDATOR)
select  sequence_nextval('S_OPTDEFCODE'),optid , '查看', 'list',  '查看',
		'F',now(),now(),'/*','R' ,CREATOR,UPDATOR
		from f_optinfo where optid not in (select optid from f_optdef);

insert into f_rolepower(rolecode,optcode,updateDate,createdate,optscopecodes,CREATOR,UPDATOR)
	select 'G-SYSADMIN',optcode,now(),now(),'',CREATOR,UPDATOR from f_optdef;

insert into f_userrole (USERCODE, ROLECODE, OBTAINDATE, 
			SECEDEDATE, CHANGEDESC, UPDATEDATE, CREATEDATE,CREATOR,UPDATOR)
values ('u0000000', 'G-SYSADMIN', STR_TO_DATE('23-05-2012','%d-%m-%Y'), 
	STR_TO_DATE('01-10-2020', '%d-%m-%Y'),'' ,now(), now(),'u0000000','u0000000');

