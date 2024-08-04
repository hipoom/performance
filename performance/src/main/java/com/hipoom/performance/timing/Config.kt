package com.hipoom.performance.timing

/**
 * @author ZhengHaiPeng
 * @since 2024/8/4 16:14
 */
class Config(

    /**
     * 如果一个函数执行的耗时低于这个值，则不会记录到文件中。
     */
    val recordThresholdMills: Long,

    /**
     * The period of saving timing's info to the file.
     * The recommend value is 1000ms.
     */
    val flushPeriodMills: Long,

    /**
     * print log to the logcat or not.
     */
    val needPrintLogcat: Boolean
)