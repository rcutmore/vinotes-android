package com.robcutmore.vinotes.database;

import android.content.Context;

import java.util.HashMap;


public class TastingNoteDatabaseHelper extends DatabaseHelper {

    private String tableName = "notes";
    private HashMap<String, String> columns = new HashMap<>();
    private String sqlCreateTable;

    public TastingNoteDatabaseHelper(Context context) {
        super(context);

        // Store all columns in notes table.
        this.columns.put("id", "_id");
        this.columns.put("wine", "wine_id");
        this.columns.put("tasted", "tasted");
        this.columns.put("rating", "rating");

        // Store SQL command to create notes table.
        this.sqlCreateTable = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY NOT NULL, %s INTEGER NOT NULL UNIQUE, " +
            "%s INTEGER, %s INTEGER);",
            this.tableName, this.columns.get("id"), this.columns.get("wine"),
            this.columns.get("tasted"), this.columns.get("rating")
        );
    }

}