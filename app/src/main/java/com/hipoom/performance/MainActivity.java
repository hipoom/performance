package com.hipoom.performance;

import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.hipoom.hook.adapter.pine.PineInitializer;
import com.hipoom.performance.timing.Config;
import com.hipoom.performance.timing.Indent;
import com.hipoom.performance.timing.TimingRecorder;
import com.test.TestTimingRecording;

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

        TimingRecorder.init(this, new Config(
            0,
            1000,
            true
        ));

        //TimingRecorder.onFramePopListeners.add((depth, frame) -> {
        //    Log.i(
        //        "ZHP_TEST",
        //        Indent.of(depth) + frame.getMethodDescription() + " 执行完毕，耗时: " + frame.getCostMills() + " 毫秒."
        //    );
        //});


        // 初始化 Pine Hook Style
        PineInitializer.init();

        TestTimingRecording.test();
    }
}