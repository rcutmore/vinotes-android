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


public class WineDataSource extends DataSource {

    private WineryDataSource wineryDataSource;

    public WineDataSource(final Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.dbColumns = this.dbHelper.getWineColumns();
        this.wineryDataSource = new WineryDataSource(context, false);
    }

    protected WineDataSource(final Context context, final boolean closeDatabaseWhenFinished) {
        this(context);
        this.closeDatabaseWhenFinished = closeDatabaseWhenFinished;
    }

    public Wine add(final long wineryId, final String name, final int vintage) {
        // Add new wine to API.
        Wine wine = WineRequest.add(wineryId, name, vintage);

        // If wine was successfully added to API then add to local database as well.
        if (wine != null) {
            this.addToDatabase(wine);
        }

        return wine;
    }

    public void remove(final long id) {
        String table = this.dbHelper.getWineTable();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        this.database.delete(table, whereClause, null);
        this.close();
    }

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

    public ArrayList<Wine> getAll(final boolean refreshFromAPI) {
        ArrayList<Wine> wines;
        if (refreshFromAPI) {
            wines = WineRequest.getAll();

            for (Wine wine : wines) {
                this.addToDatabase(wine);
            }
        } else {
            wines = this.getAllFromDatabase(null);
        }

        return wines;
    }

    public ArrayList<Wine> getAllForWinery(final long wineryId) {
        return this.getAllFromDatabase(wineryId);
    }

    @Override
    protected void connectToDatabase() {
        try {
            this.open();
        } catch (SQLException e) {
            Log.w(WineDataSource.class.getName(), "Error connecting to database.");
        }
    }

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

    private void addToDatabase(final Wine wine) {
        // Prepare wine values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), wine.getId());
        values.put(this.dbColumns.get("winery"), wine.getWinery().getId());
        values.put(this.dbColumns.get("name"), wine.getName());
        values.put(this.dbColumns.get("vintage"), wine.getVintage());

        // Insert wine into database if it doesn't exist yet.
        String table = this.dbHelper.getWineTable();
        this.connectToDatabase();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        this.close();
    }

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

        // Query wines table for all wines.
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return wines.
        cursor.moveToFirst();
        ArrayList<Wine> wines = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            wines.add(this.cursorToWine(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        this.close();
        return wines;
    }

    private Wine getFromDatabase(final long id) {
        // Query wines table for wine with given id.
        String table = this.dbHelper.getWineTable();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.connectToDatabase();
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return wine.
        cursor.moveToFirst();
        Wine wine = !cursor.isAfterLast() ? this.cursorToWine(cursor) : null;
        cursor.close();
        this.close();
        return wine;
    }

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
