package com.robcutmore.vinotes.ui;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.Date;


public class AddNoteActivity extends ActionBarActivity {

    private final int WINERY_REQUEST_CODE = 1;
    private final int WINE_REQUEST_CODE = 2;

    private RetainedNoteFragment dataFragment;

    // Note data
    private Date tastingDate = null;
    private Winery winery = null;
    private Wine wine = null;
    private Integer rating = null;

    // User input
    private EditText etTastingDate;
    private EditText etWinery;
    private EditText etWine;
    private RatingBar rbRating;

    // Data sources
    private WineryDataSource wineryDataSource;
    private WineDataSource wineDataSource;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Get references to user input.
        this.etTastingDate = (EditText) findViewById(R.id.etTastingDate);
        this.etWinery = (EditText) findViewById(R.id.etWinery);
        this.etWine = (EditText) findViewById(R.id.etWine);
        this.rbRating = (RatingBar) findViewById(R.id.rbRating);

        // Initialize data source objects.
        Context appContext = this.getApplicationContext();
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);

        this.restoreActivityState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.storeActivityState();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        boolean handleWinery = requestCode == this.WINERY_REQUEST_CODE && resultCode == RESULT_OK;
        boolean handleWine = requestCode == this.WINE_REQUEST_CODE && resultCode == RESULT_OK;

        if (handleWinery) {
            // Look up and display selected winery.
            long wineryId = data.getLongExtra("id", 0);
            this.winery = (wineryId > 0) ? this.wineryDataSource.get(wineryId) : null;
            boolean wineryFound = this.winery != null;
            String wineryName = wineryFound ? this.winery.getName() : "";
            this.etWinery.setText(wineryName);

            // Reset wine selection.
            this.wine = null;
            this.etWine.setText("");
            this.etWine.setEnabled(wineryFound);

        } else if (handleWine) {
            // Look up and display selected wine.
            long wineId = data.getLongExtra("id", 0);
            this.wine = (wineId > 0) ? this.wineDataSource.get(wineId) : null;
            String wineName = (this.wine != null) ? this.wine.getName() : "";
            this.etWine.setText(wineName);
        }
    }

    public void saveNote(final View view) {
        // Validate user input.
        if (!this.isAnyInputInvalid()) {
            // Save new note.
        }
    }

    public void setTastingDate(final String newTastingDate) {
        this.etTastingDate.setText(newTastingDate);
    }

    public void showDatePickerDialog(final View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showWinePicker(final View view) {
        // Open activity to allow user to select wine.
        Intent intent = new Intent(this, SelectWineActivity.class);
        intent.putExtra("wineryId", this.winery.getId());
        startActivityForResult(intent, this.WINE_REQUEST_CODE);
    }

    public void showWineryPicker(final View view) {
        // Open activity to allow user to select winery.
        Intent intent = new Intent(this, SelectWineryActivity.class);
        startActivityForResult(intent, this.WINERY_REQUEST_CODE);
    }

    private boolean isAnyInputInvalid() {
        boolean isInvalid = false;

        if (this.winery == null || this.isInputEmpty(this.etWinery)) {
            this.etWinery.setError("Winery must be selected.");
            isInvalid = true;
        }
        if (this.wine == null || this.isInputEmpty(this.etWine)) {
            this.etWine.setError("Wine must be selected.");
            isInvalid = true;
        }

        return isInvalid;
    }

    private boolean isInputEmpty(final EditText userInput) {
        return userInput.getText().toString().trim().length() == 0;
    }

    private void restoreActivityState() {
        FragmentManager fm = getFragmentManager();
        this.dataFragment = (RetainedNoteFragment) fm.findFragmentByTag("noteData");

        // Create and store fragment to persist data if it hasn't been created yet.
        if (this.dataFragment == null) {
            this.dataFragment = new RetainedNoteFragment();
            fm.beginTransaction().add(dataFragment, "noteData").commit();
            this.storeActivityState();
        }

        // Restore previously saved data from fragment.
        this.setTastingDate(this.dataFragment.getTastingDate());
        this.setWinery(this.dataFragment.getWinery());
        this.setWine(this.dataFragment.getWine());
        this.setRating(this.dataFragment.getRating());
    }

    private void storeActivityState() {
        this.dataFragment.setTastingDate(this.tastingDate);
        this.dataFragment.setWinery(this.winery);
        this.dataFragment.setWine(this.wine);
        this.dataFragment.setRating(this.rating);
    }

    private void setTastingDate(Date tastingDate) {
        this.tastingDate = tastingDate;

        // Display selected tasting date.
        String dateText = (this.tastingDate != null) ? this.tastingDate.toString() : "";
        this.etTastingDate.setText(dateText);
    }

    private void setWinery(Winery winery) {
        this.winery = winery;

        // Display selected winery.
        String wineryText;
        if (this.winery != null) {
            wineryText = this.winery.getName();
            this.etWine.setEnabled(true);
        } else {
            wineryText = "";
            this.etWine.setEnabled(false);
        }
        this.etWinery.setText(wineryText);
    }

    private void setWine(Wine wine) {
        this.wine = wine;

        // Display selected wine.
        String wineText;
        if (this.wine != null) {
            wineText = String.format("%s %s", this.wine.getName(), this.wine.getVintage());
        } else {
            wineText = "";
            this.etWine.setEnabled(false);
        }
        this.etWine.setText(wineText);
    }

    private void setRating(Integer rating) {
        this.rating = rating;

        // Display selected rating.
        if (this.rating != null) {
            this.rbRating.setRating((float) this.rating);
        } else {
            this.rbRating.setRating(0f);
        }
    }

}
