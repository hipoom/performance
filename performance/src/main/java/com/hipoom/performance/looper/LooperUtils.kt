package com.hipoom.performance.looper

import android.os.Looper
import android.util.Printer
import com.hipoom.performance.reflect.ReflectUtils

/**
 * 这个类用于替换 Looper 中的 Printer.
 * 可以通过调用 LooperUtils#enableMessageTiming() 将每个 Message 的耗时打印出来。
 * 这个类并不能定位具体的问题，只能打印 Message 执行耗时，且不能定位到是哪个 Message。
 *
 * @author ZhengHaiPeng
 * @since 2024/9/28 00:18
 */
object LooperUtils {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 是否已经替换过了
     */
    private var hasReplaced = false



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    /**
     * 将系统的 printer 替换为 HipoomPrinter.
     */
    fun replaceMainLooper() {
        val mainLooper = Looper.getMainLooper()

        // 取出旧的 printer
        val oldPrinter = ReflectUtils.getValue(Looper::class.java, "mLogging", mainLooper) as? Printer

        // 如果旧的 Printer 就是 HipoomPrinter，不再替换。
        if (oldPrinter == HipoomPrinter) {
            return
        }

        // 把旧的加入到 HipoomPrinter.callbacks 中
        oldPrinter?.also { HipoomPrinter.callbacks.add(it) }

        // 更新
        mainLooper.setMessageLogging(HipoomPrinter)

        // 标记为已经替换了
        hasReplaced = true
    }

    /**
     * 启动 message 执行耗时的监听。
     */
    fun enableMessageTiming() {
        HipoomPrinter.isMsgTimingEnable = true
    }

}