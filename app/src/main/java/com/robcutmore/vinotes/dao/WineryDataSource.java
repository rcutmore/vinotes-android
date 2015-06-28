package com.robcutmore.vinotes.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robcutmore.vinotes.model.Winery;
import com.robcutmore.vinotes.database.WineryDatabaseHelper;
import com.robcutmore.vinotes.request.WineryRequest;

import java.util.HashMap;


public class WineryDataSource extends DataSource {

    public WineryDataSource(Context context) {
        this.dbHelper = new WineryDatabaseHelper(context);
        this.dbColumns = this.dbHelper.getColumns();
    }

    public Winery add(final String name) {
        // Add new winery to API.
        Winery winery = WineryRequest.add(name);

        // If winery was successfully added to API then add to local database as well.
        if (winery != null) {
            this.addToDatabase(winery.getId(), winery.getName());
        }

        return winery;
    }

    public void remove(final long id) {
        // Only remove from database, wineries cannot be removed from API.
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        this.database.delete(table, whereClause, null);
    }

    public Winery get(final long id) {
        // Fetch winery from local database. If null then request from API before returning.
        Winery winery = this.getFromDatabase(id);
        if (winery == null) {
            winery = WineryRequest.get(id);
        }
        return winery;
    }

    public HashMap<Long, Winery> getAll() {
        // Request all wineries from external API.
        Winery[] wineriesFromAPI =  WineryRequest.getAll();

        // Add each winery to local database and ArrayList to be returned.
        HashMap<Long, Winery> wineries = new HashMap<>();
        for (int i = 0; i < wineriesFromAPI.length; i++) {
            Winery winery = wineriesFromAPI[i];
            this.addToDatabase(winery.getId(), winery.getName());
            wineries.put(winery.getId(), winery);
        }

        return wineries;
    }

    @Override
    protected String[] getDatabaseTableColumns() {
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("name")
        };
        return columns;
    }

    private void addToDatabase(final long id, final String name) {
        // Prepare winery values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), id);
        values.put(this.dbColumns.get("name"), name);

        // Insert winery into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private Winery getFromDatabase(final long id) {
        // Query wineries table for winery with given id.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), id);
        Cursor cursor = this.database.query(table, columns, whereClause, null, null, null, null);

        // Store and return winery.
        cursor.moveToFirst();
        Winery winery = !cursor.isAfterLast() ? this.cursorToWinery(cursor) : null;
        cursor.close();
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