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

    // Traits
    private static final String TRAIT_TABLE = "traits";
    private static final Map<String, String> TRAIT_COLUMNS = createTraitMap();
    private static Map<String, String> createTraitMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("name", "name");
        return Collections.unmodifiableMap(columns);
    }
    private static final String TRAIT_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
            "%s INTEGER PRIMARY KEY NOT NULL, " +
            "%s TEXT NOT NULL UNIQUE" +
        ");",
        TRAIT_TABLE,
        TRAIT_COLUMNS.get("id"),
        TRAIT_COLUMNS.get("name")
    );

    // Wineries
    private static final String WINERY_TABLE = "wineries";
    private static final Map<String, String> WINERY_COLUMNS = createWineryMap();
    private static Map<String, String> createWineryMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("name", "name");
        return Collections.unmodifiableMap(columns);
    }
    private static final String WINERY_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s (" +
            "%s INTEGER PRIMARY KEY NOT NULL, " +
            "%s TEXT NOT NULL UNIQUE" +
        ");",
        WINERY_TABLE,
        WINERY_COLUMNS.get("id"),
        WINERY_COLUMNS.get("name")
    );

    // Wines
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
        "CREATE TABLE IF NOT EXISTS %s (" +
            "%s INTEGER PRIMARY KEY NOT NULL, " +
            "%s INTEGER NOT NULL, " +
            "%s TEXT NOT NULL, " +
            "%s INTEGER NOT NULL, " +
            "FOREIGN KEY(%s) REFERENCES %s(%s)" +
        ");",
        WINE_TABLE,
        WINE_COLUMNS.get("id"),
        WINE_COLUMNS.get("winery"),
        WINE_COLUMNS.get("name"),
        WINE_COLUMNS.get("vintage"),
        WINE_COLUMNS.get("winery"), WINERY_TABLE, WINERY_COLUMNS.get("id")
    );

    // Notes
    private static final String NOTE_TABLE = "notes";
    private static final Map<String, String> NOTE_COLUMNS = createNoteMap();
    private static Map<String, String> createNoteMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "_id");
        columns.put("wine", "wine_id");
        columns.put("tasted", "tasted");
        columns.put("rating", "rating");
        return columns;
    }
    private static final String NOTE_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(" +
            "%s INTEGER PRIMARY KEY NOT NULL, " +
            "%s INTEGER NOT NULL, " +
            "%s INTEGER, " +
            "%s INTEGER, " +
            "FOREIGN KEY(%s) REFERENCES %s(%s)" +
        ");",
        NOTE_TABLE,
        NOTE_COLUMNS.get("id"),
        NOTE_COLUMNS.get("wine"),
        NOTE_COLUMNS.get("tasted"),
        NOTE_COLUMNS.get("rating"),
        NOTE_COLUMNS.get("wine"), WINE_TABLE, WINE_COLUMNS.get("id")
    );

    // Note traits
    private static final String NOTE_TRAIT_TABLE = "note_traits";
    private static final Map<String, String> NOTE_TRAIT_COLUMNS = createNoteTraitMap();
    private static Map<String, String> createNoteTraitMap() {
        Map<String, String> columns = new HashMap<>();
        columns.put("note", "note_id");
        columns.put("trait", "trait_id");
        columns.put("type", "trait_type");
        return columns;
    }
    private static final String NOTE_TRAIT_SQL_CREATE = String.format(
        "CREATE TABLE IF NOT EXISTS %s(" +
            "%s INTEGER NOT NULL, " +
            "%s INTEGER NOT NULL, " +
            "%s TEXT NOT NULL, " +
            "PRIMARY KEY(%s, %s, %s), " +
            "FOREIGN KEY(%s) REFERENCES %s(%s), " +
            "FOREIGN KEY(%s) REFERENCES %s(%s)" +
        ");",
        NOTE_TRAIT_TABLE,
        NOTE_TRAIT_COLUMNS.get("note"),
        NOTE_TRAIT_COLUMNS.get("trait"),
        NOTE_TRAIT_COLUMNS.get("type"),
        NOTE_TRAIT_COLUMNS.get("note"), NOTE_TRAIT_COLUMNS.get("trait"), NOTE_TRAIT_COLUMNS.get("type"),
        NOTE_TRAIT_COLUMNS.get("note"), NOTE_TABLE, NOTE_COLUMNS.get("id"),
        NOTE_TRAIT_COLUMNS.get("trait"), TRAIT_TABLE, TRAIT_COLUMNS.get("id")
    );

    /**
     * Gets instance of DatabaseHelper.
     * If one already exists it is returned, otherwise a new one is created.
     *
     * @param context  application context
     * @return DatabaseHelper instance
     */
    public static synchronized DatabaseHelper getInstance(final Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Private constructor to prevent direct instantiation.
     * Make call to static method "getInstance()" to instantiate.
     */
    private DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates database tables.
     *
     * @param database  database to create tables in
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TRAIT_SQL_CREATE);
        database.execSQL(WINERY_SQL_CREATE);
        database.execSQL(WINE_SQL_CREATE);
        database.execSQL(NOTE_SQL_CREATE);
        database.execSQL(NOTE_TRAIT_SQL_CREATE);
    }

    /**
     * Drops and recreates database tables when there is an upgrade.
     *
     * @param database  database to upgrade
     * @param oldVersion  old version number
     * @param newVersion  new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String logMessage = String.format(
            "Upgrading database from version %d to %d.", oldVersion, newVersion);
        Log.w(DatabaseHelper.class.getName(), logMessage);

        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", TRAIT_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", WINERY_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", WINE_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", NOTE_TABLE));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s;", NOTE_TRAIT_TABLE));
        this.onCreate(database);
    }

    /**
     * @return HashMap of columns in traits table
     */
    public Map<String, String> getTraitColumns() {
        return TRAIT_COLUMNS;
    }

    /**
     * @return name of traits table
     */
    public String getTraitTable() {
        return TRAIT_TABLE;
    }

    /**
     * @return HashMap of columns in wineries table
     */
    public Map<String, String> getWineryColumns() {
        return WINERY_COLUMNS;
    }

    /**
     * @return name of wineries table
     */
    public String getWineryTable() {
        return WINERY_TABLE;
    }

    /**
     * @return HashMap of columns in wines table
     */
    public Map<String, String> getWineColumns() {
        return WINE_COLUMNS;
    }

    /**
     * @return name of wines table
     */
    public String getWineTable() {
        return WINE_TABLE;
    }

    /**
     * @return HashMap of columns in notes table
     */
    public Map<String, String> getNoteColumns() {
        return NOTE_COLUMNS;
    }

    /**
     * @return name of notes table
     */
    public String getNoteTable() {
        return NOTE_TABLE;
    }

    /**
     * @return HashMap of columns in note_traits table
     */
    public Map<String, String> getNoteTraitColumns() {
        return NOTE_TRAIT_COLUMNS;
    }

    /**
     * @return name of note_traits table
     */
    public String getNoteTraitTable() {
        return NOTE_TRAIT_TABLE;
    }

}
