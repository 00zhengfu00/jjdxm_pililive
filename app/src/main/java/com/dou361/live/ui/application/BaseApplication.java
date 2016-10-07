package com.dou361.live.ui.application;

import android.app.Application;
import android.os.Handler;

import com.dou361.baseutils.utils.LogUtils;
import com.dou361.baseutils.utils.UtilsManager;
import com.pili.pldroid.streaming.StreamingEnv;

import org.litepal.LitePalSDK;

/**
 * Created by wei on 2016/5/27.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        LitePalSDK.init(this);
        UtilsManager.init(this, "", new Handler(), Thread.currentThread());
        UtilsManager.getInstance().setDebugEnv(true);
        UtilsManager.getInstance().setLogLevel(LogUtils.LogType.LEVEL_ERROR);
        StreamingEnv.init(getApplicationContext());
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
