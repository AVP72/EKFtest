package com.pikarevsoft.ekftest.ekftest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import static com.pikarevsoft.ekftest.ekftest.Public.ADAPTER_POSITION;
import static com.pikarevsoft.ekftest.ekftest.Public.DATA_BIRTHDAY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FAMILY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FULL_PARENT_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.NAME_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.N_CHILD_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PATRONYMIC_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PROF_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;
import static com.pikarevsoft.ekftest.ekftest.Public.getDateBirthdayInMillis;

public class NewWorkerActivity extends AppCompatActivity {

    Context context;
    EditText familyEdit, nameEdit, patronymicEdit, profEdit;
    TextView dateBirthdayTxt;
    ImageView btnAddChild;
    Calendar calendar;
    long oldDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        context = this;
        calendar = Calendar.getInstance();
        oldDate = calendar.getTimeInMillis();

        familyEdit = findViewById(R.id.new_worker_edit_family);
        nameEdit = findViewById(R.id.new_worker_edit_name);
        patronymicEdit = findViewById(R.id.new_worker_edit_patronymic);
        dateBirthdayTxt = findViewById(R.id.new_worker_edit_date_birthday);
        profEdit = findViewById(R.id.new_worker_edit_prof);

        dateBirthdayTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnAddChild = findViewById(R.id.worker_btn_add_child);
        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getString(R.string.save_worker));
                dialog.setCancelable(false);

                dialog.setPositiveButton(context.getString(R.string.write), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertWorkerInDBAndRunEdit();
                    }
                });

                dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ничего не делать, просто закрыть диалог
                    }
                });

                dialog.show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_worker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveWorkerInBD();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!familyEdit.getText().toString().equals("") || !nameEdit.getText().toString().equals("") ||
                !patronymicEdit.getText().toString().equals("") || !profEdit.getText().toString().equals("") ||
                calendar.getTimeInMillis() != oldDate) {
            AlertDialog.Builder dialogExit = new Worker().buildDialog(this, this);
            dialogExit.show();
        } else {
            finish();
        }
    }

    long saveWorkerInBD(){
        ContentValues cv = new ContentValues();
        cv.put(Worker.sFamily, familyEdit.getText().toString());
        cv.put(Worker.sName, nameEdit.getText().toString());
        cv.put(Worker.sPatronymic, patronymicEdit.getText().toString());
        cv.put(Worker.sDateBirthday, getDateBirthdayInMillis(calendar, oldDate));
        cv.put(Worker.sProf, profEdit.getText().toString());
        return db.insert(Worker.sTable, null, cv);
    }

    void insertWorkerInDBAndRunEdit(){

        long id = saveWorkerInBD();

        Intent intentEditWorker = new Intent(context, EditWorkerActivity.class);
        intentEditWorker.putExtra(ADAPTER_POSITION, 0);
        intentEditWorker.putExtra(ID_INTENT, id);
        intentEditWorker.putExtra(FAMILY_INTENT, familyEdit.getText().toString());
        intentEditWorker.putExtra(NAME_INTENT, nameEdit.getText().toString());
        intentEditWorker.putExtra(PATRONYMIC_INTENT, patronymicEdit.getText().toString());
        intentEditWorker.putExtra(DATA_BIRTHDAY_INTENT, getDateBirthdayInMillis(calendar, oldDate));
        intentEditWorker.putExtra(PROF_INTENT, profEdit.getText().toString());
        intentEditWorker.putExtra(N_CHILD_INTENT, 0);
        context.startActivity(intentEditWorker);

        Intent intentNewChild = new Intent(context, NewChildActivity.class);
        String fullParent =
                        familyEdit.getText().toString() +" "+
                        nameEdit.getText().toString() + " " +
                        patronymicEdit.getText().toString();
        intentNewChild.putExtra(FULL_PARENT_INTENT, fullParent);
        intentNewChild.putExtra(ID_INTENT, id);
        context.startActivity(intentNewChild);

        finish();

    }

}
