package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.Note;
import com.robcutmore.vinotes.model.Trait;
import com.robcutmore.vinotes.request.NoteRequest;
import com.robcutmore.vinotes.utils.DateUtils;
import com.robcutmore.vinotes.model.Wine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


/**
 * NoteDataSource manages adding, retrieving, and deleting note-related data.
 * Interacts with API and local database.
 */
public class NoteDataSource extends DataSource {

    private Map<String, String> dbTraitColumns;
    private WineDataSource wineDataSource;
    private TraitDataSource traitDataSource;

    /**
     * Constructor.
     */
    public NoteDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getNoteColumns();
        this.dbTraitColumns = this.dbHelper.getNoteTraitColumns();
        this.wineDataSource = new WineDataSource(context, false);
        this.traitDataSource = new TraitDataSource(context, false);
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
     * @param wine  wine for new note
     * @param tasted  tasting date for new note
     * @param rating  rating for new note
     * @param colorTraits  list of color traits
     * @param noseTraits  list of nose traits
     * @param tasteTraits  list of taste traits
     * @param finishTraits  list of finish traits
     * @return Note object
     */
    public Note add(final Wine wine, final Date tasted, final Integer rating,
                    final ArrayList<Trait> colorTraits, final ArrayList<Trait> noseTraits,
                    final ArrayList<Trait> tasteTraits, final ArrayList<Trait> finishTraits) {
        // Add new note to API.
        Note note = NoteRequest.add(
            wine, tasted, rating, colorTraits, noseTraits, tasteTraits, finishTraits);

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
        this.connect();
        this.database.delete(table, whereClause, null);
        this.disconnect();
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
    protected void connect() {
        try {
            this.setDatabase();
        } catch (SQLException e) {
            Log.w(NoteDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * Fetches database column names for notes table.
     *
     * @return array containing column names
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
     * Fetches database column names for note_traits table.
     *
     * @return array containing column names
     */
    private String[] getTraitDatabaseTableColumns() {
        String[] columns = {
            this.dbTraitColumns.get("note"),
            this.dbTraitColumns.get("trait"),
            this.dbTraitColumns.get("type"),
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

        // Add tasting note.
        long noteId = note.getId();
        String noteTable = this.dbHelper.getNoteTable();
        ContentValues noteValues = new ContentValues();
        noteValues.put(this.dbColumns.get("id"), noteId);
        noteValues.put(this.dbColumns.get("wine"), note.getWine().getId());
        noteValues.put(this.dbColumns.get("tasted"), DateUtils.convertDateToTimestamp(note.getTasted()));
        noteValues.put(this.dbColumns.get("rating"), note.getRating());
        this.database.insertWithOnConflict(noteTable, null, noteValues, SQLiteDatabase.CONFLICT_IGNORE);

        // Add note color traits.
        String noteTraitTable = this.dbHelper.getNoteTraitTable();
        ContentValues traitValues = new ContentValues();
        traitValues.put(this.dbTraitColumns.get("note"), noteId);
        traitValues.put(this.dbTraitColumns.get("type"), "color");
        for (Trait trait : note.getColorTraits()) {
            traitValues.put(this.dbTraitColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note nose traits.
        traitValues.put(this.dbTraitColumns.get("type"), "nose");
        for (Trait trait : note.getNoseTraits()) {
            traitValues.put(this.dbTraitColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note taste traits.
        traitValues.put(this.dbTraitColumns.get("type"), "taste");
        for (Trait trait : note.getTasteTraits()) {
            traitValues.put(this.dbTraitColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note finish traits.
        traitValues.put(this.dbTraitColumns.get("type"), "finish");
        for (Trait trait : note.getFinishTraits()) {
            traitValues.put(this.dbTraitColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        this.database.endTransaction();
        this.disconnect();
    }

    /**
     * Fetches all notes from database.
     *
     * @return an ArrayList containing Note objects
     */
    private ArrayList<Note> getAllFromDatabase() {
        this.connect();

        // Fetch all notes from database.
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();
        String orderBy = String.format("%s DESC", this.dbColumns.get("tasted"));
        Cursor notesCursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Process all results.
        notesCursor.moveToFirst();
        ArrayList<Note> notes = new ArrayList<>();
        while (!notesCursor.isAfterLast()) {
            Note note = this.cursorToNote(notesCursor);

            // Fetch all IDs of note traits.
            table = this.dbHelper.getNoteTraitTable();
            columns = this.getTraitDatabaseTableColumns();
            String where = String.format("%s = %d", this.dbTraitColumns.get("note"), note.getId());
            Cursor traitsCursor = this.database.query(table, columns, where, null, null, null, null);

            while (!traitsCursor.isAfterLast()) {
                // Set trait.
                String traitType = traitsCursor.getString(2);
                Trait trait = this.cursorToTrait(traitsCursor);
                if (trait != null) {
                    switch (traitType) {
                        case "color":
                            note.addColorTrait(trait);
                            break;
                        case "nose":
                            note.addNoseTrait(trait);
                            break;
                        case "taste":
                            note.addTasteTrait(trait);
                            break;
                        case "finish":
                            note.addFinishTrait(trait);
                            break;
                        default:
                            // Invalid trait type so do not add.
                    }
                }
                traitsCursor.moveToNext();
            }

            notes.add(note);
            traitsCursor.close();
            notesCursor.moveToNext();
        }

        notesCursor.close();
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
        // Query notes table for note with given id.
        String table = this.dbHelper.getNoteTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connect();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return tasting note.
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
        this.database.delete(this.dbHelper.getNoteTable(), null, null);
        this.database.delete(this.dbHelper.getNoteTraitTable(), null, null);
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

        // Create tasting note object with information from cursor.
        Wine wine = this.wineDataSource.get(wineId);
        return new Note(id, wine, tasted, rating);
    }

    private Trait cursorToTrait(final Cursor cursor) {
        long id = cursor.getLong(1);
        return this.traitDataSource.get(id);
    }

}
