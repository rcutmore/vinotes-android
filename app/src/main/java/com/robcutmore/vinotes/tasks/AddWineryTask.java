package com.robcutmore.vinotes.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.models.Winery;


/**
 * Adds and returns new winery.
 */
public class AddWineryTask extends AsyncTask<Winery, Void, Winery> {

    /**
     * Interface to be implemented by calling activity for returning newly added winery.
     */
    public interface TaskListener {
        void onTaskFinished(Winery winery);
    }

    private TaskListener callbackListener;
    private WineryDataSource wineryDataSource;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     */
    public AddWineryTask(final Context context, final TaskListener listener) {
        this.callbackListener = listener;
        this.wineryDataSource = new WineryDataSource(context.getApplicationContext());
    }

    /**
     * Adds new winery to API and database.
     *
     * @param wineries  wineries to add
     * @return new winery
     */
    @Override
    protected Winery doInBackground(Winery... wineries) {
        Winery wineryToAdd = wineries[0];
        return this.wineryDataSource.add(wineryToAdd);
    }

    /**
     * Sends newly added winery to callback listener.
     *
     * @param winery  new winery
     */
    @Override
    protected void onPostExecute(Winery winery) {
        super.onPostExecute(winery);
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(winery);
        }
    }

}
