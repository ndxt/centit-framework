-- 删除数据不一致的地方
delete from f_optdef where optid not in(select optid from f_optinfo)
/

delete from f_rolepower where rolecode not in(select rolecode from f_roleinfo)
/

commit
/
-- 检查业务系统设置一致性
update f_optinfo set preoptid='0' 
where preoptid is null or
	( preoptid <> '0' 
		and preoptid not in(select optid from f_optinfo))
/

update f_optinfo set optroute = '...' 
	where optroute is null or 
		( optroute != '...' 
			and optid in (select preoptid from f_optinfo))
/

commit
/

