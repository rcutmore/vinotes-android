package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.models.Note;
import com.robcutmore.vinotes.request.NoteRequest;
import com.robcutmore.vinotes.utils.DateUtils;
import com.robcutmore.vinotes.models.Wine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Manages adding, retrieving, and deleting note-related data.
 * Interacts with API and local database.
 */
public class NoteDataSource extends DataSource {

    private WineDataSource wineDataSource;
    private NoteTraitDataSource noteTraitDataSource;

    /**
     * Constructor.
     */
    public NoteDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getNoteColumns();
        this.wineDataSource = new WineDataSource(context, false);
        this.noteTraitDataSource = new NoteTraitDataSource(context, false);
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

    // Public methods

    /**
     * Adds new note.
     *
     * @param newNote  new note to add
     * @return Note object
     */
    public Note add(final Note newNote) {
        // Add new note to API and, if successful, add to database as well.
        Note note = NoteRequest.add(newNote);
        if (note != null) {
            this.addToDatabase(note);
        }
        return note;
    }

    /**
     * Fetches note with given id.
     *
     * @param id  id of note to retrieve
     * @return Note object
     */
    public Note get(final long id) {
        // Fetch note from local database.
        // If missing then request from API and, if found, add to database.
        Note note = this.getFromDatabase(id);
        if (note == null) {
            note = NoteRequest.get(id);
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
     * @return ArrayList containing Note objects
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
     * Deletes given note.
     *
     * @param note  note to delete
     */
    public void remove(final Note note) {
        this.connect();
        this.noteTraitDataSource.removeTraitsFromDatabase(note);
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), note.getId());
        this.database.delete(this.dbHelper.getNoteTable(), whereClause, null);
        this.disconnect();
    }

    /**
     * Updates given note.
     *
     * @param note  note with updated attributes
     * @return updated note from API
     */
    public Note update(final Note note) {
        // Update note via API and, if successful, update database as well.
        Note updatedNote = NoteRequest.update(note);
        if (updatedNote != null) {
            this.updateDatabase(updatedNote);
        }
        return note;
    }

    // Protected / private database methods

    /**
     * Connects to database.
     */
    @Override
    protected void connect() {
        try {
            this.setDatabase();
        } catch (SQLException e) {
            Log.w(NoteDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * @return Array containing column names for notes database table
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
        this.connect();
        this.database.beginTransaction();

        // Add note.
        String table = this.dbHelper.getNoteTable();
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), note.getId());
        values.put(this.dbColumns.get("wine"), note.getWine().getId());
        values.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(note.getTasted()));
        values.put(this.dbColumns.get("rating"), note.getRating());
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Add note traits.
        this.noteTraitDataSource.addTraitsToDatabase(note);

        this.database.endTransaction();
        this.disconnect();
    }

    /**
     * Fetches all notes from database.
     *
     * @return ArrayList containing Note objects
     */
    private ArrayList<Note> getAllFromDatabase() {
        this.connect();

        // Fetch all notes from database.
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();
        String orderBy = String.format("%s DESC", this.dbColumns.get("tasted"));
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Process all results and return notes.
        ArrayList<Note> notes = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = this.cursorToNote(cursor);
            note = this.noteTraitDataSource.getTraitsFromDatabase(note);
            notes.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        this.disconnect();
        return notes;
    }

    /**
     * Fetches note with given id from database.
     *
     * @param id  id of note to retrieve
     * @return Note object
     */
    private Note getFromDatabase(final long id) {
        this.connect();

        // Fetch note with given id from database.
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();
        String where = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, where, null, null, null, null);

        // Return note.
        cursor.moveToFirst();
        Note note = !cursor.isAfterLast() ? this.cursorToNote(cursor) : null;
        cursor.close();
        this.disconnect();
        return note;
    }

    /**
     * Deletes all notes from database.
     */
    private void removeAllFromDatabase() {
        this.connect();
        this.database.beginTransaction();
        this.database.delete(this.dbHelper.getNoteTraitTable(), null, null);
        this.database.delete(this.dbHelper.getNoteTable(), null, null);
        this.database.endTransaction();
        this.disconnect();
    }

    /**
     * Updates note information in database.
     *
     * @param note  note to update in database
     */
    private void updateDatabase(final Note note) {
        this.connect();
        this.database.beginTransaction();

        // Update note information.
        ContentValues newValues = new ContentValues();
        newValues.put(this.dbColumns.get("wine"), note.getWine().getId());
        newValues.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(note.getTasted()));
        newValues.put(this.dbColumns.get("rating"), note.getRating());
        String where = String.format("%s = %d", this.dbColumns.get("id"), note.getId());
        this.database.update(this.dbHelper.getNoteTable(), newValues, where, null);

        // Refresh note traits in case any were updated.
        this.noteTraitDataSource.removeTraitsFromDatabase(note);
        this.noteTraitDataSource.addTraitsToDatabase(note);

        this.database.endTransaction();
        this.disconnect();
    }

    /**
     * Creates note using data at current position of given cursor.
     *
     * @param cursor  cursor containing note data
     * @return Note object
     */
    private Note cursorToNote(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineId = cursor.getLong(1);
        Date tasted = DateUtils.convertTimestampToDate(cursor.getLong(2));
        int rating = cursor.getInt(3);

        // Create note object with information from cursor.
        Wine wine = this.wineDataSource.get(wineId);
        return new Note(id, wine, tasted, rating);
    }

}
