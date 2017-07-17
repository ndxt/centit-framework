create or replace package WORK_DAY_OPT is

  -- Author  : CODEFAN
  -- Created : 2013-8-30 13:47:32
  -- Purpose : 工作日历计算
  
   /**
  判断一天是否是工作日 0 假期  1 工作日
  */
  function is_work_day(currDate in date) return number;
  
  /**
  判断是否是工作时间 0 不是工作时间  1  是工作时间
  */
  function is_work_time(currTime in date) return number;
  
  /**
  判断当前时间是否是工作时间 0 不是工作时间  1  是工作时间
  */
  function is_work_time return number;
  
  /**
  工作日计算，返回参数currDate 后 workDays个工作日的日期
  */
  function calc_next_work_date(currDate in date, workDays in number)
     return date; 
  /**
  工作时间计算，返回参数currDate 后 workDays个工作日 workHours 工作时间 的日期
  */
  function calc_next_work_time(currDate in date, workDays in number,workHours in number)
     return date;
     
  /**
  计算两个日期之间的周末天数（周六、周日分别计算一天）
  部包含 endDays ，这个最后一天
  */   
  function calc_Weekend_Days(beginDate in date, endDate in date)
    return number;
    
  /**
  计算两个日期之间的工作日时间， 返回值为  工作日， 工作时间偏移
  */  
  procedure proc_calc_work_time(beginDate in date, endDate in date,workDays out number, timeOffset out number);  
  
  /**
  计算两个日期之间的工作日时间， 返回值为  工作时 
  */   
  function calc_work_time(beginDate in date, endDate in date,oneDayWorkTime in number)
     return number;   
     
end WORK_DAY_OPT;
/
create or replace package body WORK_DAY_OPT is

/**
判断一天是否是工作日 0 假期  1 工作日
*/
    function is_work_day(currDate in date) return number
    is
      nTemp number(10);
      nRes  number(1);
      dayD varchar2(2);
    begin 
      dayD := to_char(currDate, 'D');
      nRes := 0;
      if dayD='1' or dayD='7' then
         select count(1) into nTemp from F_WORK_DAY where workday=currDate and daytype='B';
         if nTemp > 0 then
            nRes := 1;
         end if;
       else
         select count(1) into nTemp from F_WORK_DAY where workday=currDate and daytype='A';
         if nTemp = 0 then
            nRes := 1;
         end if;
       end if;
      return nRes;
    end is_work_day;
    
     
  /**
  判断是否是工作时间 0 不是工作时间  1  是工作时间
  */
  function is_work_time(currTime in date) return number
     is
      amBegin date;
      amEnd date;
      pmBegin date; 
      pmEnd date;
    begin
      if is_work_day(currTime) = 0 then
        return 0;
      else
         amBegin := to_date( to_char(currTime,'YYYY-MM-DD ')|| '9:00' ,'YYYY-mm-dd HH24:mi') ;
         amEnd   := to_date( to_char(currTime,'YYYY-MM-DD ')|| '12:00' ,'YYYY-mm-dd HH24:mi') ;
         
         if currTime>=amBegin and currTime < amEnd then
           return 1;  
         else
           pmBegin := to_date( to_char(currTime,'YYYY-MM-DD ')|| '13:30' ,'YYYY-mm-dd HH24:mi') ;
           pmEnd   := to_date( to_char(currTime,'YYYY-MM-DD ')|| '17:30' ,'YYYY-mm-dd HH24:mi') ;

           if currTime>=pmBegin and currTime < pmEnd then
             return 1;  
           else
             return 0;
           end if;
        end if;
      end if;
     
    end is_work_time;
   
  /**
  判断当前时间是否是工作时间 0 不是工作时间  1  是工作时间
  */
  function is_work_time return number
    is
    begin
      return is_work_time(sysdate);
    end is_work_time;
  
 /**
   计算 工作日后的时间，
   这个最好重构一下 F_WORK_DAY 表，记录所有的天是否是工作日，并对工作日排序，这样就可以很容的查找到对应的日期了。
   **/
   
   
   function calc_next_work_date(currDate in date, workDays in number)
     return date
   is
     nextWorkDate date;
     nCurWorkDays number(10);
     holidays number(10);
     adddays number(10);
   begin
     nCurWorkDays :=  FLOOR(workDays/5) * 7;
     nextWorkDate := currDate+nCurWorkDays;
     select  count(case daytype when 'A' then 1 end),
             count(case daytype when 'B' then 1 end) into holidays,adddays
     from F_WORK_DAY where workday >=currDate and workday<nextWorkDate;
     nCurWorkDays := FLOOR(workDays/5)*5 + adddays - holidays;
     
     while nCurWorkDays < workDays loop
       
       nextWorkDate := nextWorkDate +1;
       if is_work_day(nextWorkDate)=1 then
           nCurWorkDays := nCurWorkDays+1;
       end if;
     end loop;
     
     while nCurWorkDays > workDays loop       
       nextWorkDate := nextWorkDate - 1;
       if is_work_day(nextWorkDate)=1 then
           nCurWorkDays := nCurWorkDays - 1;
       end if;
     end loop;    
     
     return nextWorkDate;
   end calc_next_work_date;
   
      
   function calc_next_work_time(currDate in date, workDays in number,workHours in number)
     return date
   is
     addDays number(10);
     beginDate date;
     calcDate date;
     amBegin date;
     amEnd date;
     --pmBegin date; 
     pmEnd date;
     amBreak varchar2(32);
     pmBreak varchar2(32);
     wHours number(4);
     wMinutes number(4);
   begin
     beginDate := currDate;
     --calcDate := currDate;
     addDays := workDays;
/*     
     select * from F_WORK_CLASS where class_id = 1;
     
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi') into amBegin
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='AM_BEGIN';
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi'),extracode into amEnd,amBreak
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='AM_END';
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi') into pmBegin
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='PM_BEGIN';  
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi'),extracode into pmEnd,pmBreak
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='PM_END';
*/
     amBegin := to_date( to_char(currDate,'YYYY-MM-DD ')|| '9:00' ,'YYYY-mm-dd HH24:mi') ;
     amEnd   := to_date( to_char(currDate,'YYYY-MM-DD ')|| '12:00' ,'YYYY-mm-dd HH24:mi') ;
     amBreak := '1:30';
     --pmBegin := to_date( to_char(currDate,'YYYY-MM-DD ')|| '13:30' ,'YYYY-mm-dd HH24:mi') ;
     pmEnd   := to_date( to_char(currDate,'YYYY-MM-DD ')|| '17:30' ,'YYYY-mm-dd HH24:mi') ;
     pmBreak := '15:30' ;
                     
     if beginDate < amBegin then 
       beginDate := amBegin; 
     elsif beginDate> pmEnd then
       beginDate := amBegin;
       addDays := workDays +1;    
     end if;
     
     wHours := FLOOR(workHours);
     wMinutes := ROUND((workHours-wHours)*60); 
     calcDate := beginDate + TO_DSINTERVAL('0 '||to_char(wHours)||':'||to_char(wMinutes)||':00');
     
     if beginDate < amEnd and calcDate > amEnd then 
        calcDate := calcDate +  TO_DSINTERVAL('0 '||amBreak||':00');
     end if;
     if calcDate > pmEnd then 
        calcDate := calcDate +  TO_DSINTERVAL('0 '||pmBreak||':00');
        calcDate := calcDate - 1;
        if calcDate > amEnd then 
           calcDate := calcDate +  TO_DSINTERVAL('0 '||amBreak||':00');
        end if;
        addDays := workDays +1;    
     end if;
     
     return calc_next_work_date(calcDate,addDays);
   end calc_next_work_time;
   
   function calc_Weekend_Days(beginDate in date, endDate in date)
    return number
   is
    m number(6);
    nWeekDay number(6);
    days number(6);
   begin
     nWeekDay := to_number(to_char(beginDate,'D')) -1;
     m := trunc(endDate) - trunc(beginDate);
     days :=FLOOR((m+nWeekDay) / 7) * 2;
     if nWeekDay>0 then
          days := days-1;
     end if;
     
     if mod(m+nWeekDay, 7)>0 then
          days := days+1;
     end if;
     
     return days;
   end calc_Weekend_Days;
   
   procedure proc_calc_work_time(beginDate in date, endDate in date,workDays out number, timeOffset out number)
    is
     nSign number(1);
     nDays number(6);
     nWeekEnds number(6);
     nTemp number(6);
     wHours number(8,4);
     holidays number(10);
     adddays number(10);
     currDate date;
     bDate date;
     eDate date;
     --amBegin date;
     amEnd date;
     pmBegin date; 
     --pmEnd date;
     amBreak varchar2(32);
   begin
     select  count(case daytype when 'A' then 1 end),
             count(case daytype when 'B' then 1 end) into holidays,adddays
     from F_WORK_DAY 
     where workday >=trunc(beginDate) and workday < trunc(endDate);-- +1;
     nDays := trunc(endDate) - trunc(beginDate);
     nWeekEnds := calc_Weekend_Days(beginDate,endDate);
     
     
     currDate := beginDate + nDays;
     if currDate < endDate then
        bDate := currDate;
        eDate := endDate;
        nSign := 1;
     else
        eDate := currDate;
        bDate := endDate;
        nSign := -1;
     end if;
     
     wHours := (eDate-bDate) * 24;
/*     
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi'),extracode into amEnd,amBreak
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='AM_END';
     select to_date( to_char(currDate,'YYYY-MM-DD ')|| datavalue ,'YYYY-mm-dd HH24:mi') into pmBegin
                     from f_Datadictionary where catalogcode='SYSPARAM' and datacode='PM_BEGIN';  
*/                     
     amEnd   := to_date( to_char(currDate,'YYYY-MM-DD ')|| '12:00' ,'YYYY-mm-dd HH24:mi') ;
     amBreak := '1:30';
     pmBegin := to_date( to_char(currDate,'YYYY-MM-DD ')|| '13:30' ,'YYYY-mm-dd HH24:mi') ;
           
     if  bDate< amEnd and eDate > pmBegin then
       nTemp := instr(amBreak,':');
       wHours := wHours - to_number(substr(amBreak ,0,nTemp-1));
       wHours := wHours - to_number(substr(amBreak ,nTemp+1))/60;       
     end if;             
     workDays := nDays - nWeekEnds + adddays - holidays;
     timeOffset := nSign * wHours;
   end proc_calc_work_time;
/**   
   function calc_work_time(beginDate in date, endDate in date)
     return number
   is
     nSign number(1);
     workDays number(6);
     timeOffset number(8,4);
   begin
     proc_calc_work_time(beginDate,endDate,workDays,timeOffset);
     if timeOffset < 0 then
       nSign := -1;
       timeOffset := 0-timeOffset;
     else
       nSign := 1;
     end if;     
     return  nSign * ( workDays * 100 + timeOffset);
   end calc_work_time;
**/     
   function calc_work_time(beginDate in date, endDate in date,oneDayWorkTime in number)
     return number
    is
     workDays number(6);
     timeOffset number(8,4);
   begin
     proc_calc_work_time(beginDate,endDate,workDays,timeOffset);
     return  workDays * oneDayWorkTime + timeOffset;
   end;

end WORK_DAY_OPT;
/
