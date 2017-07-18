package com.centit.framework.system.service;

import java.util.Date;
import java.util.List;

import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.core.service.BaseEntityManager;
import com.centit.framework.system.po.OptFlowNoInfo;
import com.centit.framework.system.po.OptFlowNoInfoId;
import com.centit.framework.system.po.OptFlowNoPool;
import com.centit.support.algorithm.DatetimeOpt;

/**
 * 流水号生成规则，流水号根据   编码属主    OwnerCode  ，编码类别    CodeCode ，编码依据日期  CodeDate 三个属性进行顺序编码
 *
 * @author codefan
 * 2012-6-11
 */
public interface OptFlowNoInfoManager extends BaseEntityManager<OptFlowNoInfo, OptFlowNoInfoId> {
     final static String DefaultOwnerCode = "noowner";
     final static Date DefaultCodeDate = DatetimeOpt.createUtilDate(2000, 1, 1);

    /**
     * 获取下一个流水号，流水好是根据 拥有者、类别代码、编码的基准时间
     *
     * @param ownerCode    根据 拥有者，如果设置为  DefaultOwnerCode 则这个编码则依赖于编码
     * @param codeCode     类别代码
     * @param codeBaseDate 编码的基准时间
     * @return 返回流水号
     */
     long newNextLsh(String ownerCode, String codeCode, Date codeBaseDate);

    /**
     * 获取下一个流水号，流水好是根据 拥有者、类别代码、编码的基准时间这个时间是按照天来编制的就是同一天中顺序编号
     *
     * @param ownerCode    根据 拥有者
     * @param codeCode     类别代码
     * @param codeBaseDate 编码的基准时间
     * @return 返回流水号
     */
     long newNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate);

    /**
     * 获取下一个流水号，流水好是根据 拥有者、类别代码、编码的基准时间这个时间是按照月来编制的就是同一月中顺序编号
     *
     * @param ownerCode    根据 拥有者
     * @param codeCode     类别代码
     * @param codeBaseDate 编码的基准时间
     * @return 返回流水号
     */
     long newNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate);

    /**
     * 获取下一个流水号，流水好是根据 拥有者、类别代码、编码的基准时间这个时间是按照年来编制的就是同一年中顺序编号
     *
     * @param ownerCode    根据 拥有者
     * @param codeCode     类别代码
     * @param codeBaseDate 编码的基准时间
     * @return 返回流水号
     */
     long newNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate);

    /**
     * 这个只根据 类别代码来编号，他类似于序列，sql server中没有序列可以用这个来模拟
     *
     * @param ownerCode 根据 拥有者
     * @param codeCode codeCode
     * @return  long
     */
     long newNextLsh(String ownerCode, String codeCode);

    /**
     * 这个只根据 类别代码来编号，他类似于序列，sql server中没有序列可以用这个来模拟
     *
     * @param codeCode  codeCode
     * @return long
     */
     long newNextLsh(String codeCode);

    /**
     * view 这一组方法和上面一样，则是这一组并没有记录当前获取的值，如果反复调用则会得到相同的值，
     * 调用这组方法后再调用下面对应的record方法会获得和上面create相对应的函数的效果
     * 这组方法的用处是为了避免编码跳号，但是带来的另一个副作用就是会获得相同的编码，使用要注意处理相应的异常。
     */

     long viewNextLsh(String ownerCode, String codeCode, Date codeBaseDate);

     long viewNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate);

     long viewNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate);

     long viewNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate);

     long viewNextLsh(String ownerCode, String codeCode);

     long viewNextLsh(String codeCode);

    /**
     * 配合view对应的方法使用。 或者配合 池中的流水号 listLshInPool
     * 作用是 记录某个流水号已经被使用
     *
     * @param ownerCode String
     * @param codeCode String
     * @param codeBaseDate Date
     * @param currCode long
     */
     void recordNextLsh(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void recordNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void recordNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void recordNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void recordNextLsh(String ownerCode, String codeCode, long currCode);

     void recordNextLsh(String codeCode, long currCode);

    /**
     * 回收一个流水号
     *
     * @param ownerCode String
     * @param codeCode String
     * @param codeBaseDate Date
     * @param currCode long
     */
     void releaseLsh(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void releaseLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void releaseLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void releaseLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode);

     void releaseLsh(String ownerCode, String codeCode, long currCode);

     void releaseLsh(String codeCode, long currCode);

    /**
     * 从pool池中查找某个类别的流水号
     *
     * @param ownerCode String
     * @param codeCode String
     * @param codeBaseDate Date
     * @param pageDesc PageDesc
     * @return List
     */
     List<OptFlowNoPool> listLshInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc);

     List<OptFlowNoPool> listLshBaseDayInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc);

     List<OptFlowNoPool> listLshBaseMonthInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc);

     List<OptFlowNoPool> listLshBaseYearInPool(String ownerCode, String codeCode, Date codeBaseDate, PageDesc pageDesc);

     List<OptFlowNoPool> listLshInPool(String ownerCode, String codeCode, PageDesc pageDesc);

     List<OptFlowNoPool> listLshInPool(String codeCode, PageDesc pageDesc);

    /**
     * 流水号可以回收的算法，在回收池中找一个最小的流水号，如果没有调用 newNextLsh
     *
     * @param ownerCode String
     * @param codeCode String
     * @param codeBaseDate Date
     * @return long
     */
     long assignNextLsh(String ownerCode, String codeCode, Date codeBaseDate);

     long assignNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate);

     long assignNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate);

     long assignNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate);

     long assignNextLsh(String ownerCode, String codeCode);

     long assignNextLsh(String codeCode);

}
