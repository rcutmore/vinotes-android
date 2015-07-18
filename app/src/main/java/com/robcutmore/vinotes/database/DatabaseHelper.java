package com.robcutmore.vinotes.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "vinotes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TRAIT_TABLE = "traits";
    private static final Map<String, String> TRAIT_COLUMNS = createTraitMap();
    private static Map<String, String> createTraitMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("name", "name");
        return Collections.unmodifiableMap(columns);
    }
    private static final String TRAIT_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY NOT NULL, %s TEXT NOT NULL UNIQUE);",
        TRAIT_TABLE, TRAIT_COLUMNS.get("id"), TRAIT_COLUMNS.get("name")
    );

    private static final String WINERY_TABLE = "wineries";
    private static final Map<String, String> WINERY_COLUMNS = createWineryMap();
    private static Map<String, String> createWineryMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("name", "name");
        return Collections.unmodifiableMap(columns);
    }
    private static final String WINERY_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY NOT NULL, %s TEXT NOT NULL UNIQUE);",
        WINERY_TABLE, WINERY_COLUMNS.get("id"), WINERY_COLUMNS.get("name")
    );

    private static final String WINE_TABLE = "wines";
    private static final Map<String, String> WINE_COLUMNS = createWineMap();
    private static Map<String, String> createWineMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("winery", "winery_id");
        columns.put("name", "name");
        columns.put("vintage", "vintage");
        return Collections.unmodifiableMap(columns);
    }
    private static final String WINE_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY NOT NULL, " +
        "%s INTEGER NOT NULL UNIQUE, %s TEXT NOT NULL UNIQUE, %s INTEGER NOT NULL);",
        WINE_TABLE, WINE_COLUMNS.get("id"), WINE_COLUMNS.get("winery"),
        WINE_COLUMNS.get("name"), WINE_COLUMNS.get("vintage")
    );

    private static final String NOTE_TABLE = "notes";
    private static final Map<String, String> NOTE_COLUMNS = createNoteMap();
    private static Map<String, String> createNoteMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("wine", "wine_id");
        columns.put("tasted", "tasted");
        columns.put("rating", "rating");
    }
    private static final String NOTE_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY NOT NULL, " +
        "%s INTEGER NOT NULL UNIQUE, %s INTEGER, %s INTEGER);",
        NOTE_TABLE, NOTE_COLUMNS.get("id"), NOTE_COLUMNS.get("wine"),
        NOTE_COLUMNS.get("tasted"), NOTE_COLUMNS.get("rating")
    );

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TRAIT_SQL_CREATE);
        database.execSQL(WINERY_SQL_CREATE);
        database.execSQL(WINE_SQL_CREATE);
        database.execSQL(NOTE_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String logMessage = String.format(
            "Upgrading database from version %d to %d.", oldVersion, newVersion);
        Log.w(DatabaseHelper.class.getName(), logMessage);

        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", TRAIT_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", WINERY_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", WINE_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", NOTE_TABLE));
        this.onCreate(database);
    }

    public Map<String, String> getTraitColumns() {
        return TRAIT_COLUMNS;
    }

    public String getTraitTable() {
        return TRAIT_TABLE;
    }

    public Map<String, String> getWineryColumns() {
        return WINERY_COLUMNS;
    }

    public String getWineryTable() {
        return WINERY_TABLE;
    }

    public Map<String, String> getWineColumns() {
        return WINE_COLUMNS;
    }

    public String getWineTable() {
        return WINE_TABLE;
    }

    public Map<String, String> getNoteColumns() {
        return NOTE_COLUMNS;
    }

    public String getNoteTable() {
        return NOTE_TABLE;
    }

}