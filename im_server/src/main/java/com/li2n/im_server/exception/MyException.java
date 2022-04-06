package com.li2n.im_server.exception;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-4-1 下午 2:38
 */
public class MyException extends Throwable {

    /**
     * Constructs a new throwable with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public MyException(String message, Object object) {
        System.err.println("异常信息：" + message);
        System.err.println("异常对象：" + object.toString());
    }
}
