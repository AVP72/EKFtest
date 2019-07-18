package com.pikarevsoft.ekftest.ekftest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

public class Man {

    public static final String sId = "_id";                     // id
    public static final String sFamily = "family";
    public static final String sName = "name";
    public static final String sPatronymic = "patronymic";
    public static final String sDateBirthday = "birthday";

    long id;
    private String family, name, patronymic;
    private long dateBirthday;

    Man(String family, String name, String patronymic, long dateBirthday) {
        this.family = family;
        this.name = name;
        this.patronymic = patronymic;
        this.dateBirthday = dateBirthday;
    }

    Man() {}

    String forSqlString(String s) {
        return " '" + s + "' ";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    long getDateBirthday() {
        return dateBirthday;
    }

    void setDateBirthday(long dateBirthday) {
        this.dateBirthday = dateBirthday;
    }

    AlertDialog.Builder buildDialog(final Context context, final AppCompatActivity activity){
        AlertDialog.Builder dialogExit = new AlertDialog.Builder(context);
        dialogExit.setTitle(context.getString(R.string.exit_without_save));
        dialogExit.setCancelable(false);

        dialogExit.setPositiveButton(context.getString(R.string.yes_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });

        dialogExit.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ничего не делать, просто закрыть диалог
            }
        });

        return dialogExit;
    }
}
