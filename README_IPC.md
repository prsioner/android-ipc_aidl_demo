android 进程间通信的方式
1.Intent
2.文件共享
3.AIDL
4.Messenger
5.ContentProvider
6.RemoteViews
7.socket



AIDL原理性介绍
1. 举一个使用aidl 进行IPC通信的例子
MainActivity 和PersonService 分属于两个进程，我们用aidl 进行双向通信
完成 MainActivity创建person对象传到service 中进行保存，activity主动向service
拿所有数据，service以集合的方式返回所有数据
   
我们定义的IPersonService.aidl (aidl 文件路径要和package定义的包名路劲下)

    // IPersonService.aidl
     package com.prsioner.androidaidldemo;
     
     // Declare any non-default types here with import statements
     import com.prsioner.androidaidldemo.Person;
     interface IPersonService {
             void savePersonInfo(in Person person);
             List<Person> getAllPerson();
     }
     
Person.aidl

    // Person.aidl
    package com.prsioner.androidaidldemo;
    
    // Declare any non-default types here with import statements
    
    parcelable Person;
 
编译后自动生成的接口类IPersonService 在build/generated/source/aidl/....    
  
activity 作为client 端通过bindService启动service进程，传入一个ServiceConnection 来接受服务端在
客户端的引用 iService

    IPersonService iService;
    ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(tag,"onServiceConnected");
                iService = IPersonService.Stub.asInterface(service);
            }
    
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(tag,"onServiceDisconnected()");
            }
    };
    
activity 发送数据和接受数据

    iService.savePersonInfo(person);

    List<Person> personList = iService.getAllPerson();
    
    
service 服务端的部分代码：
    
    public IBinder onBind(Intent intent) {
            return iBinder;
        }
    
    
    private final IPersonService.Stub iBinder = new IPersonService.Stub(){
    
    @Override
    public void savePersonInfo(Person person) throws RemoteException {
           if(person !=null){
                    personList.add(person);
           }
           }
    
           @Override
           public List<Person> getAllPerson() throws RemoteException {
                return personList;
           }
    };
    
    
我们来看aidl 完成通信的具体实现过程
    
    package com.prsioner.androidaidldemo;
    public interface IPersonService extends android.os.IInterface
    {
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.prsioner.androidaidldemo.IPersonService
    {
    private static final java.lang.String DESCRIPTOR = "com.prsioner.androidaidldemo.IPersonService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
    this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.prsioner.androidaidldemo.IPersonService interface,
     * generating a proxy if needed.
     */
    public static com.prsioner.androidaidldemo.IPersonService asInterface(android.os.IBinder obj)
    {
    if ((obj==null)) {
    return null;
    }
    android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
    if (((iin!=null)&&(iin instanceof com.prsioner.androidaidldemo.IPersonService))) {
    return ((com.prsioner.androidaidldemo.IPersonService)iin);
    }
    return new com.prsioner.androidaidldemo.IPersonService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
    return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
    java.lang.String descriptor = DESCRIPTOR;
    switch (code)
    {
    case INTERFACE_TRANSACTION:
    {
    reply.writeString(descriptor);
    return true;
    }
    case TRANSACTION_savePersonInfo:
    {
    data.enforceInterface(descriptor);
    com.prsioner.androidaidldemo.Person _arg0;
    if ((0!=data.readInt())) {
    _arg0 = com.prsioner.androidaidldemo.Person.CREATOR.createFromParcel(data);
    }
    else {
    _arg0 = null;
    }
    this.savePersonInfo(_arg0);
    reply.writeNoException();
    return true;
    }
    case TRANSACTION_getAllPerson:
    {
    data.enforceInterface(descriptor);
    java.util.List<com.prsioner.androidaidldemo.Person> _result = this.getAllPerson();
    reply.writeNoException();
    reply.writeTypedList(_result);
    return true;
    }
    default:
    {
    return super.onTransact(code, data, reply, flags);
    }
    }
    }
    private static class Proxy implements com.prsioner.androidaidldemo.IPersonService
    {
    private android.os.IBinder mRemote;
    Proxy(android.os.IBinder remote)
    {
    mRemote = remote;
    }
    @Override public android.os.IBinder asBinder()
    {
    return mRemote;
    }
    public java.lang.String getInterfaceDescriptor()
    {
    return DESCRIPTOR;
    }
    @Override public void savePersonInfo(com.prsioner.androidaidldemo.Person person) throws android.os.RemoteException
    {
    android.os.Parcel _data = android.os.Parcel.obtain();
    android.os.Parcel _reply = android.os.Parcel.obtain();
    try {
    _data.writeInterfaceToken(DESCRIPTOR);
    if ((person!=null)) {
    _data.writeInt(1);
    person.writeToParcel(_data, 0);
    }
    else {
    _data.writeInt(0);
    }
    mRemote.transact(Stub.TRANSACTION_savePersonInfo, _data, _reply, 0);
    _reply.readException();
    }
    finally {
    _reply.recycle();
    _data.recycle();
    }
    }
    @Override public java.util.List<com.prsioner.androidaidldemo.Person> getAllPerson() throws android.os.RemoteException
    {
    android.os.Parcel _data = android.os.Parcel.obtain();
    android.os.Parcel _reply = android.os.Parcel.obtain();
    java.util.List<com.prsioner.androidaidldemo.Person> _result;
    try {
    _data.writeInterfaceToken(DESCRIPTOR);
    mRemote.transact(Stub.TRANSACTION_getAllPerson, _data, _reply, 0);
    _reply.readException();
    _result = _reply.createTypedArrayList(com.prsioner.androidaidldemo.Person.CREATOR);
    }
    finally {
    _reply.recycle();
    _data.recycle();
    }
    return _result;
    }
    }
    static final int TRANSACTION_savePersonInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getAllPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }
    public void savePersonInfo(com.prsioner.androidaidldemo.Person person) throws android.os.RemoteException;
    public java.util.List<com.prsioner.androidaidldemo.Person> getAllPerson() throws android.os.RemoteException;
    }
    
    
**IInterface接口:** 所有用Binder传输数据的接口都必须继承这个接口

**Stub:** 继承自Binder 实现我们定义的aidl类接口IPersonService,其实就是作为服务端的一个binder对象
Stub 中有一个DESCRIPTOR,它是Binder的唯一标识，其中两个int常量是用来标识我们在接口中定义的方法的
    
**asInterface()方法** 用于将服务端的Binder对象转换为客户端所需要的接口对象,该过程区分进程，如果进程一样，
就返回服务端Stub对象本身，否则呢就返回封装后的Stub.Proxy对象(服务端在客户度的代理对象)

**onTransact() 方法** 是运行在服务端的Binder线程中的，当客户端发起远程请求后，在底层封装后会交由此方法来处理。
通过code来区分客户端请求的方法   

**Proxy代理对象类**我们主要看一下我们定义的方法savePersonInfo()和getAllPerson()就可以了，这两个方法都是运行在客户端，
当客户端发起远程请求时，_data会写入参数，然后调用transact方法发起RPC(远程过程调用)请求，同时挂起当前线程，
然后服务端的onTransact方法就会被调起，直到RPC过程返回后，当前线程继续执行，并从_reply取出返回值（如果有的话），并返回结果
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        