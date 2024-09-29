package com.hipoom.performance.looper.main.record.trace

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import android.os.SystemClock
import android.util.Log
import androidx.annotation.Keep
import com.hipoom.holder.ObjectPool
import com.hipoom.hook.JavaHook
import com.hipoom.hook.adapter.HookStyleFactory
import com.hipoom.performance.utils.LogSaver
import me.weishu.reflection.Reflection
import java.io.File
import java.text.SimpleDateFormat
import java.util.Arrays


/**
 * @author ZhengHaiPeng
 * @since 9/29/24 7:24 PM
 */
@SuppressLint("PrivateApi")
@Keep
object MainLooperTracer {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * Message 对象与 enqueueMessage 时的堆栈。
     */
    private val message2Trace = HashMap<Message, MessageInfo>()

    /**
     * MessageInfo 的对象池，避免频繁创建 MessageInfo 对象。
     */
    private val messageInfoPool = ObjectPool {
        return@ObjectPool MessageInfo()
    }

    /**
     * 获取主线程的 Looper 对象。
     */
    private val mainLooper = Looper.getMainLooper()

    /**
     * 主线程的 Queue 对象。
     */
    private val mainQueue: MessageQueue

    /**
     * 开始处理消息的时刻
     */
    private var onBeginHandleMessageMills = 0L

    /**
     * 正在执行消息的信息。
     * 这个信息是在 dispatchMessage beforeCall 的时候获取的。
     */
    private var currentInfo: MessageInfo? = null

    /**
     * 日志记录工具
     */
    private lateinit var logSaver: LogSaver

    /**
     * 是否正在记录中
     */
    private var isTracing = false

    /**
     * 是否已经执行过解封反射限制的操作了
     */
    private var didUnsealReflect = false



    /* ======================================================= */
    /* Constructors or Instance Creator                        */
    /* ======================================================= */

    init {
        @Suppress("LiftReturnOrAssignment")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainQueue = Looper.getMainLooper().queue
        }
        else {
            val mainLooper = Looper.getMainLooper()
            val mQueueFiled = Looper::class.java.getDeclaredField("mQueue")
            mainQueue = mQueueFiled.get(mainLooper) as MessageQueue
        }


    }



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    @SuppressLint("SimpleDateFormat")
    @Synchronized
    fun startRecord(context: Context) {
        val hooker = HookStyleFactory.getAnyOne()
        if (hooker == null) {
            Log.i("Hipoom", "[Warn] Can't find any hook impl.")
            return
        }

        if (isTracing) {
            return
        }
        isTracing = true

        // 解封反射限制，否则会找不到被系统隐藏的方法，例如 MessageQueue # enqueueMessage.
        if (!didUnsealReflect) {
            val code = Reflection.unseal(context)
            if (code == 0) {
                Log.i("Hipoom", "解封反射限制成功！")
            }
            didUnsealReflect = true
        }

        // 初始化日志
        val file = context.getExternalFilesDir("hipoom")!!
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS")
        val date = sdf.format(System.currentTimeMillis())
        val log = File(file, "/performance/looper/main-tracer/${date}.txt")
        logSaver = LogSaver(log, LogSaver.Config(3000))

        // Hook 消息入队列的时机
        hookEnqueue()

        // Hook 消息出队列的时机
        hookDispatchMessage()
    }

    fun stopRecord() {
        isTracing = false
    }

    /**
     * 注册广播接收器。
     * 可以通过 adb 命令打开、关闭记录。
     */
    fun registryBroadcastReceiver(context: Context) {
        context.registerReceiver(AdbCommandReceiver(), IntentFilter().apply {
            addAction("hipoom-performance-main-tracer")
        })
    }



    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    /**
     * hook 消息入队列的时机。
     */
    private fun hookEnqueue() {
        JavaHook
            .forClass(MessageQueue::class.java)
            .method.named("enqueueMessage").all()
            .doBefore {
                // 如果当前不是主线程的 MessageQueue，或者没有在记录，不处理
                if (it.self != mainQueue || !isTracing) {
                    return@doBefore
                }

                val info = messageInfoPool.obtain()
                info.addTimestamp = SystemClock.elapsedRealtime()

                // 获取 message 对象
                val msg = it.args?.get(0) as? Message ?: return@doBefore
                // 创建堆栈
                info.addTrace = Throwable()

                // 加入到缓存中
                message2Trace[msg] = info
            }
            .hook()
    }

    /**
     * hook 消息出队列的时机。
     */
    private fun hookDispatchMessage() {
        JavaHook
            .forClass(Handler::class.java)
            .method.named("dispatchMessage").all()
            .doBefore {
                // 如果不是主线程的消息，不处理
                if ((it.self as Handler).looper != mainLooper) {
                    return@doBefore
                }

                // 记录时间
                onBeginHandleMessageMills = SystemClock.elapsedRealtime()
            }
            .doAfter {
                // 如果不是主线程的消息，不处理
                if ((it.self as Handler).looper != mainLooper) {
                    return@doAfter
                }

                // 消息执行耗时
                val now = SystemClock.elapsedRealtime()
                val duration = now - onBeginHandleMessageMills

                // 消息对象
                val msg = it.args?.get(0) as Message

                // 获取缓存中的数据
                val info = message2Trace[msg] ?: return@doAfter

                // 从缓存中移除
                message2Trace.remove(msg)

                // 打印日志
                logSaver.append("\n\n[add: ${info.addTimestamp}] [begin: $onBeginHandleMessageMills] [end: $now] [duration: $duration] add trace:")

                Log.getStackTraceString(info.addTrace).split("\n").let { lines ->
                    lines.subList(7, lines.size)
                }.also { lines ->
                    val log = lines.joinToString(separator = "\n") { line -> line }
                    logSaver.append(log)
                }

                // 回收 info
                info.addTrace = null
                info.addTimestamp = 0
                messageInfoPool.recycle(info)
            }
            .hook()
    }

}