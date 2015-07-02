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

    public void remove(final long id) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public HashMap<Long, Wine> getAll() {
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

    private void addToDatabase(final Wine wine) {
        // Prepare wine values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), wine.getId());
        values.put(this.dbColumns.get("winery"), wine.getWinery().getId());
        values.put(this.dbColumns.get("name"), wine.getName());
        values.put(this.dbColumns.get("vintage"), wine.getVintage());

        // Insert wine into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private Wine getFromDatabase(final long id) {
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