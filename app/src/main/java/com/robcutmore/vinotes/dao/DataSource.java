package com.robcutmore.vinotes.dao;


import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.Map;


/**
 * Data source is an abstract base class for adding, retrieving, and deleting data.
 */
public abstract class DataSource {

    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;
    protected Map<String, String> dbColumns;
    protected boolean closeDatabaseWhenFinished = true;


    /**
     * Connects to database.
     */
    abstract protected void connect();

    /**
     * Disconnects from database.
     */
    protected void disconnect() {
        if (this.closeDatabaseWhenFinished) {
            this.dbHelper.close();
        }
    }

    /**
     * @return array of database table column names
     */
    abstract protected String[] getDatabaseTableColumns();

    /**
     * Sets database.
     *
     * @throws SQLException
     */
    protected void setDatabase() throws SQLException {
        this.database = this.dbHelper.getWritableDatabase();
    }

}
