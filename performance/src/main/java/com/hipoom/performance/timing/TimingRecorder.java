package com.hipoom.performance.timing;

import java.util.Stack;

import android.os.SystemClock;
import androidx.annotation.NonNull;

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

   public static Listener onFramePopListener = null;



   /* ======================================================= */
   /* Public Methods                                          */
   /* ======================================================= */

   public static void push(@NonNull String methodDescription) {
      Frame frame = new Frame();
      frame.beginTime = SystemClock.elapsedRealtime();
      frame.methodDescription = methodDescription;
      Stack<Frame> stack = stacks.get();
      if (stack == null) {
         stack = new Stack<>();
         stacks.set(stack);
      }
      stack.push(frame);
   }

   public static void pop() {
      final long now = SystemClock.elapsedRealtime();
      Stack<Frame> stack = stacks.get();
      if (stack == null) {
         return;
      }

      Frame frame = stack.pop();
      frame.endTime = now;

      Listener listener = onFramePopListener;
      if (listener != null) {
         listener.onFramePop(stack.size(), frame);
      }
   }



   /* ======================================================= */
   /* Inner Class                                             */
   /* ======================================================= */

   /**
    * TODO: 这里的 Frame 应该用享元模式优化。后续优化...
    */
   public static class Frame {

      public long beginTime;

      public long endTime;

      public String methodDescription;

   }

   public interface Listener {
      void onFramePop(int depth, @NonNull Frame frame);
   }

}
