package com.pikarevsoft.ekftest.ekftest;

import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Public {

    public static SQLiteDatabase db;

    static String ADAPTER_POSITION = "ADAPTER_POSITION";
    static String ID_INTENT = "ID_INTENT";
    static String FAMILY_INTENT = "FAMILY_INTENT";
    static String NAME_INTENT = "NAME_INTENT";
    static String PATRONYMIC_INTENT = "PATRONYMIC_INTENT";
    static String DATA_BIRTHDAY_INTENT = "DATA_BIRTHDAY_INTENT";
    static String PROF_INTENT = "PROF_INTENT";
    static String N_CHILD_INTENT = "N_CHILD_INTENT";
    static String BROADCAST_UPDATE_LIST_WORKER = "com.pikarevsoft.ekftest.ekftest.updatelistworker";
    static String BROADCAST_UPDATE_LIST_CHILDREN = "com.pikarevsoft.ekftest.ekftest.updatelistchildren";

    static String FULL_PARENT_INTENT = "FULL_PARENT_INTENT";

    static final int ERROR = -1;

    static String convertDateToTxtDDMMYYYY(long date){
        if (date == Long.MAX_VALUE) return "Не указано";
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(date);
        final SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dataFormat.format(d.getTime());
    }

    static long getDateBirthdayInMillis(Calendar calendar, long oldDate){
        // Если значения совпадают, то дата не вводилась, значит ее нужно заменить минимум, т.к. 0L может оказаться датой рождения
        long dataForWrite;
        if (calendar.getTimeInMillis() == oldDate) dataForWrite = Long.MAX_VALUE;
        else dataForWrite = calendar.getTimeInMillis();
        return dataForWrite;
    }

}
