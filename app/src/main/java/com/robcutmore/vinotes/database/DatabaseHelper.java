package com.robcutmore.vinotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;


public abstract class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "vinotes.db";
    protected static final int DATABASE_VERSION = 1;
    private String tableName = "";
    private HashMap<String, String> columns = new HashMap<>();
    private String sqlCreateTable = "";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(this.sqlCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String logMessage = String.format(
            "Upgrading database from version %d to %d.", oldVersion, newVersion);
        Log.w(DatabaseHelper.class.getName(), logMessage);

        String sql = String.format("DROP TABLE IF EXISTS %s;", this.tableName);
        database.execSQL(sql);
        this.onCreate(database);
    }

    public HashMap<String, String> getColumns() {
        return this.columns;
    }

    public String getTableName() {
        return this.tableName;
    }

}