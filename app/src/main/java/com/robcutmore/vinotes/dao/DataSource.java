package com.robcutmore.vinotes.dao;

import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.HashMap;


public abstract class DataSource {

    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;
    protected HashMap<String, String> dbColumns;

    public void open() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    public void close() {
        this.dbHelper.close();
    }

    abstract protected String[] getDatabaseTableColumns();

}
