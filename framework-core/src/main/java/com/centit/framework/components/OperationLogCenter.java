package com.centit.framework.components;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.support.algorithm.DatetimeOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class OperationLogCenter {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogCenter.class);
    private static OperationLogWriter logWriter = null;
    private static BlockingQueue<OperationLog> waitingForWriteLogs = new LinkedBlockingQueue<>();
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);

    private static Set<String> includeOpts = null;
    private static Set<String> excludeOpts = null;
    /**
     * 异步写入日志
     */
    static {
        // 5秒写入一次
        executor.scheduleWithFixedDelay(() ->
        {
            if (logWriter == null)
                return;
            int nCount = 100;
            // 每一百条日志批量写入一次
            try {
                while (nCount > 99) {
                    nCount = 0;
                    ArrayList<OperationLog> optLogs = new ArrayList<>(100);
                    do { //true){//
                        OperationLog optLog = waitingForWriteLogs.poll();
                        if (optLog == null) {
                            break;
                        }
                        optLogs.add(optLog);
                        nCount++;
                    } while (nCount < 100);
                    if (nCount > 0) {
                        logWriter.save(optLogs);
                    }
                }
            } catch (Exception e) {
                logger.error("日志写入定时器错误：" + e.getMessage());
            }
        }, 30, 5, TimeUnit.SECONDS);
        //默认执行时间间隔为5秒
    }

    private OperationLogCenter() {
    }

    /**
     * 设置默认的写入器，如果用户调用  registerOperationLogWriter 则不执行
     *
     * @param optLogWriter optLogWriter
     */
    public static void initOperationLogWriter(OperationLogWriter optLogWriter) {
        if (logWriter == null)
            logWriter = optLogWriter;
    }

    public static void addOptLogWhiteList(String optId){
        if(includeOpts==null){
            includeOpts = new HashSet<>(16);
        }
        includeOpts.add(optId);
    }

    public static void addOptLogBlackList(String optId){
        if(excludeOpts==null){
            excludeOpts = new HashSet<>(16);
        }
        excludeOpts.add(optId);
    }
    /**
     * 个用户设置自己的日志写入器
     *
     * @param optLogWriter optLogWriter
     */
    public static void registerOperationLogWriter(OperationLogWriter optLogWriter) {
        logWriter = optLogWriter;
    }

    public static void log(OperationLog optLog) {
        if((optLog != null) &&
            (includeOpts==null || includeOpts.isEmpty() || includeOpts.contains(optLog.getOptId())) &&
            (excludeOpts==null || excludeOpts.isEmpty() || !excludeOpts.contains(optLog.getOptId())) ){
            try {
                waitingForWriteLogs.put(optLog);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                //throw new RuntimeException(e);
            }
        }
    }

    /**
     * 新建对象日志
     *
     * @param request    获取操作人员
     * @param optId      业务代码（类别）
     * @param optTag     对象主键，如果是联合主键请用url的方式编写，比如a=1;b=2
     * @param optMethod  操作方法
     * @param optContent 操作说明
     * @param newObject  新增对象
     */
    public static void logNewObject(HttpServletRequest request, String optId, String optTag, String optMethod,
                                    String optContent, Object newObject) {
        log(OperationLog.create().level(OperationLog.LEVEL_INFO)
            .user(WebOptUtils.getCurrentUserCode(request)).operation(optId).tag(optTag).method(optMethod)
            .unit(WebOptUtils.getCurrentUnitCode(request)).topUnit(WebOptUtils.getCurrentTopUnit(request))
            .correlation(WebOptUtils.getCorrelationId(request))
            .content(optContent).newObject(newObject)
            .time(DatetimeOpt.currentUtilDate()).loginIp(WebOptUtils.getRequestAddr(request)));
    }

    /**
     * 查询数据日志
     *
     * @param request   业务请求
     * @param optId     业务代码（类别）
     * @param optMethod 对象主键
     * @param queryDesc 查询说明
     * @param queryMap  查询条件对象
     */

    public static void logQuery(HttpServletRequest request, String optId, String optMethod,
                                String queryDesc, Map<String, Object> queryMap) {
        log(OperationLog.create().level(OperationLog.LEVEL_INFO)
            .user(WebOptUtils.getCurrentUserCode(request)).operation(optId).method(optMethod)
            .unit(WebOptUtils.getCurrentUnitCode(request)).topUnit(WebOptUtils.getCurrentTopUnit(request))
            .correlation(WebOptUtils.getCorrelationId(request))
            .content(queryDesc).newObject(queryMap)
            .time(DatetimeOpt.currentUtilDate()).loginIp(WebOptUtils.getRequestAddr(request)));
    }

    /**
     * 删除对象日志
     *
     * @param request    获取操作人员
     * @param optId      业务代码（类别）
     * @param optTag     对象主键，如果是联合主键请用url的方式编写，比如a=1;b=2
     * @param optMethod  操作方法
     * @param optContent 操作说明
     * @param oldObject  删除的对象
     */
    public static void logDeleteObject(HttpServletRequest request, String optId, String optTag, String optMethod,
                                       String optContent, Object oldObject) {
        log(OperationLog.create().level(OperationLog.LEVEL_INFO)
            .user(WebOptUtils.getCurrentUserCode(request)).operation(optId).tag(optTag).method(optMethod)
            .unit(WebOptUtils.getCurrentUnitCode(request)).topUnit(WebOptUtils.getCurrentTopUnit(request))
            .correlation(WebOptUtils.getCorrelationId(request))
            .content(optContent)
            .oldObject(oldObject).time(DatetimeOpt.currentUtilDate()).loginIp(WebOptUtils.getRequestAddr(request)));
    }

    /**
     * 更新对对象日志
     *
     * @param request    获取操作人员
     * @param optId      业务代码（类别）
     * @param optTag     对象主键，如果是联合主键请用url的方式编写，比如a=1;b=2
     * @param optMethod  操作方法
     * @param optContent 操作说明
     * @param newObject  新对象
     * @param oldObject  旧对象
     */
    public static void logUpdateObject(HttpServletRequest request, String optId, String optTag, String optMethod,
                                       String optContent, Object newObject, Object oldObject) {
        log(OperationLog.create().user(WebOptUtils.getCurrentUserCode(request)).operation(optId)
            .unit(WebOptUtils.getCurrentUnitCode(request)).topUnit(WebOptUtils.getCurrentTopUnit(request))
            .correlation(WebOptUtils.getCorrelationId(request))
            .tag(optTag).method(optMethod).content(optContent)
            .makeDifference(oldObject, newObject)
            .time(DatetimeOpt.currentUtilDate()).loginIp(WebOptUtils.getRequestAddr(request)));
    }
}
