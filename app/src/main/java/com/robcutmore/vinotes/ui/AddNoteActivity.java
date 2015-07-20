package com.robcutmore.vinotes.ui;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.robcutmore.vinotes.R;


public class AddNoteActivity extends ActionBarActivity {

    private TextView etTastingDate;
    private TextView etWinery;
    private TextView etWine;
    private TextView etVintage;
    private RatingBar rbRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Get references to user input.
        this.etTastingDate = (TextView) findViewById(R.id.etTastingDate);
        this.etWinery = (TextView) findViewById(R.id.etWinery);
        this.etWine = (TextView) findViewById(R.id.etWine);
        this.etVintage = (TextView) findViewById(R.id.etVintage);
        this.rbRating = (RatingBar) findViewById(R.id.rbRating);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public void onSaveNote(View view) {
        // Validate user input.
        if (isInputValid()) {
            // Save new note.
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setTastingDate(final String newTastingDate) {
        this.etTastingDate.setText(newTastingDate);
    }

    private boolean isInputValid() {
        boolean isValid = true;

        boolean isWineryEmpty = this.etWinery.getText().toString().trim().length() == 0;
        if (isWineryEmpty) {
            this.etWinery.setError("Winery must be entered.");
            isValid = false;
        }
        boolean isWineEmpty = this.etWine.getText().toString().trim().length() == 0;
        if (isWineEmpty) {
            this.etWine.setError("Wine must be entered.");
            isValid = false;
        }
        boolean isVintageEmpty = this.etVintage.getText().toString().trim().length() == 0;
        if (isVintageEmpty) {
            this.etVintage.setError("Vintage must be entered.");
            isValid = false;
        }

        return isValid;
    }

}
