package com.robcutmore.vinotes;

import android.content.Context;

import java.util.HashMap;


public class WineryDatabaseHelper extends DatabaseHelper {

    private String tableName = "wineries";
    private HashMap<String, String> columns = new HashMap<>();
    private String sqlCreateTable;

    public WineryDatabaseHelper(Context context) {
        super(context);

        // Store all columns in wineries table.
        this.columns.put("id", "_id");
        this.columns.put("name", "name");

        // Store SQL command to create wineries table.
        this.sqlCreateTable = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL);",
            this.tableName, this.columns.get("id"), this.columns.get("name")
        );
    }

}