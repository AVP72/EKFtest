package com.pikarevsoft.ekftest.ekftest;

import android.database.sqlite.SQLiteDatabase;

public class Child extends Man {

    static final String sParent = "parent";
    static final String sTable = "child";

    long parent;

    Child(){}

    void createTable(SQLiteDatabase db) {

        String s = "CREATE TABLE " + sTable + " ( \n" +
                forSqlString(sId) + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n " +
                forSqlString(sParent) + "INTEGER UNSIGNED,\n" +
                forSqlString(sFamily) + "TEXT, \n" +
                forSqlString(sName) + "TEXT, \n" +
                forSqlString(sPatronymic) + "TEXT, \n" +
                forSqlString(sDateBirthday) + "INTEGER UNSIGNED \n" +
                ");";

        db.execSQL(s);
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }
}
