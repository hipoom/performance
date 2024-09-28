@file:Suppress("MemberVisibilityCanBePrivate")

package com.hipoom.performance.looper

import android.os.SystemClock
import android.util.Log
import android.util.Printer
import com.hipoom.holder.Callbacks

/**
 * @author ZhengHaiPeng
 * @since 2024/9/28 00:22
 *
 */
object HipoomPrinter : Printer {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 标记是否需要统计 message 执行耗时。
     */
    var isMsgTimingEnable: Boolean = false

    /**
     * 保存外部添加的回调。
     */
    val callbacks = Callbacks<Printer>()

    /**
     * 开始执行消息的时刻。
     */
    var beginTimestampMills = 0L



    /* ======================================================= */
    /* Override/Implements Methods                             */
    /* ======================================================= */

    override fun println(x: String?) {
        callbacks.notifyAll { it.println(x) }

        if (!isMsgTimingEnable) {
            return
        }

        val isBegin = x?.startsWith(">>>>> Dispatching to") ?: false
        if (isBegin) {
            beginTimestampMills = SystemClock.elapsedRealtime()
            return
        }

        val isEnd = x?.startsWith("<<<<< Finished to") ?: false
        if (isEnd) {
            val duration = SystemClock.elapsedRealtime() - beginTimestampMills
            Log.i("ZHP_TEST", "消息执行耗时:$duration")
        }
    }

}