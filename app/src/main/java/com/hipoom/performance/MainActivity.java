package com.hipoom.performance;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.hipoom.hook.adapter.pine.PineInitializer;
import com.hipoom.performance.looper.main.record.trace.MainLooperTracer;
import top.canyie.pine.PineConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PineInitializer.init();
        PineConfig.debug = false;
        PineConfig.debuggable = true;

        // MainLooperTracer.INSTANCE.startRecord(this);

        MainLooperTracer.INSTANCE.registryBroadcastReceiver(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.i("ZHP_TEST", "这是主动加的消息，堆栈：\n" + Log.getStackTraceString(new Throwable()));
        }, 3000);

    }
}