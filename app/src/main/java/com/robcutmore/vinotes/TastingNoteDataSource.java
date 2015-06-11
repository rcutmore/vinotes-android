package com.robcutmore.vinotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        Wine wine = this.wineDataSource.getWine(wineId);
        TastingNote note = new TastingNote(wine);
        note.setId(id);
        note.setTasted(tasted);
        note.setRating(rating);
        return note;
    }

    public void deleteNote(final TastingNote note) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), note.getId());
        this.database.delete(table, whereClause, null);
    }

    public TastingNote getNote(final long id) {
        // Query notes table for note with given id.
        String table = this.dbHelper.getTableName();
        String[] columns = {
                this.dbColumns.get("id"),
                this.dbColumns.get("wine"),
                this.dbColumns.get("tasted"),
                this.dbColumns.get("rating")
        };
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return tasting note.
        TastingNote note;
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            note = this.cursorToNote(cursor);
        } else {
            note = null;
        }
        return note;
    }

    public HashMap<Long, TastingNote> getAllNotes() {
        // Query notes table for all notes.
        String table = this.dbHelper.getTableName();
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("wine"),
            this.dbColumns.get("tasted"),
            this.dbColumns.get("rating")
        };
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all notes.
        HashMap<Long, TastingNote> notes = new HashMap<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            TastingNote note = this.cursorToNote(cursor);
            notes.put(note.getId(), note);
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }

    private TastingNote cursorToNote(Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineId = cursor.getLong(1);
        Date tasted = DateUtils.convertTimestampToDate(cursor.getLong(3));
        int rating = cursor.getInt(4);

        // Create tasting note object with information from cursor.
        Wine wine = this.wineDataSource.getWine(wineId);
        TastingNote note = new TastingNote(wine);
        note.setId(id);
        note.setTasted(tasted);
        note.setRating(rating);
        return note;
    }

}