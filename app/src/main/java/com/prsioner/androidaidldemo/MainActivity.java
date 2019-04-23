package com.prsioner.androidaidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prsioner.androidaidldemo.service.MusicService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String tag = MainActivity.class.getSimpleName();
    private Button addDataBtn;
    private Button showDataBtn;
    private TextView tvShowData;

    IMusicService musicService;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(tag,"onServiceConnected");
            musicService = IMusicService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(tag,"onServiceDisconnected()");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initView();

        bindService();


    }

    private void bindService(){
        Intent bIntent = new Intent(MainActivity.this, MusicService.class);
        bindService(bIntent, serviceConnection,BIND_AUTO_CREATE);
    }
    private void initView(){

        addDataBtn = findViewById(R.id.btn_add_data);
        showDataBtn = findViewById(R.id.btn_show_data);
        tvShowData = findViewById(R.id.tv_show_data);

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = new Person();
                person.setName("Kevin");
                person.setAge(25);
                person.setTelNumber("13888888888");
                try {
                    musicService.savePersonInfo(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        showDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Person> personList = musicService.getAllPerson();
                    for (int i=0;i<personList.size();i++){
                        Log.e(tag,personList.get(i).toString());
                        tvShowData.setText("第一人信息:"+personList.get(0).toString());
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
