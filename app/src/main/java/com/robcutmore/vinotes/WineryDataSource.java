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

    public Winery getWinery(final long id) {
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

    public HashMap<Long, Winery> getAllWineries() {
        // Query wineries table for all wineries.
        String table = this.dbHelper.getTableName();
        String[] columns = this.getDatabaseTableColumns();
        Cursor cursor = this.database.query(table, columns, null, null, null, null, null);

        // Store and return all wineries.
        HashMap<Long, Winery> wineries = new HashMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Winery winery = this.cursorToWinery(cursor);
            wineries.put(winery.getId(), winery);
            cursor.moveToNext();
        }
        cursor.close();
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

    private Winery cursorToWinery(Cursor cursor) {
        // Get information for cursor's current position.
        long id = cursor.getLong(0);
        String name = cursor.getString(1);

        // Create winery object with information from cursor.
        Winery winery = new Winery(name);
        winery.setId(id);
        return winery;
    }

}