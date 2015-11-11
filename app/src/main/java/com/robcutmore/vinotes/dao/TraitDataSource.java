package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.models.Trait;
import com.robcutmore.vinotes.request.TraitRequest;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Manages adding, retrieving, and deleting trait-related data.
 * Interacts with API and local database.
 */
public class TraitDataSource extends DataSource {

    /**
     * Constructor.
     */
    public TraitDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getTraitColumns();
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  true or false for closing connection after operations
     */
    protected TraitDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    // Public methods

    /**
     * Adds new trait.
     *
     * @param newTrait  new trait to add
     * @return Trait object
     */
    public Trait add(final Trait newTrait) {
        // Add new note trait to API and, if successful, add to database as well.
        Trait trait = TraitRequest.add(newTrait);
        if (trait != null) {
            this.addToDatabase(trait);
        }
        return trait;
    }

    /**
     * Fetches trait with given id.
     *
     * @param id  id of trait to retrieve
     * @return Trait object
     */
    public Trait get(final long id) {
        // Fetch winery from local database. If missing then request from API.
        // If missing then request from API and, if found, add to database.
        Trait trait = this.getFromDatabase(id);
        if (trait == null) {
            trait = TraitRequest.get(id);
            if (trait != null) {
                this.addToDatabase(trait);
            }
        }
        return trait;
    }

    /**
     * Fetches all traits, either from API or database.
     * Repopulates database when fetching traits from API.
     *
     * @param refreshFromAPI  true or false to refresh with data from API
     * @return ArrayList containing Trait objects
     */
    public ArrayList<Trait> getAll(final boolean refreshFromAPI) {
        ArrayList<Trait> traits;
        if (refreshFromAPI) {
            // Repopulate traits in database with data from API.
            this.removeAllFromDatabase();
            traits = TraitRequest.getAll();
            for (Trait trait : traits) {
                this.addToDatabase(trait);
            }
        } else {
            // Retrieve all traits from database.
            traits = this.getAllFromDatabase();
        }
        return traits;
    }

    /**
     * Deletes given trait.
     *
     * @param trait  trait to delete
     */
    public void remove(final Trait trait) {
        this.connect();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), trait.getId());
        this.database.delete(this.dbHelper.getTraitTable(), whereClause, null);
        this.disconnect();
    }

    // Protected / private database methods.

    /**
     * Connects to database.
     */
    @Override
    protected void connect() {
        try {
            this.setDatabase();
        } catch (SQLException e) {
            Log.w(TraitDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * @return Array containing column names for traits database table
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
    private void addToDatabase(final Trait trait) {
        this.connect();
        String table = this.dbHelper.getTraitTable();
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), trait.getId());
        values.put(this.dbColumns.get("name"), trait.getName());
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.disconnect();
    }

    /**
     * Fetches all traits from database.
     *
     * @return ArrayList containing Trait objects
     */
    private ArrayList<Trait> getAllFromDatabase() {
        this.connect();

        // Fetch all traits from database.
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        String orderBy = String.format("%s COLLATE NOCASE ASC", this.dbColumns.get("name"));
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Process all results and return traits.
        ArrayList<Trait> traits = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            traits.add(this.cursorToTrait(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.disconnect();
        return traits;
    }

    /**
     * Fetches trait with given id from database.
     *
     * @param id  id of trait to retrieve
     * @return Trait object
     */
    private Trait getFromDatabase(final long id) {
        this.connect();

        // Fetch trait with given id from database.
        String table = this.dbHelper.getTraitTable();
        String[] columns = this.getDatabaseTableColumns();
        String where = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, where, null, null, null, null);

        // Return trait.
        cursor.moveToFirst();
        Trait trait = !cursor.isAfterLast() ? this.cursorToTrait(cursor) : null;
        cursor.close();
        this.disconnect();
        return trait;
    }

    /**
     * Deletes all traits from database.
     */
    private void removeAllFromDatabase() {
        this.connect();
        this.database.delete(this.dbHelper.getTraitTable(), null, null);
        this.disconnect();
    }

    /**
     * Creates trait using data at current position of cursor.
     *
     * @param cursor  cursor containing trait data
     * @return Trait object
     */
    private Trait cursorToTrait(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create trait object with information from cursor.
        return new Trait(id, name);
    }

}
