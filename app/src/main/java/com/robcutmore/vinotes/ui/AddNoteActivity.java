package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.robcutmore.vinotes.R;


public class AddNoteActivity extends ActionBarActivity {

    private final int WINERY_REQUEST_CODE = 1;

    private EditText etTastingDate;
    private EditText etWinery;
    private EditText etWine;
    private RatingBar rbRating;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Get references to user input.
        this.etTastingDate = (EditText) findViewById(R.id.etTastingDate);
        this.etWinery = (EditText) findViewById(R.id.etWinery);
        this.etWine = (EditText) findViewById(R.id.etWine);
        this.rbRating = (RatingBar) findViewById(R.id.rbRating);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        // Test stub, to be replaced.
        this.etWine.setText("Test Wine");
    }

    public void showWineryPicker(final View view) {
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