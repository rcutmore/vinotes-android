package com.robcutmore.vinotes.activities;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.models.Note;
import com.robcutmore.vinotes.tasks.FetchDataTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Lists all stored tasting notes.
 */
public class HomeActivity
        extends ActionBarActivity
        implements SwipeRefreshLayout.OnRefreshListener, FetchDataTask.TaskListener {

    // Request codes
    private final int ADD_NOTE_REQUEST_CODE = 1;
    private final int EDIT_NOTE_REQUEST_CODE = 2;

    // User input
    private SwipeRefreshLayout swipeLayout;
    private ListView lvNotes;

    // Note list
    private ArrayList<Note> notes;
    private ArrayAdapter<Note> notesAdapter;

    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        /**
         * Opens clicked note for viewing/editing.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Note note = (Note) parent.getAdapter().getItem(position);
            onViewNote(note);
        }
    };

    /**
     * Sets up activity and private variables.
     * Refreshes data from API and displays note list.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect list view to note array list.
        this.notes = new ArrayList<>();
        this.notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.notes);
        this.lvNotes = (ListView) findViewById(R.id.lvNotes);
        this.lvNotes.setAdapter(notesAdapter);
        this.lvNotes.setOnItemClickListener(this.clickListener);

        // Set SwipeRefreshLayout.
        this.swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        this.swipeLayout.setOnRefreshListener(this);

        this.fetchNotes(false, true);
    }

    /**
     * Sets up options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets up option items.
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens activity to add new note.
     *
     * @param view  button that was clicked
     */
    public void onAddNote(final View view) {
        Intent intent = new Intent(this, ManageNoteActivity.class);
        startActivityForResult(intent, this.ADD_NOTE_REQUEST_CODE);
    }

    /**
     * Refreshes all data from API.
     */
    @Override
    public void onRefresh() {
        this.fetchNotes(true, false);
    }

    /**
     * Displays all notes.
     *
     * @param notes  notes from database or API
     */
    @Override
    public void onTaskFinished(final ArrayList<Note> notes) {
        this.swipeLayout.setRefreshing(false);
        this.notes.clear();
        this.notes.addAll(notes);
        this.notesAdapter.notifyDataSetChanged();
    }

    // Protected methods

    /**
     * Handles result from adding a new note.
     *
     * @param requestCode  code to differentiate requests
     * @param resultCode  code for result status
     * @param data  contains returned data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        boolean handleAddNote = requestCode == this.ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK;
        boolean handleEditNote = requestCode == this.EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK;
        if (handleAddNote || handleEditNote) {
            Bundle args = (data != null) ? data.getExtras() : null;
            if (args != null) {
                Note note = args.getParcelable("note");
                this.addNote(note);
            }
        }
    }

    /**
     * Opens activity to view/edit note.
     *
     * @param note  note to view/edit
     */
    protected void onViewNote(final Note note) {
        Bundle args = new Bundle();
        args.putParcelable("note", note);
        Intent intent = new Intent(this, ManageNoteActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, this.EDIT_NOTE_REQUEST_CODE);
    }

    // Private methods

    /**
     * Adds note to note list.
     *
     * @param note  new note to add
     */
    private void addNote(final Note note) {
        int previousIndex = this.notes.indexOf(note);
        if (previousIndex > -1) {
            this.notes.set(previousIndex, note);
        } else {
            this.notes.add(note);
            Collections.sort(this.notes, new Comparator<Note>() {
                /**
                 * Sorts notes by tasting date.
                 *
                 * @param n1 first note
                 * @param n2 second note
                 * @return result of date comparison
                 */
                @Override
                public int compare(Note n1, Note n2) {
                    return n2.getTasted().compareTo(n1.getTasted());
                }
            });
        }
        this.notesAdapter.notifyDataSetChanged();
    }

    /**
     * Fetches notes.
     *
     * @param refreshFromAPI  whether or not to refresh data from API
     */
    private void fetchNotes(final boolean refreshFromAPI, final boolean showProgress) {
        new FetchDataTask(this, this, refreshFromAPI, showProgress).execute();
    }

}
