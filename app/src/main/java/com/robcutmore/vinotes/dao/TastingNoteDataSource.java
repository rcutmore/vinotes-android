package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.utils.DateUtils;
import com.robcutmore.vinotes.model.TastingNote;
import com.robcutmore.vinotes.database.TastingNoteDatabaseHelper;
import com.robcutmore.vinotes.model.Wine;

import java.util.Date;
import java.util.HashMap;


public class TastingNoteDataSource extends DataSource {

    private WineDataSource wineDataSource;

    public TastingNoteDataSource(Context context) {
        this.dbHelper = new TastingNoteDatabaseHelper(context);
        this.dbColumns = this.dbHelper.getColumns();
        this.wineDataSource = new WineDataSource(context);
    }

    public TastingNote createNote(final long id, final long wineId, final Date tasted, final int rating) {
        // Prepare tasting note values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), id);
        values.put(this.dbColumns.get("wine"), wineId);
        values.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(tasted));
        values.put(this.dbColumns.get("rating"), rating);

        // Insert tasting note into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Create and return tasting note with given information.
        Wine wine = this.wineDataSource.get(wineId);
        TastingNote note = new TastingNote(wine);
        note.setId(id);
        note.setTasted(tasted);
        note.setRating(rating);
        return note;
    }

    public void deleteNote(final long id) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public TastingNote getNote(final long id) {
        // Query notes table for note with given id.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return tasting note.
        cursor.moveToFirst();
        TastingNote note = !cursor.isAfterLast() ? this.cursorToNote(cursor) : null;
        cursor.close();
        return note;
    }

    public HashMap<Long, TastingNote> getAllNotes() {
        // Query notes table for all notes.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all notes.
        HashMap<Long, TastingNote> notes = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TastingNote note = this.cursorToNote(cursor);
            notes.put(note.getId(), note);
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }

    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("wine"),
            this.dbColumns.get("tasted"),
            this.dbColumns.get("rating")
        };
        return columns;
    }

    private TastingNote cursorToNote(Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineId = cursor.getLong(1);
        Date tasted = !cursor.isNull(3) ? DateUtils.convertTimestampToDate(cursor.getLong(3)) : null;
        Integer rating = !cursor.isNull(4) ? cursor.getInt(4) : null;

        // Create tasting note object with information from cursor.
        final Wine wine = this.wineDataSource.get(wineId);
        return new TastingNote(id, wine, tasted, rating);
    }

}