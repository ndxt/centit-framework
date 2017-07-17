package com.centit.framework.security.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.centit.support.algorithm.DatetimeOpt;

public class CheckFailLogs {
   
    private static int checkTimeTnterval = 3;//Minite
    //允许尝试的最大次数
    private static int maxTryTimes= -1;
    //L:loginName H: HostIP
    private static char checkType='L';
    
    private static int lockMinites=15;


    public static void setMaxTryTimes(int maxTryTimes) {
        CheckFailLogs.maxTryTimes = maxTryTimes;
    }

    public static int getMaxTryTimes() {
        return CheckFailLogs.maxTryTimes;
    }
    
    public static void setCheckType(String checkType) {
        CheckFailLogs.checkType =
                checkType.equalsIgnoreCase("hostIp")?'H':'L';
    }

    public static char getCheckType() {
        return CheckFailLogs.checkType;
    }
    
    public static void setLockMinites(int lockMinites) {
/*        if(CheckFailLogs.DefaultCheckTimeTnterval
              == CheckFailLogs.checkTimeTnterval)
           CheckFailLogs.setCheckTimeTnterval(lockMinites);*/
        CheckFailLogs.lockMinites = lockMinites;
    }
    
    public static void setCheckTimeTnterval(int checkTimeTnterval) {
        CheckFailLogs.checkTimeTnterval = checkTimeTnterval;
    }
    

    
    private static Map<String ,CheckFailLog> failLogs =
            new HashMap<String ,CheckFailLog>();
    

    public static class CheckFailLog {
        private int tryTimes;
        
        private Date lastCheckTime; 
        
        public CheckFailLog(){
            tryTimes = 1;
            lastCheckTime = DatetimeOpt.currentUtilDate();                
        }

        public int getTryTimes() {
            return tryTimes;
        }
        public void setTryTimes(int tryTimes) {
            this.tryTimes = tryTimes;
        }
        public Date getLastCheckTime() {
            return lastCheckTime;
        }
        public void setLastCheckTime(Date lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
        }
        
        public void plusCheckFail(){
            Date currentDate = DatetimeOpt.currentUtilDate();
            if( DatetimeOpt.addMinutes(lastCheckTime,
                    checkTimeTnterval ).before(currentDate))
                tryTimes=1;
            else
                tryTimes++;
            lastCheckTime = currentDate;
        }
        
        public boolean isLocked(){
            Date currentDate = DatetimeOpt.currentUtilDate();
            if( DatetimeOpt.addMinutes(lastCheckTime,lockMinites).before(currentDate))
                return false;
            return tryTimes >= maxTryTimes;
        }
    }
    public static String getCheckKey(HttpServletRequest request){
        String checkKey;
        if (CheckFailLogs.getCheckType() == 'L')
            checkKey = request.getParameter(
                    UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
        else
            checkKey = request.getRemoteHost() + ":" + request.getRemotePort();
        return checkKey;
    }
    
    public static void removeCheckFail(HttpServletRequest request){
        failLogs.remove(getCheckKey(request));
    }
    
    public static void plusCheckFail(HttpServletRequest request){
        String checkKey = getCheckKey(request);
        CheckFailLog failLog = failLogs.get(checkKey);
        if(failLog!=null)
            failLog.plusCheckFail();
        else
            failLog = new CheckFailLog();
        failLogs.put(checkKey, failLog);
    }

    public static boolean isLocked(HttpServletRequest request){
        CheckFailLog failLog = failLogs.get(getCheckKey(request));
        return failLog==null?false:failLog.isLocked();
    }
    public static int getHasTriedTimes(HttpServletRequest request){
        CheckFailLog failLog = failLogs.get(getCheckKey(request));
        return failLog==null?0:failLog.getTryTimes();
    }
}
