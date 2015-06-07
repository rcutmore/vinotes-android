package com.robcutmore.vinotes;


import android.content.Context;

import java.util.HashMap;

public class NoteTraitDatabaseHelper extends DatabaseHelper {

    private String tableName = "traits";
    private HashMap<String, String> columns = new HashMap<>();
    private String sqlCreateTable;

    public NoteTraitDatabaseHelper(Context context) {
        super(context);

        // Store all columns in traits table.
        this.columns.put("id", "_id");
        this.columns.put("name", "name");

        // Store SQL command to create traits table.
        this.sqlCreateTable = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY NOT NULL, %s TEXT NOT NULL UNIQUE);",
            this.tableName, this.columns.get("id"), this.columns.get("name")
        );
    }

}