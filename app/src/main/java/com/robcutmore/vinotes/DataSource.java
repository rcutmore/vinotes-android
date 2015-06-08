package com.robcutmore.vinotes;

import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.HashMap;


abstract class DataSource {

    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;
    protected HashMap<String, String> dbColumns;

    public void open() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    public void close() {
        this.dbHelper.close();
    }

}
