package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.request.TastingNoteRequest;
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

    public void remove(final long id) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public TastingNote get(final long id) {
        // Fetch note from local database. If missing then request from API.
        TastingNote note = this.getFromDatabase(id);
        if (note == null) {
            note = TastingNoteRequest.get(id);

            // If note was found then add to database since it was missing.
            if (note != null) {
                this.addToDatabase(note);
            }
        }
        return note;
    }

    public HashMap<Long, TastingNote> getAll() {
        // Request all notes from external API.
        TastingNote[] notesFromAPI = TastingNoteRequest.getAll();

        // Add each note to local database and HashMap to be returned.
        HashMap<Long, TastingNote> notes = new HashMap<>();
        for (int i = 0; i < notesFromAPI.length; i++) {
            TastingNote note = notesFromAPI[i];
            this.addToDatabase(note);
            notes.put(note.getId(), note);
        }

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

    private void addToDatabase(final TastingNote note) {
        // Prepare tasting note values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), note.getId());
        values.put(this.dbColumns.get("wine"), note.getWine().getId());
        values.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(note.getTasted()));
        values.put(this.dbColumns.get("rating"), note.getRating());

        // Insert tasting note into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private TastingNote getFromDatabase(final long id) {
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