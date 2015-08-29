package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.NoteTrait;
import com.robcutmore.vinotes.request.NoteTraitRequest;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * NoteTraitDataSource manages adding, retrieving, and deleting trait-related data.
 * Interacts with API and local database.
 */
public class NoteTraitDataSource extends DataSource {

    /**
     * Constructor.
     */
    public NoteTraitDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getTraitColumns();
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  true or false for closing connection after operations
     */
    protected NoteTraitDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    /**
     * Adds new trait.
     *
     * @param name  name of new trait
     * @return NoteTrait object
     */
    public NoteTrait add(final String name) {
        // Add new note trait to API.
        NoteTrait trait = NoteTraitRequest.add(name);

        // If note trait was successfully added to API then add to local database as well.
        if (trait != null) {
            this.addToDatabase(trait);
        }

        return trait;
    }

    /**
     * Deletes trait with given id.
     *
     * @param id  id of trait to delete
     */
    public void remove(final long id) {
        String table = this.dbHelper.getTraitTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        this.database.delete(table, whereClause, null);
        this.close();
    }

    /**
     * Fetches trait with given id.
     *
     * @param id  id of trait to retrieve
     * @return NoteTrait object
     */
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

    /**
     * Fetches and returns all traits, either from API or database.
     * Repopulates database when fetching traits from API.
     *
     * @param refreshFromAPI  true or false to refresh with data from API
     * @return an ArrayList containing NoteTrait objects
     */
    public ArrayList<NoteTrait> getAll(final boolean refreshFromAPI) {
        ArrayList<NoteTrait> traits;
        if (refreshFromAPI) {
            // Repopulate traits in database with data from API.
            this.removeAllFromDatabase();
            traits = NoteTraitRequest.getAll();
            for (NoteTrait trait : traits) {
                this.addToDatabase(trait);
            }
        } else {
            // Retrieve all traits from database.
            traits = this.getAllFromDatabase();
        }
        return traits;
    }

    /**
     * Connects to database.
     */
    @Override
    protected void connectToDatabase() {
        try {
            this.open();
        } catch (SQLException e) {
            Log.w(NoteTraitDataSource.class.getName(), "Error connecting to database.");
        }
    }

    /**
     * Fetches database column names for traits table.
     *
     * @return an array containing column names
     */
    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("name")
        };
        return columns;
    }

    /**
     * Adds given trait to database.
     *
     * @param trait  trait to add to database
     */
    private void addToDatabase(final NoteTrait trait) {
        // Prepare trait values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), trait.getId());
        values.put(this.dbColumns.get("name"), trait.getName());

        // Insert trait into database if it doesn't exist yet.
        String table = this.dbHelper.getTraitTable();
        this.connectToDatabase();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.close();
    }

    /**
     * Fetches all traits from database.
     *
     * @return an ArrayList containing NoteTrait objects
     */
    private ArrayList<NoteTrait> getAllFromDatabase() {
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();

        // Order results by trait name.
        String orderBy = String.format("%s COLLATE NOCASE ASC", this.dbColumns.get("name"));

        // Query traits table for all traits.
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Store and return traits.
        cursor.moveToFirst();
        ArrayList<NoteTrait> traits = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            traits.add(this.cursorToTrait(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.close();
        return traits;
    }

    /**
     * Fetches trait with given id from database.
     *
     * @param id  id of trait to retrieve
     * @return NoteTrait object
     */
    private NoteTrait getFromDatabase(final long id) {
        // Query traits table for trait with given id.
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return note trait.
        cursor.moveToFirst();
        NoteTrait trait = !cursor.isAfterLast() ? this.cursorToTrait(cursor) : null;
        cursor.close();
        this.close();
        return trait;
    }

    /**
     * Deletes all traits from database.
     */
    private void removeAllFromDatabase() {
        this.connectToDatabase();
        this.database.delete(this.dbHelper.getTraitTable(), null, null);
        this.close();
    }

    /**
     * Creates trait using data at current position of cursor.
     *
     * @param cursor  cursor containing trait data
     * @return NoteTrait object
     */
    private NoteTrait cursorToTrait(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create note trait object with information from cursor.
        return new NoteTrait(id, name);
    }

}
