package com.pikarevsoft.ekftest.ekftest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.pikarevsoft.ekftest.ekftest.Public.ERROR;
import static com.pikarevsoft.ekftest.ekftest.Public.FULL_PARENT_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;
import static com.pikarevsoft.ekftest.ekftest.Public.getDateBirthdayInMillis;

public class NewChildActivity extends AppCompatActivity {

    TextView txtFullParent, dateBirthdayTxt;
    EditText familyEdit, nameEdit, patronymicEdit;
    Calendar calendar;
    long id, oldDate;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        context = this;
        Intent intent = getIntent();

        String fullParent = intent.getStringExtra(FULL_PARENT_INTENT);
        id = intent.getLongExtra(ID_INTENT, ERROR);
        if (fullParent == null || id == ERROR) {
            Toast.makeText(this, getString(R.string.error_read_line), Toast.LENGTH_LONG).show();
            finish();
        }

        calendar = Calendar.getInstance();
        oldDate = calendar.getTimeInMillis();

        txtFullParent = findViewById(R.id.child_txt_parent);
        txtFullParent.setText(fullParent);

        familyEdit = findViewById(R.id.child_edit_family);
        nameEdit = findViewById(R.id.child_edit_name);
        patronymicEdit = findViewById(R.id.child_edit_patronymic);
        dateBirthdayTxt = findViewById(R.id.child_txt_date_birthday);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_worker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ContentValues cv = new ContentValues();
        cv.put(Child.sParent, id);
        cv.put(Child.sFamily, familyEdit.getText().toString());
        cv.put(Child.sName, nameEdit.getText().toString());
        cv.put(Child.sPatronymic, patronymicEdit.getText().toString());
        cv.put(Child.sDateBirthday, getDateBirthdayInMillis(calendar, oldDate));
        db.insert(Child.sTable, null, cv);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!familyEdit.getText().toString().equals("") || !nameEdit.getText().toString().equals("") ||
                !patronymicEdit.getText().toString().equals("") || calendar.getTimeInMillis() != oldDate)
        {
            AlertDialog.Builder dialogExit = new Child().buildDialog(this, this);
            dialogExit.show();
        } else {
            finish();
        }
    }


}
