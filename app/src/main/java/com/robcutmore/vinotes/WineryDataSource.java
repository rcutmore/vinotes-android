package com.robcutmore.vinotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;


public class WineryDataSource extends DataSource {

    public WineryDataSource(Context context) {
        this.dbHelper = new WineryDatabaseHelper(context);
        this.dbColumns = this.dbHelper.getColumns();
    }

    public Winery createWinery(final long id, final String name) {
        // Prepare winery values to be inserted into database.
        ContentValues values = new ContentValues();
        values.put(this.dbColumns.get("id"), id);
        values.put(this.dbColumns.get("name"), name);

        // Insert winery into database if it doesn't exist yet.
        String table = this.dbHelper.getTableName();
        this.database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Create and return winery object with given information.
        Winery winery = new Winery(name);
        winery.setId(id);
        return winery;
    }

    public void deleteWinery(final Winery winery) {
        String table = this.dbHelper.getTableName();
        String whereClause = String.format("%s = %d", this.dbColumns.get("id"), winery.getId());
        this.database.delete(table, whereClause, null);
    }

    public HashMap<Long, Winery> getAllWineries() {
        // Query wineries table for all wineries.
        String table = this.dbHelper.getTableName();
        String[] columns = {
            this.dbColumns.get("id"),
            this.dbColumns.get("name")
        };
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all wineries.
        HashMap<Long, Winery> wineries = new HashMap<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Winery winery = this.cursorToWinery(cursor);
            wineries.put(winery.getId(), winery);
            cursor.moveToNext();
        }
        cursor.close();
        return wineries;
    }

    private Winery cursorToWinery(Cursor cursor) {
        Winery winery = new Winery(cursor.getString(1));
        winery.setId(cursor.getLong(0));
        return winery;
    }

}