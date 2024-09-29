package com.hipoom.performance.looper.main.record.trace;

/**
 * @author ZhengHaiPeng
 * @since 2024/9/29 22:53
 */
public class MessageInfo {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 消息添加时刻。
     */
    public long addTimestamp;

    /**
     * 消息添加时的堆栈。
     */
    public Throwable addTrace;

}
