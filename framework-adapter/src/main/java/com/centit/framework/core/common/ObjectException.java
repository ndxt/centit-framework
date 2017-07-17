package com.centit.framework.core.common;


/**
 * An exception that is thrown by classes wanting to trap unique
 * constraint violations.  This is used to wrap Spring's
 * DataIntegrityViolationException so it's checked in the web layer.
 * <p/>
 * <p><a href="UserExistsException.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class ObjectException extends RuntimeException {
    private static final long serialVersionUID = 4050482305178810162L;

    public static final int UNKNOWN_EXCEPTION = -1;
    public static final int NULL_EXCEPTION = 2;
    public static final int BLANK_EXCEPTION = 3;
    public static final int FORMAT_DATE_EXCEPTION = 4;
    public static final int FORMAT_NUMBER_EXCEPTION = 5;
    public static final int DATABASE_OPERATE_EXCEPTION = 6;
    public static final int DATABASE_OUT_SYNC_EXCEPTION = 7;
    
    private int exceptionCode;
    private Object objectData;
    /**
     * Constructor for UserExistsException.
     *
     * @param message
     */
    public ObjectException(int exceptionCode,String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
    
    public ObjectException( int exceptionCode, Throwable exception) {
        super(exception);
        this.exceptionCode = exceptionCode;
    }
    
    public ObjectException( Throwable exception) {
        super(exception);
        this.exceptionCode = UNKNOWN_EXCEPTION;
    }
    
    public ObjectException(String message) {
    	 super(message);
         this.exceptionCode = UNKNOWN_EXCEPTION;
    }

    public ObjectException(Object obj,int exceptionCode,String message) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.objectData = obj;
    }
    
    public ObjectException(Object obj, int exceptionCode, Throwable exception) {
        super(exception);
        this.exceptionCode = exceptionCode;
        this.objectData = obj;
    }
    
    public ObjectException(Object obj,String message) {
        super(message);
        this.exceptionCode = UNKNOWN_EXCEPTION;
        this.objectData = obj;
    }
    
    public ObjectException(Object obj, Throwable exception) {
        super(exception);
        this.exceptionCode = UNKNOWN_EXCEPTION;
        this.objectData = obj;
    }
    
	public int getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public Object getObjectData() {
		return objectData;
	}

	public void setObjectData(Object objectData) {
		this.objectData = objectData;
	}
  
}
