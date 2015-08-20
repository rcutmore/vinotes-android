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


public class WineryDataSource extends DataSource {

    public WineryDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getWineryColumns();
    }

    protected WineryDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    public Winery add(final String name) {
        // Add new winery to API.
        Winery winery = WineryRequest.add(name);

        // If winery was successfully added to API then add to local database as well.
        if (winery != null) {
            this.addToDatabase(winery);
        }

        return winery;
    }

    public void remove(final long id) {
        // Only remove from database, wineries cannot be removed from API.
        String table = this.dbHelper.getWineryTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        this.database.delete(table, whereClause, null);
        this.close();
    }

    public Winery get(final long id) {
        // Fetch winery from local database. If missing then request from API.
        Winery winery = this.getFromDatabase(id);
        if (winery == null) {
            winery = WineryRequest.get(id);

            // If winery is found then add to database since it was missing.
            if (winery != null) {
                this.addToDatabase(winery);
            }
        }
        return winery;
    }

    public ArrayList<Winery> getAll(final boolean refreshFromAPI) {
        ArrayList<Winery> wineries;
        if (refreshFromAPI) {
            wineries = WineryRequest.getAll();

            for (Winery winery : wineries) {
                this.addToDatabase(winery);
            }
        } else {
            wineries = this.getAllFromDatabase();
        }

        return wineries;
    }

    @Override
    protected void connectToDatabase() {
        try {
            this.open();
        } catch (SQLException e) {
            Log.w(WineryDataSource.class.getName(), "Error connecting to database.");
        }
    }

    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("name")
        };
        return columns;
    }

    private void addToDatabase(final Winery winery) {
        // Prepare winery values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), winery.getId());
        values.put(this.dbColumns.get("name"), winery.getName());

        // Insert winery into database if it doesn't exist yet.
        String table = this.dbHelper.getWineryTable();
        this.connectToDatabase();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.close();
    }

    private ArrayList<Winery> getAllFromDatabase() {
        String table = this.dbHelper.getWineryTable();
        String[] columns = this.getDatabaseTableColumns();

        // Order results by winery name.
        String orderBy = String.format("%s COLLATE NOCASE ASC", this.dbColumns.get("name"));

        // Query wineries table for all wineries.
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, orderBy);

        // Store and return wineries.
        cursor.moveToFirst();
        ArrayList<Winery> wineries = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            wineries.add(this.cursorToWinery(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.close();
        return wineries;
    }

    private Winery getFromDatabase(final long id) {
        // Query wineries table for winery with given id.
        String table = this.dbHelper.getWineryTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return winery.
        cursor.moveToFirst();
        Winery winery = !cursor.isAfterLast() ? this.cursorToWinery(cursor) : null;
        cursor.close();
        this.close();
        return winery;
    }

    private Winery cursorToWinery(Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create winery object with information from cursor.
        return new Winery(id, name);
    }

}
