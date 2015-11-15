package com.robcutmore.vinotes.tasks;


import android.content.Context;
import android.os.AsyncTask;

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
        this.callbackListener = listener;
        this.traitDataSource = new TraitDataSource(context.getApplicationContext());
        this.trait = trait;
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
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(trait);
        }
    }

}
