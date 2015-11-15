package com.robcutmore.vinotes.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.models.Note;


/**
 * Adds/updates and returns note.
 */
public class ManageNoteTask extends AsyncTask<Void, Void, Note> {

    /**
     * Interface to be implemented by calling activity for returning note.
     */
    public interface TaskListener {
        void onTaskFinished(Note note);
    }

    private ProgressDialog progress;
    private TaskListener callbackListener;
    private NoteDataSource noteDataSource;
    private Note note;
    private boolean updateNote;

    /**
     * Constructor.
     *
     * @param context  calling activity's context
     * @param listener  callback listener to set
     * @param note  note to add or update
     * @param updateNote  true to update note, false to add new note
     */
    public ManageNoteTask(final Context context, final TaskListener listener,
                          final Note note, final boolean updateNote) {
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(context.getString(R.string.progress_save_note));
        this.callbackListener = listener;
        this.noteDataSource = new NoteDataSource(context.getApplicationContext());
        this.note = note;
        this.updateNote = updateNote;
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
     * Adds new note to API and database or updates existing note.
     *
     * @return note
     */
    @Override
    protected Note doInBackground(final Void... params) {
        if (this.updateNote) {
            return this.noteDataSource.update(this.note);
        } else {
            return this.noteDataSource.add(this.note);
        }
    }

    /**
     * Sends note to callback listener.
     *
     * @param note  new/updated note
     */
    @Override
    protected void onPostExecute(final Note note) {
        super.onPostExecute(note);
        this.progress.dismiss();
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(note);
        }
    }

}
