package com.hipoom.performance.timing;

import java.io.File;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 这个类会帮助您自动化地初始化 TimingRecorder.
 * 为了方便你自主选择是否需要使用这个功能，AutoInitProvider 并没有默认在 AndroidManifest.xml 中注册。
 * 所以，如果你需要使用这个自动化的初始化功能，你需要手动在你的 app module 中的 AndroidManifest.xml 中
 * 注册这个 Provider. 例如：
 * {@code
 * <provider
 *      android:name="com.hipoom.performance.timing.AutoInitProvider"
 *      android:authorities="hipoom"
 *      android:enabled="true"
 *      android:exported="false" />
 * }
 * <br/>
 * 因为有的大型项目使用了组件化，app module 中并没有代码，或者没有合适的地方可以添加初始化逻辑。
 * 所以提供了这个 Provider.
 */
public class AutoInitProvider extends ContentProvider {
    public AutoInitProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        // 默认配置： 超过 0 毫秒的函数会被记录、每隔 1 秒写入一次日志、不需要打印到 logcat 中。
        Config defaultConfig = new Config(
            /*recordThresholdMills =*/ 0,
            /*flushPeriodMills     =*/ 1000,
            /*needPrintLogcat      =*/ false
        );

        Log.i("Hipoom", "[AutoInitProvider] 即将初始化 TimingRecorder.");
        File workspace = TimingRecorder.init(context, defaultConfig);
        Log.i("Hipoom", "[AutoInitProvider] 初始化完成，workspace = " + workspace.getAbsolutePath());

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}