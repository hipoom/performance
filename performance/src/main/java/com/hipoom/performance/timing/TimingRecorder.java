package com.hipoom.performance.timing;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Process;
import android.util.Log;
import androidx.annotation.NonNull;
import com.hipoom.holder.Callbacks;

/**
 * @author ZhengHaiPeng
 * @since 2024/7/28 21:50
 */
public class TimingRecorder {

   /* ======================================================= */
   /* Fields                                                  */
   /* ======================================================= */

   /**
    * 记录调用的栈。
    */
   private static final ThreadLocal<Stack<Frame>> stacks = new ThreadLocal<>();

   /**
    * 每一个方法执行完毕的回调。
    */
   public static final Callbacks<Listener> onFramePopListeners = new Callbacks<>();

   /**
    * Whether to save timing's into file or not.
    * The default value is true.
    * Your can change this value by calling {@link #startSave} or {@link #stopSave}.
    */
   private static boolean needSave = true;

   /**
    * Only methods call that take longer than this field will be record.
    */
   private static long minCostForRecord = 0L;

   private static boolean needPrintLogcat = false;

   private static long initTimestamp = 0L;


   /* ======================================================= */
   /* Public Methods                                          */
   /* ======================================================= */

   /**
    * 初始化。
    * 将创建好日志保存目录，日志会被保存到以下两个文件中：
    * (1) ${workspace}/main.txt, 这里保存的是主线程的日志。
    * (2) ${workspace}/timings.txt, 这里保存的是所有线程（包括主线程的日志）。
    * 其中，${workspace} 是当前函数的返回值。
    * <br/>
    * 多次调用这个函数不会导致功能异常。 但是会刷新 initTimestamp.
    *
    * @param context 用于获取磁盘目录。
    * @return 本次初始化的日志文件。
    */
   @NonNull
   public static File init(@NonNull Context context, @NonNull Config config) {
      initTimestamp = System.currentTimeMillis();
      minCostForRecord = config.getRecordThresholdMills();
      needPrintLogcat = config.getNeedPrintLogcat();

      // 获取当前进程号
      int pid = Process.myPid();
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS");
      String date = sdf.format(initTimestamp);

      // 拼接日志文件保存目录
      File workspace = context.getExternalFilesDir("hipoom/performance/timing/" + date + "/" + pid);
      assert workspace != null;
      Log.i("Hipoom-Performance", "workspace: " + workspace.getAbsolutePath());

      // 初始化日志保存
      TimingInfoSaver.init(workspace, config);

      return workspace;
   }

   /**
    * Stop saving the timing's info.
    */
   public static void stopSave() {
      needSave = false;
   }

   /**
    * Star saving the timing's info.
    */
   public static void startSave() {
      needSave = true;
   }

   /**
    * 这个方法是由 processor 在编译时插桩调用的。
    */
   public static void push(@NonNull String methodDescription) {
      Frame frame = Frame.obtain();
      frame.setBeginMills(System.currentTimeMillis());
      frame.setMethodDescription(methodDescription);

      Stack<Frame> stack = stacks.get();
      if (stack == null) {
         stack = new Stack<>();
         stacks.set(stack);
      }
      stack.push(frame);
   }

   /**
    * 这个方法是由 processor 在编译时插桩调用的。
    */
   public static void pop() {
      final long now = System.currentTimeMillis();
      Stack<Frame> stack = stacks.get();
      if (stack == null) {
         return;
      }

      Frame frame = stack.pop();
      frame.setEndMills(now);

      // 回调每一个监听者
      onFramePopListeners.notifyAll(l -> l.onFramePop(stack.size(), frame));

      // save timing into file.
      if (needSave && frame.getCostMills() >= minCostForRecord) {
         TimingInfoSaver.INSTANCE.appendBuffer(
             now,
             now - initTimestamp,
             Thread.currentThread(),
             stack.size(),
             frame.getMethodDescription(),
             frame.getCostMills()
         );
      }

      if (needPrintLogcat) {
         String msg = "[" + stack.size() + "] " + frame.getMethodDescription() + " : " + frame.getCostMills();
         Log.i("Hipoom-Performance", "[Timing] " + msg);
      }

      // recycle to object pool
      frame.recycle();
   }

}
