package com.pikarevsoft.ekftest.ekftest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import static com.pikarevsoft.ekftest.ekftest.Public.ADAPTER_POSITION;
import static com.pikarevsoft.ekftest.ekftest.Public.DATA_BIRTHDAY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ERROR;
import static com.pikarevsoft.ekftest.ekftest.Public.FAMILY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.NAME_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PATRONYMIC_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class EditChildActivity extends AppCompatActivity {

    TextView txtFullParent, dateBirthdayTxt;
    EditText familyEdit, nameEdit, patronymicEdit;
    Calendar calendar;
    long id, idParent;
    Context context;
    Child child = new Child();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        context = this;
        Intent intent = getIntent();

        id = intent.getLongExtra(ADAPTER_POSITION, ERROR);
        idParent = intent.getLongExtra(ID_INTENT, ERROR);

        child.setFamily(intent.getStringExtra(FAMILY_INTENT));
        child.setName(intent.getStringExtra(NAME_INTENT));
        child.setPatronymic(intent.getStringExtra(PATRONYMIC_INTENT));
        child.setDateBirthday(intent.getLongExtra(DATA_BIRTHDAY_INTENT, ERROR));

        if (idParent == ERROR || child.getDateBirthday() == ERROR
                || child.getFamily() == null || child.getName() == null || child.getPatronymic() == null){
            Toast.makeText(this, getString(R.string.error_read_line), Toast.LENGTH_LONG).show();
            finish();
        }

        String fullParent = getFullNameParent(idParent);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(intent.getLongExtra(DATA_BIRTHDAY_INTENT, ERROR));

        txtFullParent = findViewById(R.id.child_txt_parent);
        txtFullParent.setText(fullParent);

        familyEdit = findViewById(R.id.child_edit_family);
        nameEdit = findViewById(R.id.child_edit_name);
        patronymicEdit = findViewById(R.id.child_edit_patronymic);
        dateBirthdayTxt = findViewById(R.id.child_txt_date_birthday);

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

        familyEdit.setText(child.getFamily());
        nameEdit.setText(child.getName());
        patronymicEdit.setText(child.getPatronymic());
        dateBirthdayTxt.setText(Public.convertDateToTxtDDMMYYYY(child.getDateBirthday()));

    }

    private String getFullNameParent(long idParent) {
        String s = "";
        Cursor cursor = db.query(Worker.sTable, null, Worker.sId+"=?", new String[]{Long.toString(idParent)}, null, null, null);
        if (cursor.moveToFirst()){
            s = cursor.getString(cursor.getColumnIndex(Worker.sFamily)) + " " +
                    cursor.getString(cursor.getColumnIndex(Worker.sName)) + " " +
                    cursor.getString(cursor.getColumnIndex(Worker.sPatronymic));
        }
        cursor.close();
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_worker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ContentValues cv = new ContentValues();
        cv.put(Child.sFamily, familyEdit.getText().toString());
        cv.put(Child.sName, nameEdit.getText().toString());
        cv.put(Child.sPatronymic, patronymicEdit.getText().toString());
        cv.put(Child.sDateBirthday, calendar.getTimeInMillis());
        db.update(Child.sTable, cv, Child.sId+"=?", new String[]{Long.toString(id)});
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!familyEdit.getText().toString().equals(child.getFamily()) ||
            !nameEdit.getText().toString().equals(child.getName()) ||
            !patronymicEdit.getText().toString().equals(child.getPatronymic()) ||
            calendar.getTimeInMillis() != child.getDateBirthday()
        ) {
            AlertDialog.Builder dialogExit = new Worker().buildDialog(this, this);
            dialogExit.show();
        } else {
            finish();
        }
    }

}
