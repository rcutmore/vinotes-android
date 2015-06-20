package com.robcutmore.vinotes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.model.NoteTrait;
import com.robcutmore.vinotes.database.NoteTraitDatabaseHelper;

import java.util.HashMap;


public class NoteTraitDataSource extends DataSource {

    public NoteTraitDataSource(Context context) {
        this.dbHelper = new NoteTraitDatabaseHelper(context);
        this.dbColumns = this.dbHelper.getColumns();
    }

    public NoteTrait createTrait(final long id, final String name) {
        // Prepare trait values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), id);
        values.put(this.dbColumns.get("name"), name);

        // Insert trait into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Create and return trait with given information.
        NoteTrait trait = new NoteTrait(name);
        trait.setId(id);
        return trait;
    }

    public void deleteTrait(final long id) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public NoteTrait getTrait(final long id) {
        // Query traits table for trait with given id.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return note trait.
        cursor.moveToFirst();
        NoteTrait trait = !cursor.isAfterLast() ? this.cursorToTrait(cursor) : null;
        cursor.close();
        return trait;
    }

    public HashMap<Long, NoteTrait> getAllTraits() {
        // Query traits table for all traits.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all traits.
        HashMap<Long, NoteTrait> traits = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NoteTrait trait = this.cursorToTrait(cursor);
            traits.put(trait.getId(), trait);
            cursor.moveToNext();
        }
        cursor.close();
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

    private NoteTrait cursorToTrait(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Crete note trait object with information from cursor.
        NoteTrait trait = new NoteTrait(name);
        trait.setId(id);
        return trait;
    }

}