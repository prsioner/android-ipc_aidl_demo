package com.prsioner.androidaidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.prsioner.androidaidldemo.IMusicService;
import com.prsioner.androidaidldemo.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by QingLin.
 * @date 2019/4/23 14:56
 * description:
 */
public class MusicService extends Service {

    private String tag = MusicService.class.getSimpleName();
    private List<Person> personList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(tag,"-----onCreate()");
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


    private final IMusicService.Stub iBinder = new IMusicService.Stub(){

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
