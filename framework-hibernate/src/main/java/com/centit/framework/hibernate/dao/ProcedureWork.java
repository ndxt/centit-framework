package com.centit.framework.hibernate.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.jdbc.Work;

import com.centit.support.algorithm.DatetimeOpt;

/**
 * 存储过程调用接口
 * @author codefan
 *
 */
public class ProcedureWork implements Work {
	
	public static final int ORACLE_TYPES_CURSOR = -10; // oracle.jdbc.OracleTypes.CURSOR
	
    private String procName; // 存储过程语句
    private ResultSet rs = null; // 返回结果集
    private List<Object> paramObjs = new ArrayList<Object>(); // 存储过程涉及参数
    protected static Logger logger = LoggerFactory.getLogger(ProcedureWork.class);
    private boolean isOracleProcedureWithReturnCursor;
    private boolean isSucceedExecuted;
    
    public ProcedureWork(String procName, Object... params) {
        isOracleProcedureWithReturnCursor = false;
        isSucceedExecuted = false;
        this.procName = procName;
        for (Object obj : params) {
            paramObjs.add(obj);
        }
    }
    
    public void setOracleProcedureWithReturnCursor(boolean isOPWRC){
        isOracleProcedureWithReturnCursor = isOPWRC;
    }
    
    public boolean hasBeSucceedExecuted(){
        return isSucceedExecuted;
    }
    
    public ResultSet getRetrunResultSet() {
        return rs;
    }
    
    @Override
    public void execute(Connection connection) throws SQLException
    {
        int n = paramObjs.size();
        StringBuilder procDesc = new StringBuilder("{call ");
        
        procDesc.append(procName).append("(");
        for (int i = 0; i < n; i++) {
            if (i > 0)
                procDesc.append(",");
            procDesc.append("?");
        }
        if(isOracleProcedureWithReturnCursor){
            if(n>0)
                procDesc.append(",");
            procDesc.append("?");
        }
        procDesc.append(")}");
        
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall(procDesc.toString());
            for (int i = 0; i < n; i++) {
                if (paramObjs.get(i) == null)
                    stmt.setNull(i + 1, Types.NULL);
                else if (paramObjs.get(i) instanceof java.util.Date)
                    stmt.setObject(i + 1, DatetimeOpt
                            .convertSqlDate((java.util.Date) paramObjs.get(i)));
                else
                    stmt.setObject(i + 1, paramObjs.get(i));
            }
            
            if(isOracleProcedureWithReturnCursor)
                stmt.registerOutParameter(n + 1, ORACLE_TYPES_CURSOR);
            
            stmt.execute();
            
            if(isOracleProcedureWithReturnCursor)
                this.rs = (ResultSet) stmt.getObject(n + 1);
            isSucceedExecuted = true;
        } catch (SQLException e) {
        	logger.error(e.getMessage(),e);//e.printStackTrace();
        }
    }
}
