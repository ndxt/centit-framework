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
public class ObjectException extends com.centit.support.common.ObjectException {
    private static final long serialVersionUID = 4050482305178810162L;

    public static final int DATABASE_OPERATE_EXCEPTION = 620;
    public static final int DATABASE_OUT_SYNC_EXCEPTION = 621;

    /**
     * Constructor for UserExistsException.
     * @param exceptionCode 异常码
     * @param message 异常信息
     */
    public ObjectException(int exceptionCode,String message) {
        super(exceptionCode, message);
    }

    /**
     *
     * @param exceptionCode 异常码
     * @param exception 异常信息
     */
    public ObjectException( int exceptionCode, Throwable exception) {
        super(exceptionCode, exception);
    }

    /**
     *
     * @param exception Throwable
     */
    public ObjectException( Throwable exception) {
        super(exception);
    }

    /**
     *
     * @param message String
     */
    public ObjectException(String message) {
        super(message);
    }

    /**
     *
     * @param obj Object
     * @param exceptionCode 异常码
     * @param message 异常信息
     */
    public ObjectException(Object obj,int exceptionCode,String message) {
        super(obj, exceptionCode, message);
    }

    /**
     *
     * @param obj Object
     * @param exceptionCode 异常码
     * @param exception 异常信息
     */
    public ObjectException(Object obj, int exceptionCode, Throwable exception) {
        super(obj, exceptionCode, exception);
    }

    /**
     *
     * @param obj Object
     * @param message String 异常信息
     */
    public ObjectException(Object obj,String message) {
        super(obj, message);
    }

    /**
     *
     * @param obj Object
     * @param exception Throwable
     */
    public ObjectException(Object obj, Throwable exception) {
        super(obj, exception);
    }

}
