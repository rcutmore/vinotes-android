package com.robcutmore.vinotes.ui;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Note;

import java.util.ArrayList;


/**
 * MainActivity lists all stored tasting notes.
 */
public class MainActivity extends ActionBarActivity {

    private final int NOTE_REQUEST_CODE = 1;

    // Data sources
    private TraitDataSource traitDataSource;
    private NoteDataSource noteDataSource;
    private WineDataSource wineDataSource;
    private WineryDataSource wineryDataSource;

    // Note list
    private ArrayList<Note> notes;
    private ArrayAdapter<Note> notesAdapter;
    private ListView lvNotes;

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
        boolean handleNote = requestCode == this.NOTE_REQUEST_CODE && resultCode == RESULT_OK;
        if (handleNote) {
            this.refreshNoteList(false);
        }
    }

    /**
     * Opens activity to add new note.
     */
    public void onAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, this.NOTE_REQUEST_CODE);
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
        ArrayList<Note> notesFromAPI = this.noteDataSource.getAll(refreshFromAPI);

        // Add notes to note list.
        this.notes.clear();
        this.notes.addAll(notesFromAPI);
        this.notesAdapter.notifyDataSetChanged();
    }

}
