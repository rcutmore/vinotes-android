package com.robcutmore.vinotes.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.model.TastingNote;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ArrayList<TastingNote> notes;
    private ArrayAdapter<TastingNote> notesAdapter;
    private ListView lvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect list view to note array list.
        notes = new ArrayList<>();
        notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        lvNotes = (ListView) findViewById(R.id.lvNotes);
        lvNotes.setAdapter(notesAdapter);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    private ArrayList<TastingNote> getNotes() {
        ArrayList<TastingNote> notes = new ArrayList<>();

        // Get list of existing tasting notes in JSON format.
        JSONArray jsonNotes;
        try {
            JSONObject json = new JSONObject(this.requestNotes());
            jsonNotes = json.getJSONArray("results");
        } catch (JSONException e) {
            jsonNotes = new JSONArray();
        }

        // Process each tasting note to store and display.
        for (int i = 0; i < jsonNotes.length(); i++) {
            try {
                JSONObject jsonNote = jsonNotes.getJSONObject(i);
            } catch (JSONException e) {
                // Invalid note so skip this one.
            }
        }

        Winery winery = new Winery("Hermann J. Wiemer");
        winery.setId(1);
        Wine firstWine = new Wine(winery, "Semi-Dry Reisling", 2015);
        firstWine.setId(1);
        TastingNote firstNote = new TastingNote(firstWine);
        firstNote.setId(1);
        firstNote.setRating(5);
        Wine secondWine = new Wine(winery, "Dry Reisling", 2014);
        secondWine.setId(2);
        TastingNote secondNote = new TastingNote(secondWine);
        secondNote.setId(2);
        secondNote.setRating(4);

        notes.add(firstNote);
        notes.add(secondNote);
        return notes;
    }

    private void populateNoteList() {
        notes.clear();
        notes.addAll(this.getNotes());
        notesAdapter.notifyDataSetChanged();
    }

    private String requestNotes() {
        // This is a stub for testing purposes.
        // To be replaced once API is live.
        return "{" +
                   "\"count\": 2," +
                   "\"next\": null," +
                   "\"previous\": null," +
                   "\"results\": [" +
                       "{" +
                           "\"pk\": 1," +
                           "\"url\": \"http://api.vinot.es/notes/1/\"," +
                           "\"taster\": \"test@vinot.es\"," +
                           "\"tasted\": null," +
                           "\"wine\": \"http://api.vinot.es/wines/1/\"," +
                           "\"color_traits\": []," +
                           "\"nose_traits\": []," +
                           "\"taste_traits\": []," +
                           "\"finish_traits\": []," +
                           "\"rating\": 5" +
                       "}," +
                       "{" +
                           "\"pk\": 2," +
                           "\"url\": \"http://api.vinot.es/notes/2/\"," +
                           "\"taster\": \"test@vinot.es\"," +
                           "\"tasted\": null," +
                           "\"wine\": \"http://api.vinot.es/wines/1/\"," +
                           "\"color_traits\": []," +
                           "\"nose_traits\": []," +
                           "\"taste_traits\": [" +
                               "\"http://api.vinot.es/traits/1/\"" +
                           "]," +
                           "\"finish_traits\": []," +
                           "\"rating\": 4" +
                       "}" +
                   "]" +
               "}";
    }

}