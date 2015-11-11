package com.robcutmore.vinotes.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.model.Trait;


/**
 * Adds and returns new trait.
 */
public class AddTraitTask extends AsyncTask<Trait, Void, Trait> {

    /**
     * Interface to be implemented by calling activity for returning newly added trait.
     */
    public interface TaskListener {
        void onTaskFinished(Trait trait);
    }

    private TaskListener callbackListener;
    private TraitDataSource traitDataSource;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     */
    public AddTraitTask(final Context context, final TaskListener listener) {
        this.callbackListener = listener;
        this.traitDataSource = new TraitDataSource(context.getApplicationContext());
    }

    /**
     * Adds new trait to API and database.
     *
     * @param traits  traits to add
     * @return new trait
     */
    @Override
    protected Trait doInBackground(Trait... traits) {
        Trait traitToAdd = traits[0];
        return this.traitDataSource.add(traitToAdd);
    }

    /**
     * Sends newly added trait to callback listener.
     *
     * @param trait  new trait
     */
    @Override
    protected void onPostExecute(Trait trait) {
        super.onPostExecute(trait);
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(trait);
        }
    }

}
