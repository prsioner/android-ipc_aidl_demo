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

import com.prsioner.androidaidldemo.service.PersonService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String tag = MainActivity.class.getSimpleName();
    private Button addDataBtn;
    private Button showDataBtn;
    private TextView tvShowData;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initView();

        //bindService();
        startService(new Intent(MainActivity.this, PersonService.class));

    }

    private void bindService(){
        Intent bIntent = new Intent(MainActivity.this, PersonService.class);
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
                    iService.savePersonInfo(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        showDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Person> personList = iService.getAllPerson();
                    Log.e(tag,"personList.size()="+personList.size());
                    for (int i=0;i<personList.size();i++){
                        Log.e(tag,personList.get(i).toString());
                        tvShowData.setText("第一人信息:"+personList.get(0).toString());
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this,AsyncActivity.class);
                startActivity(intent);
            }
        });

    }


}
