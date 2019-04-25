package com.prsioner.androidaidldemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prsioner.androidaidldemo.service.HandlerWorkService;

import java.lang.ref.WeakReference;

import javax.xml.transform.Result;

public class AsyncActivity extends AppCompatActivity {


    private HandlerThread handlerThread;

    private Button btnStartLoad;
    private Button btnCancelLoad;
    private TextView tvProgress;
    private MyAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        initView();

        //HandlerThreadTest();

        AsyncTaskTest();

        //IntentServiceTest();
    }

    private void initView() {

        btnStartLoad = findViewById(R.id.btn_start_load);
        btnCancelLoad = findViewById(R.id.btn_cancel_load);

        tvProgress = findViewById(R.id.tv_progress);

        btnCancelLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAsyncTask.onCancelled();
            }
        });
        btnStartLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //步骤3：手动调用execute(Params... params) 从而执行异步线程任务
                //注：
                //a. 必须在UI线程中调用
                //b. 同一个AsyncTask实例对象只能执行1次，若执行第2次将会抛出异常

                myAsyncTask.execute();
            }
        });
    }

    /**
     * IntentService
     */
    private void IntentServiceTest() {

        startService(new Intent(AsyncActivity.this, HandlerWorkService.class));
    }


    /**
     * 步骤2：创建AsyncTask子类的实例对象（即 任务实例）
     * 注：AsyncTask子类的实例必须在UI线程中创建
     */

    private void AsyncTaskTest() {

        myAsyncTask = new MyAsyncTask(this);

    }

    private int count = 1;

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        /**
         * 弱引用是允许被gc回收的
         **/
        private final WeakReference<AsyncActivity> weakActivity;

        MyAsyncTask(AsyncActivity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }


        /**
         * 方法1：onPreExecute():执行 线程任务前的操作
         **/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvProgress.setText("加载中...");
        }

        /**
         * 自定义的线程任务:可调用publishProgress()显示进度, 之后将执行onProgressUpdate()
         **/
        @Override
        protected String doInBackground(String... voids) {
            count++;
            while (count < 100) {
                count++;
                try {
                    Thread.sleep(200);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvProgress.setText("加载中..." + values[0] + "%");
        }


        /**
         * 方法4：onPostExecute():接收线程任务执行结果、将执行结果显示到UI组件
         * 注：必须复写，从而自定义UI操作
         **/

        @Override
        protected void onPostExecute(String result) {
            // UI操作
            tvProgress.setText("加载完成");

            /*activity没了,就可以结束线程，不再持有activity 的引用了**/
            AsyncActivity activity = weakActivity.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }

        /**
         * 方法5：onCancelled():将异步任务设置为：取消状态
         **/
        @Override
        protected void onCancelled() {
            tvProgress.setText("已取消！");
        }
    }

    /**
     * handlerThread
     */
    private void HandlerThreadTest() {

        // 步骤1：创建HandlerThread实例对象
        // 传入参数 = 线程名字，作用 = 标记该线程
        handlerThread = new HandlerThread("handlerThread");
        //步骤2：启动线程
        handlerThread.start();


        // 步骤3：创建工作线程Handler & 复写handleMessage（）
        // 作用：关联HandlerThread的Looper对象、实现消息处理操作 & 与其他线程进行通信
        // 注：消息处理操作（HandlerMessage（））的执行线程 = mHandlerThread所创建的工作线程中执行

        Handler workHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Log.e("AsyncActivity", (String) msg.obj);
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
        myAsyncTask.cancel(true);
        //handlerThread.quit();
    }
}
