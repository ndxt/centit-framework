package com.centit.framework.hibernate.common;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RichText 
	implements UserType,Serializable, Comparable<RichText>, CharSequence {

	private static final long serialVersionUID = 1L;
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int length() {
		if(text==null)
			return 0;
		return text.length();
	}

	@Override
	public char charAt(int index) {
		if(text==null)
			throw new StringIndexOutOfBoundsException(index);
		return text.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return StringUtils.substring(text, start, end);
	}

	@Override
	public int compareTo(RichText o) {
		if(text==null)
			return o==null?0:-1;
		if(o==null)
			return 1;
		return text.compareTo(o.getText());
	}
	
	public int compareToIgnoreCase(String str) {
		if(text==null)
			return str==null?0:-1;
        return text.compareToIgnoreCase(str);
    }
	
	public int hashCode() {
		return text == null ? 0 : text.hashCode();
	}
	 
	public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if(text==null)
        	return false;
        if (text == anObject) {
            return true;
        }
        return text.equals(anObject);
	}
	 
	public String toString() {
	   	return text;
	}
	 
	public RichText() {
	}
	
	public RichText(String original) {
		text = new String(original);
	}

    public RichText(char value[]) {
    	text = new String(value);
    }
	
    public RichText(char value[], int offset, int count) {
    	text = new String(value,offset,count);
    }

    public RichText(int[] codePoints, int offset, int count) {
    	text = new String(codePoints,offset,count);
    }
    public RichText(byte bytes[], Charset charset) {
    	text = new String(bytes, 0, bytes.length, charset);
    }
    
    public RichText(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
    	text = new String(bytes, 0, bytes.length, charsetName);
    }
    
    public RichText(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
    	text = new String(bytes, offset, length, charsetName);
    }
    public RichText(byte bytes[], int offset, int length, Charset charset) {
    	text = new String(bytes, offset, length, charset);
    }
    public RichText(StringBuffer buffer) {
    	text = new String(buffer);
    }
    
    public RichText(StringBuilder builder) {
    	text = new String(builder);
    }
    
    private static final int[] SQL_TYPES={Types.VARCHAR};
    
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class<RichText> returnedClass() {
		return RichText.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if(x==y){  
            return true;  
        }else if(x==null||y==null){  
            return false;  
        }else {  
            return x.equals(y);  
        }
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		if(rs.wasNull()){  
            return null;  
        }else{  
            String text=rs.getString(names[0]);  
            return new RichText(text);  
        }  
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if(value==null){  
            st.setNull(index, Types.VARCHAR);  
        }else {  
            String text= value.toString();  
            st.setString(index, text);  
        }  
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
