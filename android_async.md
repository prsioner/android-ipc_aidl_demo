Android开发中各种异步机制

1.AsyncTask

2.HandlerThread
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
    
3.IntentService
> 1.本质上IntentService也是开了一个线程，但是IntentService是继承自Service的，所以根据Android系统Kill Application的机制，
使用IntentService的应用的优先级更高一点
>2.我们知道Service可以通过startService和bindService这两种方式启动。IntentService自然也是可以通过上面两种方式启动。
但是呢，是不建议使用 bindService 去启动的,因为bindService 启动的服务不会调用onStart()生命周期方法，而启动工作线程
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

4.AsyncQueryHandler


使用选择：
1.频率不高，比较重度，且有进度交互的，可以考虑使用AsyncTask，同时注意执行时，调用其并发性执行接口；

2.中轻度队列性子任务，考虑使用HandlerThread；

3.分叉性的重度后台任务，考虑使用IntentService；

4. 涉及ContentProvider的数据库操作，考虑使用AsyncQueryHandler。