package com.hipoom.performance.utils

import android.util.Log
import com.hipoom.Files
import java.io.File
import java.lang.StringBuilder
import java.lang.Thread.sleep
import java.nio.charset.Charset
import kotlin.concurrent.thread

/**
 * 日志记录。
 * 每个 Saver 对象，都会创建一个新的线程。
 *
 * @author ZhengHaiPeng
 * @since 9/29/24 7:29 PM
 */
class LogSaver {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 日志文件。
     */
    private val logFile: File

    /**
     * 配置信息。
     */
    private val config: Config

    /**
     * 日志内容
     */
    private val text = StringBuilder()

    /**
     * 是否还在记录。
     */
    private var isRunning = true



    /* ======================================================= */
    /* Constructors or Instance Creator                        */
    /* ======================================================= */

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(logFile: File, config: Config) {
        this.logFile = logFile
        this.config = config

        // 如果文件不存在就创建
        Files.createNewFileIfNotExist(logFile)

        // 开启一个线程，定时 flush 日志
        thread {
            while (isRunning) {
                // 等待一段时间，再写入文件
                sleep(config.flushPeriodMills)

                // 将缓存写入文件
                flush()
            }
        }
    }



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    fun append(msg: String) {
        synchronized(text) {
            text.append(msg).append("\n")
        }
    }

    fun stop() {
        isRunning = false
        flush()
    }



    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    private fun flush() {
        // 将日志缓存转为文本
        val string = synchronized(text) {
            val temp = text.toString()
            text.clear()
            return@synchronized temp
        }

        // 追加日志到文件
        logFile.appendText(string, Charset.defaultCharset())
    }



    /* ======================================================= */
    /* Inner Class                                             */
    /* ======================================================= */

    class Config(
        /** 多久 flush 一次日志到文件中 */
        val flushPeriodMills: Long
    )
}