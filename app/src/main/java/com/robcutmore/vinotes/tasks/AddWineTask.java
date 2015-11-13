package com.robcutmore.vinotes.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.models.Wine;


/**
 * Adds and returns new wine.
 */
public class AddWineTask extends AsyncTask<Wine, Void, Wine> {

    /**
     * Interface to be implemented by calling activity for returning newly added wine.
     */
    public interface TaskListener {
        void onTaskFinished(Wine wine);
    }

    private TaskListener callbackListener;
    private WineDataSource wineDataSource;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     */
    public AddWineTask(final Context context, final TaskListener listener) {
        this.callbackListener = listener;
        this.wineDataSource = new WineDataSource(context.getApplicationContext());
    }

    /**
     * Adds new wine to API and database.
     *
     * @param wines  wines to add
     * @return new wine
     */
    @Override
    protected Wine doInBackground(Wine... wines) {
        Wine wineToAdd = wines[0];
        return this.wineDataSource.add(wineToAdd);
    }

    /**
     * Sends newly added wine to callback listener.
     *
     * @param wine  new wine
     */
    @Override
    protected void onPostExecute(Wine wine) {
        super.onPostExecute(wine);
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(wine);
        }
    }

}
