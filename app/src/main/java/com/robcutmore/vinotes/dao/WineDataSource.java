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
 * WineDataSource manages adding, retrieving, and deleting wine-related data.
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

    /**
     * Adds new wine.
     *
     * @param wineryId  id of winery for new wine
     * @param name  name of new wine
     * @param vintage  vintage (year) of new wine
     * @return Wine object
     */
    public Wine add(final long wineryId, final String name, final int vintage) {
        // Add new wine to API.
        Wine wine = WineRequest.add(wineryId, name, vintage);

        // If wine was successfully added to API then add to local database as well.
        if (wine != null) {
            this.addToDatabase(wine);
        }

        return wine;
    }

    /**
     * Deletes wine with given id.
     *
     * @param id  id of wine to delete
     */
    public void remove(final long id) {
        String table = this.dbHelper.getWineTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connect();
        this.database.delete(table, whereClause, null);
        this.disconnect();
    }

    /**
     * Fetches wine with given id.
     *
     * @param id  id of wine to retrieve
     * @return Wine object
     */
    public Wine get(final long id) {
        // Fetch winery from local database. If missing then request from API.
        Wine wine = this.getFromDatabase(id);
        if (wine == null) {
            wine = WineRequest.get(id);

            // If wine is found then add to database since it was missing.
            if (wine != null) {
                this.addToDatabase(wine);
            }
        }
        return wine;
    }

    /**
     * Fetches and returns all wines, either from API or database.
     * When fetching wines from API database is repopulated.
     *
     * @param refreshFromAPI  whether or not to refresh with data from API
     * @return an ArrayList containing Winery objects
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
     * @return an ArrayList containing Wine objects
     */
    public ArrayList<Wine> getAllForWinery(final long wineryId) {
        return this.getAllFromDatabase(wineryId);
    }

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
     * Fetches database column names.
     *
     * @return an array containing column names for wines table
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
     * @param wine  Wine object to add to database
     */
    private void addToDatabase(final Wine wine) {
        // Prepare wine values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), wine.getId());
        values.put(this.dbColumns.get("winery"), wine.getWinery().getId());
        values.put(this.dbColumns.get("name"), wine.getName());
        values.put(this.dbColumns.get("vintage"), wine.getVintage());

        // Insert wine into database if it doesn't exist yet.
        String table = this.dbHelper.getWineTable();
        this.connect();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.disconnect();
    }

    /**
     * Fetches all wines from database.
     *
     * @param wineryId  id of winery to get wines for
     * @return an ArrayList containing Wine objects
     */
    private ArrayList<Wine> getAllFromDatabase(final Long wineryId) {
        String table = this.dbHelper.getWineTable();
        String[] columns = this.getDatabaseTableColumns();

        // See if we need to filter for a given winery.
        String whereClause;
        if (wineryId == null) {
            whereClause = null;
        } else {
            whereClause = String.format("%s = %d", this.dbColumns.get("winery"), wineryId);
        }

        // Order results by wine name and vintage.
        String orderBy = String.format("%s COLLATE NOCASE ASC, %s DESC",
                                       this.dbColumns.get("name"), this.dbColumns.get("vintage"));

        // Query wines table for all wines.
        this.connect();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, orderBy);

        // Store and return wines.
        cursor.moveToFirst();
        ArrayList<Wine> wines = new ArrayList<>();
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
        // Query wines table for wine with given id.
        String table = this.dbHelper.getWineTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connect();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return wine.
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
     * Use data at current position of cursor to create Wine object.
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

        // Create wine object using information from cursor.
        final Winery winery = this.wineryDataSource.get(wineryId);
        return new Wine(id, winery, name, vintage);
    }

}
