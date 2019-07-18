package com.pikarevsoft.ekftest.ekftest;

import android.database.sqlite.SQLiteDatabase;

public class ViewWorkerJoinChildFromDB extends Man {

    public static final String sTable = "vwc";

    void craeteViewOrders(SQLiteDatabase db){
        String sWorker = "worker";
        String sChild = "child";
        String s = "CREATE VIEW " + sTable + " AS  \n"+
                "SELECT " + "\n" +
                forViewString(Worker.sTable, Worker.sId, sWorker +Worker.sId) +",\n"+
                forViewString(Worker.sTable, Worker.sFamily, sWorker +Worker.sFamily) +",\n"+
                forViewString(Worker.sTable, Worker.sName, sWorker +Worker.sName) +",\n"+
                forViewString(Worker.sTable, Worker.sPatronymic, sWorker +Worker.sPatronymic) +",\n"+
                forViewString(Worker.sTable, Worker.sDateBirthday, sWorker +Worker.sDateBirthday) +",\n"+
                forViewString(Worker.sTable, Worker.sProf, sWorker +Worker.sProf) +",\n"+
                forViewString(Child.sTable, Child.sFamily, sChild +Child.sFamily) +",\n"+
                forViewString(Child.sTable, Child.sName, sChild +Child.sName) +",\n"+
                forViewString(Child.sTable, Child.sPatronymic, sChild +Child.sPatronymic) +",\n"+
                forViewString(Child.sTable, Child.sDateBirthday, sChild +Child.sDateBirthday) +"\n"+
                "      FROM ("+forSqlString(Worker.sTable) +"\n"+
                "               LEFT JOIN "+ forSqlString(Child.sTable) +
                " ON ( " + forSqlString(Worker.sTable) + "." + forSqlString(Worker.sId) +
                " = " + forSqlString(Child.sTable) + "." + forSqlString(Child.sParent) + ")" +"\n"+
                "           );" ;
        //toLog(s);
        db.execSQL(s);
    }

    private String forViewString(String t, String p, String s){
        return " '" + t + "'" + "." + "'" + p + "' AS '" + s + "'";
    }

}
