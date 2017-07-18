package com.centit.framework.hibernate.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.po.EntityWithTimestamp;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;

/**
 * 对数据库CUD操作进行监听并对感兴趣的表进行数据更改的日志记录
 *
 * @author sx
 * 2014-10-14
 */

/**
 * 
 * 这个类已经废弃了，不需要了
 * 
 * @author codefan
 * 2015年10月16日
 */
//@Transactional
//@Component
public class PoDataChangesListener implements PreInsertEventListener, 
        PreUpdateEventListener, PostInsertEventListener,
        PostUpdateEventListener, PostDeleteEventListener {

    /**
     *
     */
    private static final long serialVersionUID = -1992903736066173748L;

    private static Logger logger = LoggerFactory.getLogger(PoDataChangesListener.class);

     private List<String> listeners;

    public void setListeners(List<String> listeners) {
        this.listeners = listeners;
        
        //ParserConfig.getGlobalInstance().putDeserializer(Date.class, null);
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        if (event.getEntity() instanceof EntityWithTimestamp) {
            EntityWithTimestamp baseEntity = (EntityWithTimestamp) event.getEntity();
            baseEntity.setLastModifyDate(new Date());
        }
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (event.getEntity() instanceof EntityWithTimestamp) {
            EntityWithTimestamp baseEntity = (EntityWithTimestamp) event.getEntity();
            baseEntity.setLastModifyDate(new Date());

        }
        return false;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        saveOptLog(event.getEntity(), event.getId(), event.getPersister().getPropertyNames(), event.getState(),
                event.getOldState(), OperationLog.P_OPT_LOG_METHOD_C);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        saveOptLog(event.getEntity(), event.getId(), event.getPersister().getPropertyNames(), event.getState(), null,
                OperationLog.P_OPT_LOG_METHOD_U);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        saveOptLog(event.getEntity(), event.getId(), null, null, null, OperationLog.P_OPT_LOG_METHOD_D);
    }

    public void saveOptLog(Object object, Serializable id, String[] propertyNames, Object[] states, Object[] oldState,
                           String type) {
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }

        for (String clazzName : listeners) {
            // 去除所有空格，防止输入错误
            clazzName = StringUtils.trimAllWhitespace(clazzName);
            String optContent;
            Class<?> clazz = null;
            try {
                clazz = Class.forName(clazzName);
            } catch (ClassNotFoundException e) {
                String message = "spring-hibernate.xml中配置PoDataChangesListener的listeners属性value值class=" + clazzName
                        + "不存在";

                System.err.println(message);
                logger.error(message);
                return;
            }

            if (object.getClass().isAssignableFrom(clazz)) {
                StringBuilder values = new StringBuilder("{");
                StringBuilder oldvalues = new StringBuilder("{");

                if (OperationLog.P_OPT_LOG_METHOD_D.equals(type)) {
                	optContent = "删除 " + ClassUtils.getShortName(clazz);
                } else {
                	optContent = "修改 " + ClassUtils.getShortName(clazz);
                    for (int i = 0; i < states.length; i++) {
                        String propertyName = propertyNames[i];
                        String newvalue = String.valueOf(states[i]);
                        if (ArrayUtils.isNotEmpty(oldState)) {
                            String oldvalue = String.valueOf(oldState[i]);
                            oldvalues.append("[字段 = " + propertyName + " 原值 = " + oldvalue + "] ");
                        }

                        values.append("[字段 = " + propertyName + " 新值 = " + newvalue + "] ");
                    }
                }
                values.append("}");
                oldvalues.append("}");

                OperationLogCenter.logUpdateObject(
                		getUserCode(), ClassUtils.getShortName(clazz), String.valueOf(id), type,optContent,
                        values.toString(), oldvalues.toString());
            }
        }
    }

    private String getUserCode() {
        CentitUserDetails loginUser = WebOptUtils.getLoginUser(RequestThreadLocal
                .getHttpThreadWrapper().getRequest());
        // return "操作用户 " + ((FUserinfo)loginUser).getLoginname() + " " ;
        return loginUser == null ? "" : loginUser.getUserCode();
    }

    
    public boolean requiresPostCommitHanding(EntityPersister persister) {
       
        return false;
    }
}
