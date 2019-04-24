package com.prsioner.androidaidldemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * @author Create by QingLin.
 * @date 2019/4/24 20:17
 * description:
 */
public class HandlerWorkService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HandlerWorkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}
