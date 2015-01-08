package com.tenforwardconsulting.cordova.bgloc.data.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String LOCATION_TABLE_NAME = "locations";
    private static final String LOCATION_TABLE_COLUMNS = 
        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
        " groupid INTEGER," +
        " recordedAt TEXT," +
        " accuracy TEXT," +
        " speed TEXT," +
        " bearing TEXT," +
        " altitude TEXT," +
        " latitude TEXT," +
        " longitude TEXT";
    private static final String LOCATION_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + LOCATION_TABLE_NAME + " (" +
        LOCATION_TABLE_COLUMNS +
        ");";

    LocationOpenHelper(Context context, String dbname) {
        super(context, dbname, null, DATABASE_VERSION);
    }    

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATION_TABLE_CREATE);
        Log.d(this.getClass().getName(), LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
}