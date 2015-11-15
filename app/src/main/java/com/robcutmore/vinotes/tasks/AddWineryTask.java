package com.robcutmore.vinotes.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.models.Winery;


/**
 * Adds and returns winery.
 */
public class AddWineryTask extends AsyncTask<Void, Void, Winery> {

    /**
     * Interface to be implemented by calling activity for returning winery.
     */
    public interface TaskListener {
        void onTaskFinished(Winery winery);
    }

    private ProgressDialog progress;
    private TaskListener callbackListener;
    private WineryDataSource wineryDataSource;
    private Winery winery;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     * @param winery  winery to be added
     */
    public AddWineryTask(final Context context, final TaskListener listener, final Winery winery) {
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(context.getString(R.string.progress_save_winery));
        this.callbackListener = listener;
        this.wineryDataSource = new WineryDataSource(context.getApplicationContext());
        this.winery = winery;
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
     * Adds new winery to API and database.
     *
     * @return new winery
     */
    @Override
    protected Winery doInBackground(final Void... params) {
        return this.wineryDataSource.add(this.winery);
    }

    /**
     * Sends winery to callback listener.
     *
     * @param winery  new winery
     */
    @Override
    protected void onPostExecute(final Winery winery) {
        super.onPostExecute(winery);
        this.progress.dismiss();
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(winery);
        }
    }

}
