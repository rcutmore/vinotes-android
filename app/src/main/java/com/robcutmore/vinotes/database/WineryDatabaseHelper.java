package com.robcutmore.vinotes.database;


import android.content.Context;

import java.util.HashMap;


public class WineryDatabaseHelper extends DatabaseHelper {

    public WineryDatabaseHelper(Context context) {
        super(context);

        this.tableName = "wineries";

        // Store all columns in wineries table.
        this.columns = new HashMap<>();
        this.columns.put("id", "_id");
        this.columns.put("name", "name");

        // Store SQL command to create wineries table.
        this.sqlCreateTable = String.format(
            "CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY NOT NULL, %s TEXT NOT NULL UNIQUE);",
            this.tableName, this.columns.get("id"), this.columns.get("name")
        );
    }

}