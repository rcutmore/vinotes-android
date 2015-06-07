package com.robcutmore.vinotes;

import android.content.Context;

import java.util.HashMap;


public class WineDatabaseHelper extends DatabaseHelper {

    private String tableName = "wines";
    private HashMap<String, String> columns = new HashMap<>();
    private String sqlCreateTable;

    public WineDatabaseHelper(Context context) {
        super(context);

        // Store all columns in wines table.
        this.columns.put("id", "_id");
        this.columns.put("winery", "winery_id");
        this.columns.put("name", "name");
        this.columns.put("vintage", "vintage");

        // Store SQL command to create wines table.
        this.sqlCreateTable = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY NOT NULL, %s INTEGER NOT NULL UNIQUE, " +
            "%s TEXT NOT NULL UNIQUE, %s INTEGER NOT NULL);",
            this.tableName, this.columns.get("id"), this.columns.get("winery"),
            this.columns.get("name"), this.columns.get("vintage")
        );
    }

}
