package com.robcutmore.vinotes.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.models.Note;

import java.util.ArrayList;


/**
 * Refreshes data and returns all notes.
 */
public class FetchDataTask extends AsyncTask<Void, Void, ArrayList<Note>> {

    /**
     * Interface to be implemented by calling activity for returning notes.
     */
    public interface TaskListener {
        void onTaskFinished(ArrayList<Note> notes);
    }

    private ProgressDialog progress;
    private TaskListener callbackListener;
    private TraitDataSource traitDataSource;
    private WineryDataSource wineryDataSource;
    private WineDataSource wineDataSource;
    private NoteDataSource noteDataSource;
    private boolean refreshFromApi;
    private boolean showProgress;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     * @param refreshFromApi  whether or not to refresh data from API
     * @param showProgress  whether or not to show progress during task execution
     */
    public FetchDataTask(
            final Context context,
            final TaskListener listener,
            final boolean refreshFromApi,
            final boolean showProgress
    ) {
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(context.getString(R.string.progress_fetch_data));
        this.callbackListener = listener;

        // Set data sources
        Context appContext = context.getApplicationContext();
        this.traitDataSource = new TraitDataSource(appContext);
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);
        this.noteDataSource = new NoteDataSource(appContext);

        this.refreshFromApi = refreshFromApi;
        this.showProgress = showProgress;
    }

    /**
     * Shows progress dialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.showProgress) {
            this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progress.setCancelable(true);
            this.progress.show();
        }
    }

    /**
     * Fetches application data and optionally refreshes data from API.
     *
     * @return all notes
     */
    @Override
    protected ArrayList<Note> doInBackground(Void... params) {
        if (this.refreshFromApi) {
            this.traitDataSource.getAll(true);
            this.wineryDataSource.getAll(true);
            this.wineDataSource.getAll(true);
        }
        return this.noteDataSource.getAll(this.refreshFromApi);
    }

    /**
     * Sends notes to callback listener.
     *
     * @param notes  all notes
     */
    @Override
    protected void onPostExecute(final ArrayList<Note> notes) {
        super.onPostExecute(notes);
        if (this.showProgress) {
            this.progress.dismiss();
        }
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(notes);
        }
    }

}
