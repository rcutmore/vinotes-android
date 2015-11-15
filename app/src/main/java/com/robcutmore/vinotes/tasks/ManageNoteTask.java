package com.robcutmore.vinotes.tasks;


import android.content.Context;
import android.os.AsyncTask;

import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.models.Note;


/**
 * Adds and returns new note.
 */
public class ManageNoteTask extends AsyncTask<Void, Void, Note> {

    /**
     * Interface to be implemented by calling activity for returning newly added note.
     */
    public interface TaskListener {
        void onTaskFinished(Note note);
    }

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
        this.callbackListener = listener;
        this.noteDataSource = new NoteDataSource(context.getApplicationContext());
        this.note = note;
        this.updateNote = updateNote;
    }

    /**
     * Adds new note to API and database or updates existing note.
     *
     * @return note
     */
    @Override
    protected Note doInBackground(Void... params) {
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
    protected void onPostExecute(Note note) {
        super.onPostExecute(note);
        if (this.callbackListener != null) {
            this.callbackListener.onTaskFinished(note);
        }
    }

}
