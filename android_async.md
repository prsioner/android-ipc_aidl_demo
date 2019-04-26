####Android开发中各种异步机制

#####1.AsyncTask
优点：
方便实现异步通信，不需使用 “任务线程（如继承Thread类） + Handler”的复杂组合
节省资源，采用线程池的缓存线程 + 复用线程，避免了频繁创建 & 销毁线程所带来的系统资源开销

我们写一个简单的示例：

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

执行方方法是在主线程中调用 myAsyncTask.execute();需要取消则调用 cancel()方法

注意：
AsyncTask产生的问题
   
>1.开启线程后，未结束，此时用户又一次，甚至多次开启线程，导致多次请求。
解决方式：将线程写为静态static。

>2.当用户开启线程后，退出界面，由于AsyncTask持有Activity的变量的实例，导致Activity无法被回收，从而导致内存泄漏
解决方式：采用弱引用的方式，将线程与Activity进行解耦。

>3.activity onDestroy()时，应主动取消调用取消任务: myAsyncTask.cancel(true);

>4.屏幕旋转或Activity在后台被系统杀掉等情况会导致Activity的重新创建，之前运行的AsyncTask会持有一个之前Activity的引用，
这个引用已经无效，这时调用onPostExecute()再去更新界面将不再生效

>5.AsyncTask支持并行和串行的执行异步任务，当想要串行执行时，直接执行execute()方法，如果需要并行执行，
则要执行executeOnExecutor(Executor executor ,Object... params),第一个是Executor（线程池实例），第二个是任务参数
eg：mytask = new MyAsyncTask().executeOnExecutor(Executors.newFixedThreadPool(1),imgUrls);
    
#####2.HandlerThread
用法浅析：
    
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
    
#####3.IntentService
> 1.IntentService会创建独立的worker线程来处理onHandleIntent()方法实现的代码，但是IntentService是继承自Service的，
所以根据Android系统Kill Application的机制，使用IntentService的应用的优先级更高一点
>2.Service可以通过startService和bindService这两种方式启动。IntentService自然也是可以通过上面两种方式启动。
不建议使用 bindService 去启动的,因为bindService 启动的服务不会调用onStart()生命周期方法，而启动工作线程
的方法正是在onStart()中，我们可以看IntentService的源码

    public abstract class IntentService extends Service {
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

    public void onCreate() {
        // TODO: It would be nice to have an option to hold a partial wakelock
        // during processing, and to have a static startService(Context, Intent)
        // method that would launch the service & hand off a wakelock.

        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }
    }
    
贴一下用IntentService来模拟一个耗时任务的实现代码：
    
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
我们会发现执行完耗时任务后，会自动调用stopService()执行onDestroy(),无需手动调用stopService()


#####4.AsyncQueryHandler

一个用来帮助简化处理异步ContentResolver查询的工具类
用法：
新建一个类继承AsyncQueryHandler类，并提供onXXXComplete方法的实现（可以实现任何一个或多个，当然你也可以一个也不实现，
如果你不关注操作数据库的結果），在你的实现中做一些对数据库操作完成的处理。


使用选择：

1.频率不高，比较重度，且有进度交互的，可以考虑使用AsyncTask，同时注意执行时，调用其并发性执行接口；

2.中轻度队列性子任务，考虑使用HandlerThread；

3.分叉性的重度后台任务，考虑使用IntentService；

4. 涉及ContentProvider的数据库操作，考虑使用AsyncQueryHandler。

####为什么要用ScheduledExecutorService 替代Timer

>1.Timer在执行定时任务时只会创建一个线程，所以如果存在多个任务，且任务时间过长，超过了两个任务的间隔时间，
会发生定时失效或者定时执行不准确
>2.如果TimerTask抛出RuntimeException，Timer会停止所有任务的运行
>3.Timer执行周期任务时依赖系统时间，如果当前系统时间发生变化会出现一些执行上的变化，ScheduledExecutorService基于时间
的延迟，不会由于系统时间的改变发生执行变化

ScheduleExecutorService 主要有四种线程池

1.newSingleThreadExecutor：单线程池，同时只有一个线程在跑。
2.newCachedThreadPool() ：回收型线程池，可以重复利用之前创建过的线程，运行线程最大数是Integer.MAX_VALUE。
3.newFixedThreadPool() ：固定大小的线程池，跟回收型线程池类似，只是可以限制同时运行的线程数量，超出的线程会在队列中等待
4.newScheduledThreadPool：创建一个定长线程池，支持定时及周期性任务执行

    private ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(4);
    private int timeCount = 1;
    private TimerTask timerTask;
    private void createThreadPool() {
        Log.e(tag,"createThreadPool");
        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                while (timeCount<100){
                    timeCount ++;
                    Log.e(tag,"timeCount:"+timeCount);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },1000,2000, TimeUnit.MILLISECONDS);