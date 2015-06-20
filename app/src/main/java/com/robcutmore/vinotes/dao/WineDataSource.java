package com.robcutmore.vinotes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.database.WineDatabaseHelper;
import com.robcutmore.vinotes.model.Winery;

import java.util.HashMap;


public class WineDataSource extends DataSource {

    private WineryDataSource wineryDataSource;

    public WineDataSource(Context context) {
        this.dbHelper = new WineDatabaseHelper(context);
        this.dbColumns = this.dbHelper.getColumns();
        this.wineryDataSource = new WineryDataSource(context);
    }

    public Wine createWine(final long id, final long wineryId, final String name, final int vintage) {
        // Prepare wine values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), id);
        values.put(this.dbColumns.get("winery"), wineryId);
        values.put(this.dbColumns.get("name"), name);
        values.put(this.dbColumns.get("vintage"), vintage);

        // Insert wine into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Create and return wine with given information.
        Winery winery = this.wineryDataSource.getWinery(wineryId);
        Wine wine = new Wine(winery, name, vintage);
        wine.setId(id);
        return wine;
    }

    public void deleteWine(final long id) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public Wine getWine(final long id) {
        // Query wines table for wine with given id.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return wine.
        cursor.moveToFirst();
        Wine wine = !cursor.isAfterLast() ? this.cursorToWine(cursor) : null;
        cursor.close();
        return wine;
    }

    public HashMap<Long, Wine> getAllWines() {
        // Query wines table for all wines.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all wines.
        HashMap<Long, Wine> wines = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Wine wine = this.cursorToWine(cursor);
            wines.put(wine.getId(), wine);
            cursor.moveToNext();
        }
        cursor.close();
        return wines;
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

    private Wine cursorToWine(final Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        long wineryId = cursor.getLong(1);
        String name = cursor.getString(2);
        int vintage = cursor.getInt(3);

        // Create wine object using information from cursor.
        Winery winery = this.wineryDataSource.getWinery(wineryId);
        Wine wine = new Wine(winery, name, vintage);
        wine.setId(id);
        return wine;
    }

}