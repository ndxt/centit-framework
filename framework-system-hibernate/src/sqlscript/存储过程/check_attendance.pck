create or replace package check_attendance is

  -- Author  : CODEFAN
  -- Created : 2013-8-30 14:26:26
  -- Purpose : 考勤相关存储过程
  
  --计算一个人某日考勤情况
  procedure check_one( checkuser in varchar2,checkDate in date); 
  --计算某日所有人考勤情况
  procedure check_all(checkDate in date); 
  --某人考勤按月汇总
  procedure collect_one(checkuser in varchar2,yearAndMonth in varchar2,forceDeleteOld in number default 0);
  --考勤按月汇总  
  procedure collect_all(yearAndMonth in varchar2);
  --每日自动运行前一日考勤计算
  procedure auto_check;
  --每月开始自动运行上月汇总
  procedure auto_collect;

end check_attendance;
/
create or replace package body check_attendance is

  procedure check_one( checkuser in varchar2,checkDate in date)
  is
    inTime date;
    outTime date;
    shouldInTime date;
    shouldOutTime date;
    beginTime varchar2(6);
    endTime varchar2(6);
    nClassId OA_ARRANGE_DETAIL.CLASS_ID%type;
    ckDate date;
    classType char;
  begin
    ckDate := trunc(checkDate);
    select Class_ID into nClassId from OA_ARRANGE_DETAIL 
    where USERCODE = checkuser and work_date = ckDate;
    
    if nClassId is null then -- 没有特别排班
      if WORK_DAY_OPT.is_work_day(ckDate)=0 then -- 正常休息日
        return;
      end if;
      nClassId := 1;
    end if;
    
    if nClassId = 0 then -- 排班为休息
      return;
    end if;
    
    select nvl(max(leave_or_overtime),'N') into classType -- 一般一天只能有一条
    from OA_leave_overtime 
    where Usercode = checkuser and 
          (   (Begin_Time <= ckDate and End_Time > ckDate)
            or(Begin_Time >= ckDate and Begin_Time < ckDate+1)
           ) ;           
           
    select begin_time,end_time into beginTime,endTime
    from OA_WORK_CLASS where CLASS_ID =  nClassId;    
    
    shouldInTime := ckDate + TO_DSINTERVAL('0 '|| beginTime ||':00');
    if substr(endTime,1,1) = '+' then
      shouldOutTime := ckDate + TO_DSINTERVAL('1 '|| substr(endTime,2) ||':00');
    else
      shouldOutTime := ckDate + TO_DSINTERVAL('0 '|| endTime ||':00');
    end if;

    select min(ATTENDANCE_TIME)  into inTime -- 获取上班时间
    from OA_Attendance_list 
    where usercode=checkuser and
       ATTENDANCE_TIME>= shouldInTime - 1/8 and ATTENDANCE_TIME <  shouldInTime + 1/8 ;
       
    select max(ATTENDANCE_TIME)  into outTime -- 获取下班时间
    from OA_Attendance_list 
    where usercode=checkuser and
       ATTENDANCE_TIME>= shouldOutTime - 1/8 and ATTENDANCE_TIME <  shouldOutTime + 1/8 ;
       
    insert into OA_attendance_detail 
           (USERCODE,attendance_date,CLASS_ID,is_late,is_leave,
            is_absences ,class_type ,should_in_time , in_time ,should_out_time,
            out_time)
    values(checkuser,ckDate,nClassId, case when inTime <= shouldInTime then 'N' else 'Y' end,
            case when outTime >= shouldOutTime then 'N' else 'Y' end, case when inTime is null and outTime is null then 'Y' else 'N' end ,
                 classType,shouldInTime,inTime,shouldOutTime,
            outTime);
    
  end;
  
  procedure check_all(checkDate in date)
    is
  begin
    for r in (select usercode from f_userinfo )loop
      check_one(r.usercode,checkDate);
    end loop;
  end; 

  procedure collect_one(checkuser in varchar2,yearAndMonth in varchar2, forceDeleteOld in number default 0)
    is
    beAudited char;
    overTime number(6,2);
    leaveTime number(6,2);
    lateTimes number(4);
    leaveTimes number(4);
    absencesTimes number(4);
    askLeaveTimes number(4);
    workOverTimes number(4);
    businessTimes number(4);
  begin
    select be_audited into beAudited from OA_attendance_info where USERCODE=checkuser and attendance_month = yearAndMonth;

    if beAudited = 'Y' and forceDeleteOld=0 then
      return;
    end if;
    
    if beAudited is not null and (beAudited = 'N' or forceDeleteOld='1') then
      delete from OA_attendance_info where USERCODE=checkuser and attendance_month = yearAndMonth;
    end if;
    
    select sum(work_times) into overTime from OA_leave_overtime  
    where USERCODE = USERCODE and end_time >= to_date(yearAndMonth,'YYYYMM')
          and end_time < add_months(to_date(yearAndMonth,'YYYYMM'),1);
    
    --N 正常、L 请假 O 加班 B 出差
    select sum(case when is_late='Y' then nvl2(in_Time,should_In_Time - in_Time,-1/6) else 0 end )
            +  sum(case when is_leave='Y' then nvl2(out_Time,out_Time - should_Out_Time,-1/6) else 0 end ),
           sum(case when class_Type = 'N' and is_absences='N' and is_late='Y' then 1 else 0 end ),
           sum(case when class_Type = 'N' and is_absences='N' and is_leave='Y' then 1 else 0 end ),
           sum(case when class_Type = 'N' and is_absences='Y' then 1 else 0 end ),
           sum(case when class_Type = 'L' then 1 else 0 end ),
           sum(case when class_Type ='O' then 1 else 0 end ),
           sum(case when class_Type ='B' then 1 else 0 end )
       into leaveTime ,lateTimes, leaveTimes,absencesTimes,askLeaveTimes,workOverTimes, businessTimes            
    from OA_attendance_detail
    where USERCODE = USERCODE and attendance_date >= to_date(yearAndMonth,'YYYYMM')
          and attendance_date < add_months(to_date(yearAndMonth,'YYYYMM'),1);
          --and classType = 'N';
        
    insert into OA_attendance_info  
           (USERCODE, attendance_month, collect_date ,late_times  ,leave_times ,
            absences_times, business_times, ask_leave_times,  work_over_times ,  work_time_deviation  , 
            be_audited  )
    values (checkuser,yearAndMonth,sysdate,lateTimes,leaveTimes,
           absencesTimes,businessTimes,askLeaveTimes,workOverTimes, leaveTime * 24 + overTime,
           'N'
      );

  end;
    
  procedure collect_all(yearAndMonth in varchar2)
    is
  begin
    for r in (select usercode from f_userinfo )loop
      collect_one(r.usercode,yearAndMonth,0);
    end loop;
  end;
  
  procedure auto_check
    is
  begin
    check_all(sysdate-1 );
    commit;
  end;

  procedure auto_collect
    is
  begin
    collect_all(to_char(trunc(add_months(sysdate,-1)),'YYYYMM'));
    commit;
  end; 

end check_attendance;
/
