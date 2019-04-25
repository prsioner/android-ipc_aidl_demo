package com.prsioner.androidaidldemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Create by QingLin.
 * @date 2019/4/24 20:17
 * description:
 */
public class HandlerWorkService extends IntentService {

    private String tag = "HandlerWorkService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(tag,"onCreate()----");
    }

    public HandlerWorkService() {
        super("handlerWorkService111111");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(5000);
            Log.e(tag,"耗时操作执行完成--Thread.getName()="+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(tag,"onDestroy()----");
    }
}
