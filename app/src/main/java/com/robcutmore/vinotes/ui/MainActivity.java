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
import com.robcutmore.vinotes.dao.NoteTraitDataSource;
import com.robcutmore.vinotes.dao.TastingNoteDataSource;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.TastingNote;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private NoteTraitDataSource traitDataSource;
    private TastingNoteDataSource noteDataSource;
    private WineDataSource wineDataSource;
    private WineryDataSource wineryDataSource;
    private ArrayList<TastingNote> notes;
    private ArrayAdapter<TastingNote> notesAdapter;
    private ListView lvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize data sources.
        Context appContext = this.getApplicationContext();
        this.traitDataSource = new NoteTraitDataSource(appContext);
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);
        this.noteDataSource = new TastingNoteDataSource(appContext);

        // Connect list view to note array list.
        this.notes = new ArrayList<>();
        this.notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.notes);
        this.lvNotes = (ListView) findViewById(R.id.lvNotes);
        this.lvNotes.setAdapter(notesAdapter);

        // Display any existing tasting notes.
        this.populateNoteList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void onAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void populateNoteList() {
        // Refresh data from API.
        this.traitDataSource.getAll(true);
        this.wineryDataSource.getAll(true);
        this.wineDataSource.getAll(true);
        ArrayList<TastingNote> notesFromAPI = this.noteDataSource.getAll(true);

        // Add notes to note list.
        this.notes.clear();
        this.notes.addAll(notesFromAPI);
        this.notesAdapter.notifyDataSetChanged();
    }

}
