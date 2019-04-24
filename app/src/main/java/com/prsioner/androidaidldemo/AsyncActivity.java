package com.prsioner.androidaidldemo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class AsyncActivity extends AppCompatActivity {


    private HandlerThread handlerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);


        HandlerThreadTest();
        
        AsyncTaskTest();
        
        IntentServiceTest();
    }

    private void IntentServiceTest() {
    }

    private void AsyncTaskTest() {
        
    }

    private void HandlerThreadTest() {

        // 步骤1：创建HandlerThread实例对象
        // 传入参数 = 线程名字，作用 = 标记该线程
        handlerThread = new HandlerThread("handlerThread");
        //步骤2：启动线程
        handlerThread.start();


        // 步骤3：创建工作线程Handler & 复写handleMessage（）
        // 作用：关联HandlerThread的Looper对象、实现消息处理操作 & 与其他线程进行通信
        // 注：消息处理操作（HandlerMessage（））的执行线程 = mHandlerThread所创建的工作线程中执行

        Handler workHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Log.e("AsyncActivity",(String)msg.obj);
                        break;

                    default:

                        break;
                }
            }
        };

        Message msg = Message.obtain();
        msg.what = 0;
        msg.obj = "B";
        //通过Handler发送消息到其绑定的消息队列
        workHandler.sendMessage(msg);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        handlerThread.quit();
    }
}
