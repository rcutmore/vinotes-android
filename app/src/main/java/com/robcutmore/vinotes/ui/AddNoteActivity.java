package com.robcutmore.vinotes.ui;


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


public class AddNoteActivity extends ActionBarActivity {

    private final int WINERY_REQUEST_CODE = 1;
    private final int WINE_REQUEST_CODE = 2;

    private EditText etTastingDate;
    private EditText etWinery;
    private EditText etWine;
    private RatingBar rbRating;

    private WineryDataSource wineryDataSource;
    private Winery winery;
    private WineDataSource wineDataSource;
    private Wine wine;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Get references to user input.
        this.etTastingDate = (EditText) findViewById(R.id.etTastingDate);
        this.etWinery = (EditText) findViewById(R.id.etWinery);
        this.etWine = (EditText) findViewById(R.id.etWine);
        this.rbRating = (RatingBar) findViewById(R.id.rbRating);

        // Initialize objects for note.
        this.winery = null;
        this.wine = null;

        // Initialize data source objects.
        Context appContext = this.getApplicationContext();
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);
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

        if (this.isInputEmpty(this.etWinery)) {
            this.etWinery.setError("Winery must be selected.");
            isInvalid = true;
        }
        if (this.isInputEmpty(this.etWine)) {
            this.etWine.setError("Wine must be selected.");
            isInvalid = true;
        }

        return isInvalid;
    }

    private boolean isInputEmpty(final EditText userInput) {
        return userInput.getText().toString().trim().length() == 0;
    }

}
