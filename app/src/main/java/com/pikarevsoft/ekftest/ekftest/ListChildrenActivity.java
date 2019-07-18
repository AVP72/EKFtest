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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.pikarevsoft.ekftest.ekftest.Public.BROADCAST_UPDATE_LIST_CHILDREN;
import static com.pikarevsoft.ekftest.ekftest.Public.ERROR;
import static com.pikarevsoft.ekftest.ekftest.Public.FULL_PARENT_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class ListChildrenActivity extends AppCompatActivity {

    RecyclerView listChildren;
    long id;
    String fullNameParent;
    TextView txtParent;
    BroadcastReceiver broadcastReceiverUpdateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_children);

        Intent intent = getIntent();
        id = intent.getLongExtra(ID_INTENT, ERROR);
        fullNameParent = intent.getStringExtra(FULL_PARENT_INTENT);
        if (id == ERROR || fullNameParent == null){
            Toast.makeText(this, getString(R.string.error_read_line), Toast.LENGTH_LONG).show();
            return;
        }

        listChildren = findViewById(R.id.list_cildren_of_worker_recycler_view);
        listChildren.setHasFixedSize(true);
        listChildren.setLayoutManager(new LinearLayoutManager(this));

        txtParent = findViewById(R.id.list_cildren_parent);
        txtParent.setText(fullNameParent);

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
        broadcastFilter.addAction(BROADCAST_UPDATE_LIST_CHILDREN);
        registerReceiver(broadcastReceiverUpdateList, broadcastFilter);

        updateList();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (broadcastReceiverUpdateList != null)
            unregisterReceiver(broadcastReceiverUpdateList);

    }


    private void updateList() {

        ArrayList<Child> childArrayList = new ArrayList<>();

        Cursor cursor = db.query(Child.sTable, null, Child.sParent+"=?", new String[]{Long.toString(id)}, null, null,null);
        if (cursor.moveToFirst()){
            do{
                Child child = new Child();
                child.setId(cursor.getInt(cursor.getColumnIndex(Child.sId)));
                child.setParent(cursor.getInt(cursor.getColumnIndex(Child.sParent)));
                child.setFamily(cursor.getString(cursor.getColumnIndex(Child.sFamily)));
                child.setName(cursor.getString(cursor.getColumnIndex(Child.sName)));
                child.setPatronymic(cursor.getString(cursor.getColumnIndex(Child.sPatronymic)));
                child.setDateBirthday(cursor.getLong(cursor.getColumnIndex(Child.sDateBirthday)));
                childArrayList.add(child);
            }while (cursor.moveToNext());
        }
        cursor.close();

        listChildren.setAdapter(new ChildAdapter(childArrayList));
    }

}
