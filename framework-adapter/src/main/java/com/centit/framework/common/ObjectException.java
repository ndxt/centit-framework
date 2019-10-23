package com.centit.framework.common;


/**
 * An exception that is thrown by classes wanting to trap unique
 * constraint violations.  This is used to wrap Spring's
 * DataIntegrityViolationException so it's checked in the web layer.
 * <p><a href="UserExistsException.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Deprecated
public class ObjectException extends RuntimeException {
    private static final long serialVersionUID = 4050482305178810162L;

    public static final int UNKNOWN_EXCEPTION = 601;
    public static final int NULL_EXCEPTION = 602;
    public static final int BLANK_EXCEPTION = 603;
    public static final int DATA_NOT_FOUND_EXCEPTION  = 604;
    public static final int EMPTY_RESULT_EXCEPTION  = 605;
    public static final int FORMAT_DATE_EXCEPTION = 606;
    public static final int FORMAT_NUMBER_EXCEPTION = 607;

    public static final int DATABASE_OPERATE_EXCEPTION = 620;
    public static final int DATABASE_OUT_SYNC_EXCEPTION = 621;
    private int exceptionCode;
    private Object objectData;
    /**
     * Constructor for UserExistsException.
     * @param exceptionCode 异常码
     * @param message 异常信息
     */
    public ObjectException(int exceptionCode,String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    /**
     *
     * @param exceptionCode 异常码
     * @param exception 异常信息
     */
    public ObjectException( int exceptionCode, Throwable exception) {
        super(exception);
        this.exceptionCode = exceptionCode;
    }

    /**
     *
     * @param exception Throwable
     */
    public ObjectException( Throwable exception) {
        super(exception);
        this.exceptionCode = UNKNOWN_EXCEPTION;
    }

    /**
     *
     * @param message String
     */
    public ObjectException(String message) {
        super(message);
        this.exceptionCode = UNKNOWN_EXCEPTION;
    }

    /**
     *
     * @param obj Object
     * @param exceptionCode 异常码
     * @param message 异常信息
     */
    public ObjectException(Object obj,int exceptionCode,String message) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.objectData = obj;
    }

    /**
     *
     * @param obj Object
     * @param exceptionCode 异常码
     * @param exception 异常信息
     */
    public ObjectException(Object obj, int exceptionCode, Throwable exception) {
        super(exception);
        this.exceptionCode = exceptionCode;
        this.objectData = obj;
    }

    /**
     *
     * @param obj Object
     * @param message String 异常信息
     */
    public ObjectException(Object obj,String message) {
        super(message);
        this.exceptionCode = UNKNOWN_EXCEPTION;
        this.objectData = obj;
    }

    /**
     *
     * @param obj Object
     * @param exception Throwable
     */
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
