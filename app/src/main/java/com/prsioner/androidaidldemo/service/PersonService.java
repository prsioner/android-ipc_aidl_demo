package com.prsioner.androidaidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.prsioner.androidaidldemo.IPersonService;
import com.prsioner.androidaidldemo.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by QingLin.
 * @date 2019/4/23 14:56
 * description:
 */
public class PersonService extends Service {

    private String tag = PersonService.class.getSimpleName();
    private List<Person> personList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(tag,"-----onCreate()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(tag,"-----onStart()----");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(tag,"---onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
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
}
