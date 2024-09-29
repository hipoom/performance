package com.hipoom.performance.looper.main.record.trace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * ADB 指令接收器。
 *
 * 可以通过以下指令，触发逻辑：
 *
 * adb shell am broadcast -a hipoom-performance-main-tracer --es is-record-main-looper-enable true
 *
 * @author ZhengHaiPeng
 * @since 9/29/24 7:14 PM
 */
class AdbCommandReceiver : BroadcastReceiver {

    /* ======================================================= */
    /* Constructors or Instance Creator                        */
    /* ======================================================= */

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor() : super() {
        Log.i("Hipoom", "[AdbCommandReceiver] <init>")
    }



    /* ======================================================= */
    /* Override/Implements Methods                             */
    /* ======================================================= */

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Hipoom", "[AdbCommandReceiver] 收到了广播")

        // 判断是否需要开启记录主线程的 Looper 消息添加堆栈
        val needStart = intent?.extras?.getBoolean("is-record-main-looper-enable") ?: return
        if (needStart) {
            MainLooperTracer.startRecord(context!!)
            Log.i("Hipoom", "已开启主线程任务记录")
        }
        else {
            MainLooperTracer.stopRecord()
            Log.i("Hipoom", "已关闭主线程任务记录")
        }

    }
}