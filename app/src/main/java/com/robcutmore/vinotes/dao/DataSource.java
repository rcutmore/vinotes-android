package com.robcutmore.vinotes.dao;


import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.Map;


public abstract class DataSource {

    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;
    protected Map<String, String> dbColumns;
    protected boolean closeDatabaseWhenFinished = true;

    protected void open() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

    protected void close() {
        if (this.closeDatabaseWhenFinished) {
            this.dbHelper.close();
        }
    }

    abstract protected void connectToDatabase();

    abstract protected String[] getDatabaseTableColumns();

}
