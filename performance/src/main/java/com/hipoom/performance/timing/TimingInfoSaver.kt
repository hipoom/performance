package com.hipoom.performance.timing

import android.os.Looper
import com.hipoom.Files
import java.io.File
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.ArrayList

/**
 * 保存日志文件到磁盘。
 *
 * @author ZhengHaiPeng
 * @since 2024/8/4 15:47
 */
internal object TimingInfoSaver {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 所有线程的缓存，包含主线程。
     */
    private val buffer = LinkedList<String>()

    /**
     * 仅主线程的缓存。
     */
    private val mainBuffer = LinkedList<String>()

    /**
     * 定时将缓存写入文件。
     */
    private var timer: Timer? = null

    /**
     * 如果要记录到本地，日志会保存到这个目录下。
     */
    private var workspace: File? = null



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    @JvmStatic
    fun init(workspace: File, config: Config) {
        this.workspace = workspace

        // If init be called multiple times, the old timer needs to be canceled.
        this.timer?.cancel()

        // Start a timer
        val temp = Timer()
        temp.schedule(object: TimerTask() {
            override fun run() {
                flushBuffers()
            }
        }, config.flushPeriodMills, config.flushPeriodMills)
        this.timer = temp
    }

    /**
     * 保存信息到文件。
     *
     * @param timestamp 函数执行结束的时间戳；
     * @param duration 距离 init 的时间戳；
     * @param thread 函数执行所在的线程；
     * @param depth 函数调用栈的深度；
     * @param methodDes 函数的描述；
     * @param costMills 函数执行消耗的时间。
     */
    fun appendBuffer(timestamp: Long, duration: Long, thread: Thread, depth: Int, methodDes: String, costMills: Long) {
        val msg = "[$timestamp] [$duration] [${thread.name}] [$depth] $methodDes : $costMills\n"

        // 保存到完整日志中
        synchronized(buffer) {
            buffer.add(msg)
        }

        // 如果是主线程，额外保存到 mainBuffer 中
        if (thread == Looper.getMainLooper().thread) {
            synchronized(mainBuffer) {
                mainBuffer.add(msg)
            }
        }
    }



    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    private fun flushBuffers() {
        // 将 Buffer 复制一份，避免卡住写入线程
        val tempBuffer = synchronized(buffer) {
            val temp = ArrayList(buffer)
            buffer.clear()
            temp
        }
        // 写入完整日志
        val allLog = File(workspace, "timings.txt")
        Files.createNewFileIfNotExist(allLog)
        allLog.appendText(tempBuffer.joinToString(separator = "") { it })

        // 将 mainBuffer 也复制一份
        val tempMainBuffer = synchronized(mainBuffer) {
            val temp = ArrayList(mainBuffer)
            mainBuffer.clear()
            temp
        }
        // 写入主线程的日志
        val mainLog = File(workspace, "main.txt")
        Files.createNewFileIfNotExist(mainLog)
        mainLog.appendText(tempMainBuffer.joinToString(separator = "") { it })
    }
}