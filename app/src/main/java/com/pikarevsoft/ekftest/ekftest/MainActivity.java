package com.pikarevsoft.ekftest.ekftest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import static com.pikarevsoft.ekftest.ekftest.Public.BROADCAST_UPDATE_LIST_WORKER;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class MainActivity extends AppCompatActivity {

    RecyclerView mainList;
    ImageButton btnAddNewWorker;
    Context context;
    String DBName = "EKF";
    final int VERSION_DB = 1;
    BroadcastReceiver broadcastReceiverUpdateList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        DBHelper dbHelper = new DBHelper(this, DBName, null, VERSION_DB);
        db = dbHelper.getWritableDatabase();

        mainList = findViewById(R.id.main_list);
        mainList.setHasFixedSize(true);
        mainList.setLayoutManager(new LinearLayoutManager(this));

        btnAddNewWorker = findViewById(R.id.btn_add_new_worker);
        btnAddNewWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NewWorkerActivity.class));
            }
        });

        broadcastReceiverUpdateList = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateList();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(BROADCAST_UPDATE_LIST_WORKER);
        registerReceiver(broadcastReceiverUpdateList, broadcastFilter);

        updateList();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (broadcastReceiverUpdateList != null)
            unregisterReceiver(broadcastReceiverUpdateList);
    }

    void updateList(){

        ArrayList<Worker> mainArray;

        mainArray = new ArrayList<>();
        Cursor cursor = db.query(Worker.sTable, null, null, null,null, null, null);
        if (cursor.moveToFirst()){
            do{
                Worker worker = new Worker();
                worker.setId(cursor.getInt(cursor.getColumnIndex(Worker.sId)));
                worker.setFamily(cursor.getString(cursor.getColumnIndex(Worker.sFamily)));
                worker.setName(cursor.getString(cursor.getColumnIndex(Worker.sName)));
                worker.setPatronymic(cursor.getString(cursor.getColumnIndex(Worker.sPatronymic)));
                worker.setDateBirthday(cursor.getLong(cursor.getColumnIndex(Worker.sDateBirthday)));
                worker.setProf(cursor.getString(cursor.getColumnIndex(Worker.sProf)));
                worker.setnChild(getNChildren(worker.getId()));
                mainArray.add(worker);
            }while (cursor.moveToNext());
        }
        cursor.close();

        mainList.setAdapter(new MainAdapter(mainArray));

    }

    private int getNChildren(long id) {
        Cursor cursor = db.query(Child.sTable, new String[]{Child.sId}, Child.sParent+"=?",new String[]{Long.toString(id)}, null, null, null);
        int count;
        if (cursor.moveToFirst()) count = cursor.getCount();
            else count = 0;
        cursor.close();
        return count;
    }

}
