package com.pikarevsoft.ekftest.ekftest;

import android.database.sqlite.SQLiteDatabase;

public class Worker extends Man {

    static final String sProf = "prof";
    static final String sTable = "worker";

    String prof;
    private int nChild;

    Worker() {}

    void createTable(SQLiteDatabase db) {

        String s = "CREATE TABLE " + sTable + " (\n" +
                forSqlString(sId) + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n " +
                forSqlString(sFamily) + "TEXT,\n" +
                forSqlString(sName) + "TEXT,\n" +
                forSqlString(sPatronymic) + "TEXT,\n" +
                forSqlString(sDateBirthday) + "INTEGER UNSIGNED,\n" +
                forSqlString(sProf) + "TEXT \n" +
                ");";

       // toLog(s);
        db.execSQL(s);
    }


    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    int getnChild() {
        return nChild;
    }

    void setnChild(int nChild) {
        this.nChild = nChild;
    }
}
