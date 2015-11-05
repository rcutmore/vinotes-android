package com.robcutmore.vinotes.ui;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Lists all stored tasting notes.
 */
public class HomeActivity extends ActionBarActivity {

    private final int ADD_NOTE_REQUEST_CODE = 1;
    private final int EDIT_NOTE_REQUEST_CODE = 2;

    // Data sources
    private TraitDataSource traitDataSource;
    private NoteDataSource noteDataSource;
    private WineDataSource wineDataSource;
    private WineryDataSource wineryDataSource;

    // Note list
    private ArrayList<Note> notes;
    private ArrayAdapter<Note> notesAdapter;
    private ListView lvNotes;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize data sources.
        Context appContext = this.getApplicationContext();
        this.traitDataSource = new TraitDataSource(appContext);
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);
        this.noteDataSource = new NoteDataSource(appContext);

        // Connect list view to note array list.
        this.notes = new ArrayList<>();
        this.notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.notes);
        this.lvNotes = (ListView) findViewById(R.id.lvNotes);
        this.lvNotes.setAdapter(notesAdapter);
        this.lvNotes.setOnItemClickListener(this.clickListener);

        this.refreshNoteList(true);
    }

    /**
     * Sets up options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sets up option items.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
     * Opens activity to add new note.
     */
    public void onAddNote(View view) {
        Intent intent = new Intent(this, ManageNoteActivity.class);
        startActivityForResult(intent, this.ADD_NOTE_REQUEST_CODE);
    }

    /**
     * Opens activity to view/edit note.
     *
     * @param note  note to view/edit
     */
    public void onViewNote(Note note) {
        Bundle args = new Bundle();
        args.putParcelable("note", note);
        Intent intent = new Intent(this, ManageNoteActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, this.EDIT_NOTE_REQUEST_CODE);
    }

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
     * Refreshes app data and displays updated note list.
     *
     * @param refreshFromAPI  true or false for refreshing data from API
     */
    private void refreshNoteList(final boolean refreshFromAPI) {
        // See if data should be refreshed from API.
        if (refreshFromAPI) {
            this.traitDataSource.getAll(true);
            this.wineryDataSource.getAll(true);
            this.wineDataSource.getAll(true);
        }
        ArrayList<Note> allNotes = this.noteDataSource.getAll(refreshFromAPI);

        // Add notes to note list.
        this.notes.clear();
        this.notes.addAll(allNotes);
        this.notesAdapter.notifyDataSetChanged();
    }

}
