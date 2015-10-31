package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;
import com.robcutmore.vinotes.request.WineRequest;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Manages adding, retrieving, and deleting wine-related data.
 * Interacts with API and local database.
 */
public class WineDataSource extends DataSource {

    private WineryDataSource wineryDataSource;

    /**
     * Constructor.
     */
    public WineDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getWineColumns();
        this.wineryDataSource = new WineryDataSource(context, false);
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  true or false for closing connection after operations
     */
    protected WineDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    // Public methods

    /**
     * Adds new wine.
     *
     * @param newWine  new wine to add
     * @return Wine object
     */
    public Wine add(final Wine newWine) {
        // Add new wine to API and, if successful, add to database as well.
        Wine wine = WineRequest.add(newWine);
        if (wine != null) {
            this.addToDatabase(wine);
        }
        return wine;
    }

    /**
     * Fetches wine with given id.
     *
     * @param id  id of wine to retrieve
     * @return Wine object
     */
    public Wine get(final long id) {
        // Fetch wine from local database.
        // If missing then request from API and, if found, add to database.
        Wine wine = this.getFromDatabase(id);
        if (wine == null) {
            wine = WineRequest.get(id);
            if (wine != null) {
                this.addToDatabase(wine);
            }
        }
        return wine;
    }

    /**
     * Fetches all wines, either from API or database.
     * Repopulates database when fetching wines from API.
     *
     * @param refreshFromAPI  true or false to refresh with data from API
     * @return ArrayList containing Wine objects
     */
    public ArrayList<Wine> getAll(final boolean refreshFromAPI) {
        ArrayList<Wine> wines;
        if (refreshFromAPI) {
            // Repopulate wines in database with data from API.
            this.removeAllFromDatabase();
            wines = WineRequest.getAll();
            for (Wine wine : wines) {
                this.addToDatabase(wine);
            }
        } else {
            // Retrieve all wines from database.
            wines = this.getAllFromDatabase(null);
        }
        return wines;
    }

    /**
     * Fetch all wines from database for given winery.
     *
     * @param wineryId  id of winery to get wines for
     * @return ArrayList containing Wine objects
     */
    public ArrayList<Wine> getAllForWinery(final long wineryId) {
        return this.getAllFromDatabase(wineryId);
    }

    /**
     * Deletes given wine.
     *
     * @param wine  wine to delete
     */
    public void remove(final Wine wine) {
        this.connect();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), wine.getId());
        this.database.delete(this.dbHelper.getWineTable(), whereClause, null);
        this.disconnect();
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
            Log.w(WineDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * @return Array containing column names for wines database table
     */
    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("winery"),
            this.dbColumns.get("name"),
            this.dbColumns.get("vintage")
        };
        return columns;
    }

    /**
     * Adds given wine to database.
     *
     * @param wine  wine to add to database
     */
    private void addToDatabase(final Wine wine) {
        this.connect();
        String table = this.dbHelper.getWineTable();
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), wine.getId());
        values.put(this.dbColumns.get("winery"), wine.getWinery().getId());
        values.put(this.dbColumns.get("name"), wine.getName());
        values.put(this.dbColumns.get("vintage"), wine.getVintage());
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.disconnect();
    }

    /**
     * Fetches all wines from database.
     *
     * @param wineryId  id of winery to get wines for
     * @return ArrayList containing Wine objects
     */
    private ArrayList<Wine> getAllFromDatabase(final Long wineryId) {
        this.connect();

        // Fetch all wines from database.
        String table = this.dbHelper.getWineTable();
        String[] columns = this.getDatabaseTableColumns();
        String where;
        if (wineryId == null) {
            where = null;
        } else {
            where = String.format("%s = %d", this.dbColumns.get("winery"), wineryId);
        }
        String orderBy = String.format(
            "%s COLLATE NOCASE ASC, %s DESC",
            this.dbColumns.get("name"), this.dbColumns.get("vintage")
        );
        Cursor cursor = this.database.query(table, columns, where, null, null, null, orderBy);

        // Process all results and return wines.
        ArrayList<Wine> wines = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            wines.add(this.cursorToWine(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.disconnect();
        return wines;
    }

    /**
     * Fetches wine with given id from database.
     *
     * @param id  id of wine to retrieve
     * @return Wine object
     */
    private Wine getFromDatabase(final long id) {
        this.connect();

        // Fetch wine with given id from database.
        String table = this.dbHelper.getWineTable();
        String[] columns = this.getDatabaseTableColumns();
        String where = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, where, null, null, null, null);

        // Return wine.
        cursor.moveToFirst();
        Wine wine = !cursor.isAfterLast() ? this.cursorToWine(cursor) : null;
        cursor.close();
        this.disconnect();
        return wine;
    }

    /**
     * Deletes all wines from database.
     */
    private void removeAllFromDatabase() {
        this.connect();
        this.database.delete(this.dbHelper.getWineTable(), null, null);
        this.disconnect();
    }

    /**
     * Creates wine using data at current position of cursor.
     *
     * @param cursor  cursor containing wine data
     * @return Wine object
     */
    private Wine cursorToWine(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineryId = cursor.getLong(1);
        String name = cursor.getString(2);
        int vintage = cursor.getInt(3);

        // Create wine object with information from cursor.
        final Winery winery = this.wineryDataSource.get(wineryId);
        return new Wine(id, winery, name, vintage);
    }

}
