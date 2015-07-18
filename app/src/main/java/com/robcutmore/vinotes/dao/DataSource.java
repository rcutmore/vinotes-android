package com.robcutmore.vinotes.dao;


import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.Map;


public abstract class DataSource {

    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;
    protected Map<String, String> dbColumns;

    public void open() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    public void close() {
        this.dbHelper.close();
    }

    abstract protected String[] getDatabaseTableColumns();

}
