package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.NoteTrait;
import com.robcutmore.vinotes.request.NoteTraitRequest;

import java.util.ArrayList;


public class NoteTraitDataSource extends DataSource {

    public NoteTraitDataSource(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getTraitColumns();
    }

    public NoteTrait add(final String name) {
        // Add new note trait to API.
        NoteTrait trait = NoteTraitRequest.add(name);

        // If note trait was successfully added to API then add to local database as well.
        if (trait != null) {
            this.addToDatabase(trait);
        }

        return trait;
    }

    public void remove(final long id) {
        String table = this.dbHelper.getTraitTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public NoteTrait get(final long id) {
        // Fetch winery from local database. If missing then request from API.
        NoteTrait trait = this.getFromDatabase(id);
        if (trait == null) {
            trait = NoteTraitRequest.get(id);

            // If trait is found then add to database since it was missing.
            if (trait != null) {
                this.addToDatabase(trait);
            }
        }
        return trait;
    }

    public ArrayList<NoteTrait> getAll(final boolean refreshFromAPI) {
        ArrayList<NoteTrait> traits;
        if (refreshFromAPI) {
            traits = NoteTraitRequest.getAll();

            for (NoteTrait trait : traits) {
                this.addToDatabase(trait);
            }
        } else {
            traits = this.getAllFromDatabase();
        }

        return traits;
    }

    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("name")
        };
        return columns;
    }

    private void addToDatabase(final NoteTrait trait) {
        // Prepare trait values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), trait.getId());
        values.put(this.dbColumns.get("name"), trait.getName());

        // Insert trait into database if it doesn't exist yet.
        String table = this.dbHelper.getTraitTable();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private ArrayList<NoteTrait> getAllFromDatabase() {
        // Query traits table for all traits.
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return traits.
        cursor.moveToFirst();
        ArrayList<NoteTrait> traits = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            traits.add(this.cursorToTrait(cursor));
            cursor.moveToNext();
        }
        return traits;
    }

    private NoteTrait getFromDatabase(final long id) {
        // Query traits table for trait with given id.
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return note trait.
        cursor.moveToFirst();
        NoteTrait trait = !cursor.isAfterLast() ? this.cursorToTrait(cursor) : null;
        cursor.close();
        return trait;
    }

    private NoteTrait cursorToTrait(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create note trait object with information from cursor.
        return new NoteTrait(id, name);
    }

}
