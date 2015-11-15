package com.robcutmore.vinotes.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.models.Trait;


/**
 * Adds and returns trait.
 */
public class AddTraitTask extends AsyncTask<Void, Void, Trait> {

    /**
     * Interface to be implemented by calling activity for returning trait.
     */
    public interface TaskListener {
        void onTaskFinished(Trait trait);
    }

    private ProgressDialog progress;
    private TaskListener callbackListener;
    private TraitDataSource traitDataSource;
    private Trait trait;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     */
    public AddTraitTask(final Context context, final TaskListener listener, final Trait trait) {
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(context.getString(R.string.progress_save_trait));
        this.callbackListener = listener;
        this.traitDataSource = new TraitDataSource(context.getApplicationContext());
        this.trait = trait;
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
     * Adds new trait to API and database.
     *
     * @return new trait
     */
    @Override
    protected Trait doInBackground(final Void... params) {
        return this.traitDataSource.add(this.trait);
    }

    /**
     * Sends trait to callback listener.
     *
     * @param trait  new trait
     */
    @Override
    protected void onPostExecute(final Trait trait) {
        super.onPostExecute(trait);
        this.progress.dismiss();
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(trait);
        }
    }

}
