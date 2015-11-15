package com.robcutmore.vinotes.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.models.Wine;


/**
 * Adds and returns wine.
 */
public class AddWineTask extends AsyncTask<Void, Void, Wine> {

    /**
     * Interface to be implemented by calling activity for returning wine.
     */
    public interface TaskListener {
        void onTaskFinished(Wine wine);
    }

    private ProgressDialog progress;
    private TaskListener callbackListener;
    private WineDataSource wineDataSource;
    private Wine wine;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     */
    public AddWineTask(final Context context, final TaskListener listener, final Wine wine) {
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(context.getString(R.string.progress_save_wine));
        this.callbackListener = listener;
        this.wineDataSource = new WineDataSource(context.getApplicationContext());
        this.wine = wine;
    }

    /**
     * Shows progress dialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progress.setCancelable(false);
        this.progress.show();
    }

    /**
     * Adds new wine to API and database.
     *
     * @return new wine
     */
    @Override
    protected Wine doInBackground(final Void... params) {
        return this.wineDataSource.add(this.wine);
    }

    /**
     * Sends wine to callback listener.
     *
     * @param wine  new wine
     */
    @Override
    protected void onPostExecute(final Wine wine) {
        super.onPostExecute(wine);
        this.progress.dismiss();
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(wine);
        }
    }

}
