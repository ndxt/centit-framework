package com.centit.framework.hibernate.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.jdbc.Work;

import com.centit.support.algorithm.DatetimeOpt;

/**
 * 调用 存储方法（oracle特有的）
 * 
 * @author codefan
 *
 */
public class FunctionWork implements Work {

	private String functionName; // 存储过程语句
	private Object retrunObject = null; // 返回结果集
	private List<Object> paramObjs = new ArrayList<Object>(); // 存储过程涉及参数
	private int resultType;// java.sql.Types
	private boolean isSucceedExecuted;

	public FunctionWork(String funcName, int resultType, Object... params) {

		isSucceedExecuted = false;
		this.functionName = funcName;
		for (Object obj : params) {
			paramObjs.add(obj);
		}
		this.resultType = resultType;
	}

	public boolean hasBeSucceedExecuted() {
		return isSucceedExecuted;
	}

	public Object getRetrunObject() {
		return retrunObject;
	}

	@Override
	public void execute(Connection connection) throws SQLException{
		int n = paramObjs.size();
		StringBuilder procDesc = new StringBuilder("{?=call ");

		procDesc.append(functionName).append("(");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				procDesc.append(",");
			procDesc.append("?");
		}
		procDesc.append(")}");

		CallableStatement stmt = null;
		try {
			stmt = connection.prepareCall(procDesc.toString());

			stmt.registerOutParameter(1, resultType);// Types.VARCHAR

			for (int i = 0; i < n; i++) {
				if (paramObjs.get(i) == null)
					stmt.setNull(i + 2, Types.NULL);
				else if (paramObjs.get(i) instanceof java.util.Date)
					stmt.setObject(i + 2, DatetimeOpt.convertSqlDate((java.util.Date) paramObjs.get(i)));
				else
					stmt.setObject(i + 2, paramObjs.get(i));
			}

			stmt.execute();

			retrunObject = stmt.getObject(1);

			isSucceedExecuted = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getResultType() {
		return resultType;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
}
