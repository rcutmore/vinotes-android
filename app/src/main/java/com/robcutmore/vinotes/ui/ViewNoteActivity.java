package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.model.Note;
import com.robcutmore.vinotes.model.Trait;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.utils.DateUtils;
import com.robcutmore.vinotes.utils.InputUtils;

import java.util.ArrayList;


/**
 * ViewNoteActivity is used to view and/or edit an existing tasting note.
 */
public class ViewNoteActivity extends ActionBarActivity {

    private Note note = null;

    // User input
    private EditText etTastingDate;
    private EditText etWinery;
    private EditText etWine;
    private EditText etColorTraits;
    private EditText etNoseTraits;
    private EditText etTasteTraits;
    private EditText etFinishTraits;

    /**
     * Sets up activity and private variables.
     * Refreshes data from API and displays note list.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        // Get references to user input.
        this.etTastingDate = (EditText) findViewById(R.id.etTastingDate);
        this.etWinery = (EditText) findViewById(R.id.etWinery);
        this.etWine = (EditText) findViewById(R.id.etWine);
        this.etColorTraits = (EditText) findViewById(R.id.etColorTraits);
        this.etNoseTraits = (EditText) findViewById(R.id.etNoseTraits);
        this.etTasteTraits = (EditText) findViewById(R.id.etTasteTraits);
        this.etFinishTraits = (EditText) findViewById(R.id.etFinishTraits);

        // Get selected tasting note.
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        if (args != null) {
            this.note = args.getParcelable("note");
        }
        this.displayNoteData();
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
     * Fills user input with note data.
     */
    private void displayNoteData() {
        if (this.note != null) {
            // Tasting date
            String tastingDate = DateUtils.convertDateToString(this.note.getTasted());
            this.etTastingDate.setText(tastingDate);

            // Winery and wine
            Wine wine = this.note.getWine();
            String wineryText = wine.getWinery().getName();
            this.etWinery.setText(wineryText);
            String wineText = String.format("%s (%d)", wine.getName(), wine.getVintage());
            this.etWine.setText(wineText);

            // Traits
            this.displayTraits(this.etColorTraits, this.note.getColorTraits());
            this.displayTraits(this.etNoseTraits, this.note.getNoseTraits());
            this.displayTraits(this.etTasteTraits, this.note.getTasteTraits());
            this.displayTraits(this.etFinishTraits, this.note.getFinishTraits());
        }
    }

    /**
     * Sets trait display text in given EditText.
     * If there are no traits then "None" is displayed.
     *
     * @param etTraits  EditText to display traits in
     * @param traits  traits to display
     */
    private void displayTraits(final EditText etTraits, final ArrayList<Trait> traits) {
        String traitText = "";
        for (Trait trait : traits) {
            traitText = String.format("%s, %s", traitText, trait.getName());
        }
        etTraits.setText(traitText);
        if (InputUtils.isEditTextEmpty(etTraits)) {
            etTraits.setText("None");
        }
    }

}
