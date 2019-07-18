package com.pikarevsoft.ekftest.ekftest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.pikarevsoft.ekftest.ekftest.Public.ADAPTER_POSITION;
import static com.pikarevsoft.ekftest.ekftest.Public.BROADCAST_UPDATE_LIST_CHILDREN;
import static com.pikarevsoft.ekftest.ekftest.Public.DATA_BIRTHDAY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ERROR;
import static com.pikarevsoft.ekftest.ekftest.Public.FAMILY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FULL_PARENT_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.NAME_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.N_CHILD_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PATRONYMIC_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PROF_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class EditWorkerActivity extends AppCompatActivity {

    EditText familyEdit, nameEdit, patronymicEdit, profEdit;
    TextView dateBirthdayTxt, txtNChildren;
    RecyclerView listChildren;
    Calendar calendar;
    Context context;
    long id;
    Worker worker = new Worker();
    BroadcastReceiver broadcastReceiverUpdateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        Intent intent = getIntent();

        int k = intent.getIntExtra(ADAPTER_POSITION, ERROR);
        id = intent.getLongExtra(ID_INTENT, ERROR);

        worker.setFamily(intent.getStringExtra(FAMILY_INTENT));
        worker.setName(intent.getStringExtra(NAME_INTENT));
        worker.setPatronymic(intent.getStringExtra(PATRONYMIC_INTENT));
        worker.setDateBirthday(intent.getLongExtra(DATA_BIRTHDAY_INTENT, ERROR));
        worker.setProf(intent.getStringExtra(PROF_INTENT));
        worker.setnChild(intent.getIntExtra(N_CHILD_INTENT, ERROR));

        if (k == ERROR || id == ERROR || worker.getDateBirthday() == ERROR || worker.getnChild() == ERROR
                || worker.getFamily() == null || worker.getName() == null || worker.getPatronymic() == null || worker.getProf() == null){
            Toast.makeText(this, getString(R.string.error_read_line), Toast.LENGTH_LONG).show();
            finish();
        }

        context = this;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(intent.getLongExtra(DATA_BIRTHDAY_INTENT, ERROR));

        familyEdit = findViewById(R.id.new_worker_edit_family);
        nameEdit = findViewById(R.id.new_worker_edit_name);
        patronymicEdit = findViewById(R.id.new_worker_edit_patronymic);
        dateBirthdayTxt = findViewById(R.id.new_worker_edit_date_birthday);
        profEdit = findViewById(R.id.new_worker_edit_prof);
        txtNChildren = findViewById(R.id.worker_nchild);

        dateBirthdayTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendar.getTimeInMillis() == Long.MAX_VALUE) calendar = Calendar.getInstance();
                DatePickerDialog dial = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(year, month, dayOfMonth);
                                dateBirthdayTxt.setText(Public.convertDateToTxtDDMMYYYY(calendar.getTimeInMillis()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)
                );
                dial.show();
            }
        });

        familyEdit.setText(worker.getFamily());
        nameEdit.setText(worker.getName());
        patronymicEdit.setText(worker.getPatronymic());
        dateBirthdayTxt.setText(Public.convertDateToTxtDDMMYYYY(worker.getDateBirthday()));
        profEdit.setText(worker.getProf());

        ImageView btnAddChild = findViewById(R.id.worker_btn_add_child);
        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewChildActivity.class);
                String fullParent =
                        familyEdit.getText().toString() +" "+
                        nameEdit.getText().toString() + " " +
                        patronymicEdit.getText().toString();
                intent.putExtra(FULL_PARENT_INTENT, fullParent);
                intent.putExtra(ID_INTENT, id);
                startActivity(intent);
            }
        });

        listChildren = findViewById(R.id.worker_list_of_children);
        listChildren.setHasFixedSize(true);
        listChildren.setLayoutManager(new LinearLayoutManager(this));

        broadcastReceiverUpdateList = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateList();
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_worker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ContentValues cv = new ContentValues();
        cv.put(Worker.sFamily, familyEdit.getText().toString());
        cv.put(Worker.sName, nameEdit.getText().toString());
        cv.put(Worker.sPatronymic, patronymicEdit.getText().toString());
        cv.put(Worker.sDateBirthday, calendar.getTimeInMillis());
        cv.put(Worker.sProf, profEdit.getText().toString());
        db.update(Worker.sTable, cv, Worker.sId+"=?", new String[]{Long.toString(id)});
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!familyEdit.getText().toString().equals(worker.getFamily()) ||
                !nameEdit.getText().toString().equals(worker.getName()) ||
                !patronymicEdit.getText().toString().equals(worker.getPatronymic()) ||
                !profEdit.getText().toString().equals(worker.getProf()) ||
                calendar.getTimeInMillis() != worker.getDateBirthday())
        {
            AlertDialog.Builder dialogExit = new Worker().buildDialog(this, this);
            dialogExit.show();
        } else {
            finish();
        }
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

        String s = getString(R.string.n_children);
        int nc = childArrayList.size();
        if (nc > 0) s += ": " + nc;
            else s = "Нет детей";
        txtNChildren.setText(s);
        listChildren.setAdapter(new ChildAdapter(childArrayList));
    }

}
