package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.Note;
import com.robcutmore.vinotes.request.NoteRequest;
import com.robcutmore.vinotes.utils.DateUtils;
import com.robcutmore.vinotes.model.Wine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


/**
 * NoteDataSource manages adding, retrieving, and deleting note-related data.
 * Interacts with API and local database.
 */
public class NoteDataSource extends DataSource {

    private WineDataSource wineDataSource;

    /**
     * Constructor.
     */
    public NoteDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getNoteColumns();
        this.wineDataSource = new WineDataSource(context);
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  true or false for closing connection after operations
     */
    protected NoteDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    /**
     * Adds new note.
     *
     * @param wineId  id of wine for new note
     * @param tasted  tasting date for new note
     * @param rating  rating for new note
     * @return Note object
     */
    public Note add(final long wineId, final Date tasted, final Integer rating) {
        // Add new note to API.
        Note note = NoteRequest.add(wineId, tasted, rating);

        // If note was successfully added to API then add to local database as well.
        if (note != null) {
            this.addToDatabase(note);
        }

        return note;
    }

    /**
     * Deletes note with given id.
     *
     * @param id  id of note to delete
     */
    public void remove(final long id) {
        String table = this.dbHelper.getNoteTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        this.database.delete(table, whereClause, null);
        this.close();
    }

    /**
     * Fetches note with given id.
     *
     * @param id  id of note to retrieve
     * @return Note object
     */
    public Note get(final long id) {
        // Fetch note from local database. If missing then request from API.
        Note note = this.getFromDatabase(id);
        if (note == null) {
            note = NoteRequest.get(id);

            // If note was found then add to database since it was missing.
            if (note != null) {
                this.addToDatabase(note);
            }
        }
        return note;
    }

    /**
     * Fetches all notes, either from API or database.
     * Repopulates database when fetching notes from API.
     *
     * @param refreshFromAPI  true or false to refresh with data from API
     * @return an ArrayList containing Note objects
     */
    public ArrayList<Note> getAll(final boolean refreshFromAPI) {
        ArrayList<Note> notes;
        if (refreshFromAPI) {
            // Repopulate notes in database with data from API.
            this.removeAllFromDatabase();
            notes = NoteRequest.getAll();
            for (Note note : notes) {
                this.addToDatabase(note);
            }
        } else {
            // Retrieve all notes from database.
            notes = this.getAllFromDatabase();
        }
        return notes;
    }

    /**
     * Connects to database.
     */
    @Override
    protected void connectToDatabase() {
        try {
            this.open();
        } catch (SQLException e) {
            Log.w(NoteDataSource.class.getName(), "Error connecting to database.");
        }
    }

    /**
     * Fetches database column names for notes table.
     *
     * @return an array containing column names
     */
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

    /**
     * Adds given note to database.
     *
     * @param note  note to add to database
     */
    private void addToDatabase(final Note note) {
        // Prepare tasting note values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), note.getId());
        values.put(this.dbColumns.get("wine"), note.getWine().getId());
        if (note.getTasted() != null) {
            values.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(note.getTasted()));
        } else {
            values.putNull(this.dbColumns.get("tasted"));
        }
        if (note.getRating() != null) {
            values.put(this.dbColumns.get("rating"), note.getRating());
        } else {
            values.putNull(this.dbColumns.get("rating"));
        }

        // Insert tasting note into database if it doesn't exist yet.
        String table = this.dbHelper.getNoteTable();
        this.connectToDatabase();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.close();
    }

    /**
     * Fetches all notes from database.
     *
     * @return an ArrayList containing Note objects
     */
    private ArrayList<Note> getAllFromDatabase() {
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();

        // Order results by tasted date.
        String orderBy = String.format("%s DESC", this.dbColumns.get("tasted"));

        // Query notes table for all notes.
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Store and return notes.
        cursor.moveToFirst();
        ArrayList<Note> notes = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            notes.add(this.cursorToNote(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.close();
        return notes;
    }

    /**
     * Fetches note with given id from database.
     *
     * @param id  id of note to retrieve
     * @return Note object
     */
    private Note getFromDatabase(final long id) {
        // Query notes table for note with given id.
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return tasting note.
        cursor.moveToFirst();
        Note note = !cursor.isAfterLast() ? this.cursorToNote(cursor) : null;
        cursor.close();
        this.close();
        return note;
    }

    /**
     * Deletes all notes from database.
     */
    private void removeAllFromDatabase() {
        this.connectToDatabase();
        this.database.delete(this.dbHelper.getNoteTable(), null, null);
        this.close();
    }

    /**
     * Creates note using data at current position of given cursor.
     *
     * @param cursor  cursor containing note data
     * @return Note object
     */
    private Note cursorToNote(Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineId = cursor.getLong(1);
        Date tasted = !cursor.isNull(3) ? DateUtils.convertTimestampToDate(cursor.getLong(3)) : null;
        Integer rating = !cursor.isNull(4) ? cursor.getInt(4) : null;

        // Create tasting note object with information from cursor.
        final Wine wine = this.wineDataSource.get(wineId);
        return new Note(id, wine, tasted, rating);
    }

}
