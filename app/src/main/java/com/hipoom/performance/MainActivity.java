package com.hipoom.performance;

import android.os.Bundle;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hipoom.hook.JavaHook;
import com.hipoom.hook.adapter.pine.PineInitializer;
import com.hipoom.performance.timing.TimingRecorder;
import com.hipoom.performance.timing.TimingRecorder.Frame;
import com.hipoom.performance.timing.TimingRecorder.Listener;

public class MainActivity extends AppCompatActivity {

    public static class TestClass {

        TestClass()            {}

        TestClass(String name) {}

        public void objFun() {

        }

        public void objFun(String name) {

        }

        public static void staticMethod() {

        }

        public static void staticMethod(String name) {

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TimingRecorder.onFramePopListener = (depth, frame) -> Log.i("ZHP_TEST",
            "[" + depth + "] " + frame.methodDescription + " 执行完毕，耗时: " + (frame.endTime
                - frame.beginTime) + " 毫秒.");

        // 初始化 Pine Hook Style
        PineInitializer.init();
    }
}