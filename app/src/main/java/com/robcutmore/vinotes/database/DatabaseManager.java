package com.robcutmore.vinotes.database;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseManager {

    private int openCounter;
    private static DatabaseManager instance;
    private static SQLiteOpenHelper databaseHelper;
    private SQLiteDatabase database;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            databaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            String errorMessage = String.format(
                "%s is not initialized, call initializeInstance(...) first.",
                DatabaseManager.class.getSimpleName()
            );
            throw new IllegalStateException(errorMessage);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        openCounter++;
        if (openCounter == 1) {
            // This is the first instance so open a new database connection.
            database = databaseHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized void closeDatabase() {
        openCounter--;
        if (openCounter == 0) {
            // This is the last instance so close database connection.
            database.close();
        }
    }

}
