package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.Note;
import com.robcutmore.vinotes.model.Trait;

import java.sql.SQLException;


/**
 * Manages adding, retrieving, and deleting trait-related data for notes.
 * Interacts with API and local database.
 */
public class NoteTraitDataSource extends DataSource {

    private TraitDataSource traitDataSource;

    /**
     * Constructor.
     */
    public NoteTraitDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getNoteTraitColumns();
        this.traitDataSource = new TraitDataSource(context, false);
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  true or false for closing connection after operations
     */
    public NoteTraitDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
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
            Log.w(NoteTraitDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * Fetches database column names for note_traits table.
     *
     * @return array containing column names
     */
    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("note"),
            this.dbColumns.get("trait"),
            this.dbColumns.get("type"),
        };
        return columns;
    }

    /**
     * Adds traits for given note to database.
     *
     * @param note  note to add traits for
     */
    protected void addTraitsToDatabase(final Note note) {
        this.connect();

        // Add note color traits.
        String noteTraitTable = this.dbHelper.getNoteTraitTable();
        ContentValues traitValues = new ContentValues();
        traitValues.put(this.dbColumns.get("note"), note.getId());
        traitValues.put(this.dbColumns.get("type"), "color");
        for (Trait trait : note.getColorTraits()) {
            traitValues.put(this.dbColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                    noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note nose traits.
        traitValues.put(this.dbColumns.get("type"), "nose");
        for (Trait trait : note.getNoseTraits()) {
            traitValues.put(this.dbColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                    noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note taste traits.
        traitValues.put(this.dbColumns.get("type"), "taste");
        for (Trait trait : note.getTasteTraits()) {
            traitValues.put(this.dbColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                    noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        // Add note finish traits.
        traitValues.put(this.dbColumns.get("type"), "finish");
        for (Trait trait : note.getFinishTraits()) {
            traitValues.put(this.dbColumns.get("trait"), trait.getId());
            this.database.insertWithOnConflict(
                    noteTraitTable, null, traitValues, SQLiteDatabase.CONFLICT_IGNORE);
        }

        this.disconnect();
    }

    /**
     * Fetches traits for given note from database.
     *
     * @param note  note to fetch traits for
     * @return Note object with traits set
     */
    protected Note getTraitsFromDatabase(final Note note) {
        this.connect();

        // Fetch all IDs of note traits.
        String table = this.dbHelper.getNoteTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        String where = String.format("%s = %d", this.dbColumns.get("note"), note.getId());
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
        traitsCursor.close();
        this.disconnect();
        return note;
    }

    /**
     * Removes all traits for given note from database.
     *
     * @param note  note to remove traits for.
     */
    protected void removeTraitsFromDatabase(final Note note) {
        this.connect();
        String where = String.format("%s = %d", this.dbColumns.get("note"), note.getId());
        this.database.delete(this.dbHelper.getNoteTraitTable(), where, null);
        this.disconnect();
    }

    /**
     * Creates trait using data at current position of given cursor.
     *
     * @param cursor  cursor containing trait data
     * @return Trait object
     */
    private Trait cursorToTrait(final Cursor cursor) {
        long id = cursor.getLong(1);
        return this.traitDataSource.get(id);
    }

}
