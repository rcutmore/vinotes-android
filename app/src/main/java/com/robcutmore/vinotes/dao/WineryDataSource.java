package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.robcutmore.vinotes.database.DatabaseHelper;
import com.robcutmore.vinotes.model.Winery;
import com.robcutmore.vinotes.request.WineryRequest;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Manages adding, retrieving, and deleting winery-related data.
 * Interacts with API and local database.
 */
public class WineryDataSource extends DataSource {

    /**
     * Constructor.
     */
    public WineryDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getWineryColumns();
    }

    /**
     * Constructor.
     *
     * @param closeDatabaseWhenFinished  Boolean for closing database after operations or not
     */
    protected WineryDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    // Public methods

    /**
     * Adds new winery.
     *
     * @param newWinery  new winery to add
     * @return Winery object
     */
    public Winery add(final Winery newWinery) {
        // Add new winery to API and, if successful, add to database as well.
        Winery winery = WineryRequest.add(newWinery);
        if (winery != null) {
            this.addToDatabase(winery);
        }
        return winery;
    }

    /**
     * Fetches winery with given id.
     *
     * @param id  id of winery to retrieve
     * @return Winery object
     */
    public Winery get(final long id) {
        // Fetch winery from local database.
        // If missing then request from API and, if found, add to database.
        Winery winery = this.getFromDatabase(id);
        if (winery == null) {
            winery = WineryRequest.get(id);
            if (winery != null) {
                this.addToDatabase(winery);
            }
        }
        return winery;
    }

    /**
     * Fetches all wines, either from API or database.
     * Repopulates database when fetching wines from API.
     *
     * @param refreshFromAPI  true or false to refresh with data from API
     * @return ArrayList containing Winery objects
     */
    public ArrayList<Winery> getAll(final boolean refreshFromAPI) {
        ArrayList<Winery> wineries;
        if (refreshFromAPI) {
            // Repopulate wineries in database with data from API.
            this.removeAllFromDatabase();
            wineries = WineryRequest.getAll();
            for (Winery winery : wineries) {
                this.addToDatabase(winery);
            }
        } else {
            // Retrieve all wineries from database.
            wineries = this.getAllFromDatabase();
        }
        return wineries;
    }

    /**
     * Deletes given winery.
     *
     * @param winery  winery to delete.
     */
    public void remove(final Winery winery) {
        this.connect();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), winery.getId());
        this.database.delete(this.dbHelper.getWineryTable(), whereClause, null);
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
            Log.w(WineryDataSource.class.getName(), "Error setting database.");
        }
    }

    /**
     * @return Array containing column names for wineries database table
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
     * Adds given winery to database.
     *
     * @param winery  winery to add to database
     */
    private void addToDatabase(final Winery winery) {
        this.connect();
        String table = this.dbHelper.getWineryTable();
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), winery.getId());
        values.put(this.dbColumns.get("name"), winery.getName());
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.disconnect();
    }

    /**
     * Fetches all wineries from database.
     *
     * @return ArrayList containing Winery objects.
     */
    private ArrayList<Winery> getAllFromDatabase() {
        this.connect();

        // Fetch all wineries from database.
        String table = this.dbHelper.getWineryTable();
        String[] columns = this.getDatabaseTableColumns();
        String orderBy = String.format("%s COLLATE NOCASE ASC", this.dbColumns.get("name"));
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Process all results and return wineries.
        ArrayList<Winery> wineries = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            wineries.add(this.cursorToWinery(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.disconnect();
        return wineries;
    }

    /**
     * Fetches winery with given id from database.
     *
     * @param id  id of winery to retrieve
     * @return Winery object
     */
    private Winery getFromDatabase(final long id) {
        this.connect();

        // Fetch winery with given id from database.
        String table = this.dbHelper.getWineryTable();
        String[] columns = this.getDatabaseTableColumns();
        String where = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, where, null, null, null, null);

        // Return winery.
        cursor.moveToFirst();
        Winery winery = !cursor.isAfterLast() ? this.cursorToWinery(cursor) : null;
        cursor.close();
        this.disconnect();
        return winery;
    }

    /**
     * Deletes all wineries from database.
     */
    private void removeAllFromDatabase() {
        this.connect();
        this.database.delete(this.dbHelper.getWineryTable(), null, null);
        this.disconnect();
    }

    /**
     * Creates winery using data at current position of cursor.
     *
     * @param cursor  cursor containing winery data
     * @return Winery object
     */
    private Winery cursorToWinery(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create winery object with information from cursor.
        return new Winery(id, name);
    }

}
