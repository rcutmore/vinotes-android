package com.robcutmore.vinotes.ui;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.NoteDataSource;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Note;
import com.robcutmore.vinotes.model.Trait;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;
import com.robcutmore.vinotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;


/**
 * AddNoteActivity is used to create a new tasting note.
 * Provides input for various note data and saves note.
 */
public class AddNoteActivity extends ActionBarActivity
                             implements DatePickerFragment.OnDateSelectedListener {

    private RetainedNoteFragment dataFragment;

    // Request codes.
    private final int WINERY_REQUEST_CODE = 1;
    private final int WINE_REQUEST_CODE = 2;
    private final int COLOR_TRAIT_REQUEST_CODE = 3;
    private final int NOSE_TRAIT_REQUEST_CODE = 4;
    private final int TASTE_TRAIT_REQUEST_CODE = 5;
    private final int FINISH_TRAIT_REQUEST_CODE = 6;

    // Note data
    private Date tastingDate = null;
    private Winery winery = null;
    private Wine wine = null;
    private Integer rating = null;
    private ArrayList<Trait> colorTraits = null;
    private ArrayList<Trait> noseTraits = null;
    private ArrayList<Trait> tasteTraits = null;
    private ArrayList<Trait> finishTraits = null;

    // User input
    private EditText etTastingDate;
    private EditText etWinery;
    private EditText etWine;
    private EditText etColorTraits;
    private EditText etNoseTraits;
    private EditText etTasteTraits;
    private EditText etFinishTraits;
    private RatingBar rbRating;
    private Button btnSave;

    // Data sources
    private WineryDataSource wineryDataSource;
    private WineDataSource wineDataSource;
    private NoteDataSource noteDataSource;

    /**
     * Sets up activity and private variables.
     * Restores activity state if necessary.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Get references to user input.
        this.etTastingDate = (EditText) findViewById(R.id.etTastingDate);
        this.etWinery = (EditText) findViewById(R.id.etWinery);
        this.etWine = (EditText) findViewById(R.id.etWine);
        this.etColorTraits = (EditText) findViewById(R.id.etColorTraits);
        this.etNoseTraits = (EditText) findViewById(R.id.etNoseTraits);
        this.etTasteTraits = (EditText) findViewById(R.id.etTasteTraits);
        this.etFinishTraits = (EditText) findViewById(R.id.etFinishTraits);
        this.rbRating = (RatingBar) findViewById(R.id.rbRating);
        this.btnSave = (Button) findViewById(R.id.btnSave);

        // Set change listener for rating bar.
        this.rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setRating((int) rating);
            }
        });

        // Initialize data source objects.
        Context appContext = this.getApplicationContext();
        this.wineryDataSource = new WineryDataSource(appContext);
        this.wineDataSource = new WineDataSource(appContext);
        this.noteDataSource = new NoteDataSource(appContext);

        this.restoreActivityState();
    }

    /**
     * Saves activity state before destroying.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.storeActivityState();
    }

    /**
     * Sets up options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    /**
     * Sets up option items.
     */
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

    /**
     * Handles results from other activities.
     *
     * @param requestCode  code to distinguish requests
     * @param resultCode  code for result status
     * @param data  contains returned data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // Determine which activity is returning.
        boolean isOK = resultCode == RESULT_OK;
        boolean handleWinery = requestCode == this.WINERY_REQUEST_CODE && isOK;
        boolean handleWine = requestCode == this.WINE_REQUEST_CODE && isOK;
        boolean handleColorTraits = requestCode == this.COLOR_TRAIT_REQUEST_CODE && isOK;
        boolean handleNoseTraits = requestCode == this.NOSE_TRAIT_REQUEST_CODE && isOK;
        boolean handleTasteTraits = requestCode == this.TASTE_TRAIT_REQUEST_CODE && isOK;
        boolean handleFinishTraits = requestCode == this.FINISH_TRAIT_REQUEST_CODE && isOK;

        if (handleWinery) {
            // Set selected winery.
            Bundle args = data.getExtras();
            Winery winery = args.getParcelable("winery");
            this.setWinery(winery);

        } else if (handleWine) {
            // Look up and set selected wine.
            long wineId = data.getLongExtra("id", 0);
            Wine wine = (wineId > 0) ? this.wineDataSource.get(wineId) : null;
            this.setWine(wine);

        } else if (handleColorTraits || handleNoseTraits || handleTasteTraits || handleFinishTraits) {
            // Get list of selected traits.
            Bundle args = data.getExtras();
            ArrayList<Trait> traits = args.getParcelableArrayList("traits");

            // Determine trait type and set traits.
            String traitType;
            if (handleColorTraits) {
                traitType = "color";
            } else if (handleNoseTraits) {
                traitType = "nose";
            } else if (handleTasteTraits) {
                traitType = "taste";
            } else {
                traitType = "finish";
            }
            this.setTraits(traitType, traits);
        }
    }

    /**
     * Sets selected tasting date.
     *
     * @param tastingDate  selected tasting date
     */
    public void onDateSelected(final Date tastingDate) {
        this.setTastingDate(tastingDate);
    }

    /**
     * Saves new note.
     *
     * @param view  Button that was clicked
     */
    public void saveNote(final View view) {
        Note note = this.noteDataSource.add(this.wine.getId(), this.tastingDate, this.rating);
        if (note != null) {
            Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * Displays fragment for selecting tasting date.
     *
     * @param view  EditText that was clicked
     */
    public void showDatePickerDialog(final View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Displays fragment for selecting wine traits.
     *
     * @param view  EditText that was clicked
     */
    public void showTraitPicker(final View view) {
        // Determine which trait type to open activity for.
        String traitType = "";
        int requestCode = 0;
        if (view == this.etColorTraits) {
            traitType = "color";
            requestCode = this.COLOR_TRAIT_REQUEST_CODE;
        } else if (view == this.etNoseTraits) {
            traitType = "nose";
            requestCode = this.NOSE_TRAIT_REQUEST_CODE;
        } else if (view == this.etTasteTraits) {
            traitType = "taste";
            requestCode = this.TASTE_TRAIT_REQUEST_CODE;
        } else if (view == this.etFinishTraits) {
            traitType = "finish";
            requestCode = this.FINISH_TRAIT_REQUEST_CODE;
        }

        // Start activity.
        Intent intent = new Intent(this, SelectTraitsActivity.class);
        intent.putExtra("traitType", traitType);
        startActivityForResult(intent, requestCode);
    }

    /**
     * Starts activity to select wine for note.
     *
     * @param view  EditText that was clicked
     */
    public void showWinePicker(final View view) {
        Intent intent = new Intent(this, SelectWineActivity.class);
        intent.putExtra("wineryId", this.winery.getId());
        startActivityForResult(intent, this.WINE_REQUEST_CODE);
    }

    /**
     * Starts activity to select winery for note.
     *
     * @param view  EditText that was clicked
     */
    public void showWineryPicker(final View view) {
        Intent intent = new Intent(this, SelectWineryActivity.class);
        startActivityForResult(intent, this.WINERY_REQUEST_CODE);
    }

    /**
     * Checks to see if given input is empty.
     *
     * @param userInput  EditText to check
     * @return true if input is empty otherwise false
     */
    private boolean isInputEmpty(final EditText userInput) {
        return userInput.getText().toString().trim().length() == 0;
    }

    /**
     * Restores persisted note data.
     * Sets up fragment to persist data if it doesn't exist yet.
     */
    private void restoreActivityState() {
        FragmentManager fm = getFragmentManager();
        this.dataFragment = (RetainedNoteFragment) fm.findFragmentByTag("noteData");

        // Create and store fragment to persist data if it hasn't been created yet.
        if (this.dataFragment == null) {
            this.dataFragment = new RetainedNoteFragment();
            fm.beginTransaction().add(this.dataFragment, "noteData").commit();
            this.storeActivityState();
        }

        // Restore previously saved data from fragment.
        this.setTastingDate(this.dataFragment.getTastingDate());
        this.setWinery(this.dataFragment.getWinery());
        this.setWine(this.dataFragment.getWine());
        this.setRating(this.dataFragment.getRating());
    }

    /**
     * Saves note data to be persisted if activity needs to be recreated.
     */
    private void storeActivityState() {
        this.dataFragment.setTastingDate(this.tastingDate);
        this.dataFragment.setWinery(this.winery);
        this.dataFragment.setWine(this.wine);
        this.dataFragment.setRating(this.rating);
    }

    /**
     * Sets color traits for note being created and updates display.
     *
     * @param traits  the new color traits to set
     */
    private void setTraits(final String traitType, final ArrayList<Trait> traits) {
        // Get text for all selected traits.
        String traitDisplay = "";
        for (Trait trait : traits) {
            if (traitDisplay != "") {
                traitDisplay = String.format("%s, %s", traitDisplay, trait.getName());
            } else {
                traitDisplay = trait.getName();
            }
        }

        // Set and display traits.
        switch (traitType) {
            case "color":
                this.colorTraits = traits;
                this.etColorTraits.setText(traitDisplay);
                break;
            case "nose":
                this.noseTraits = traits;
                this.etNoseTraits.setText(traitDisplay);
                break;
            case "taste":
                this.tasteTraits = traits;
                this.etTasteTraits.setText(traitDisplay);
                break;
            case "finish":
                this.finishTraits = traits;
                this.etFinishTraits.setText(traitDisplay);
                break;
        }
    }

    /**
     * Sets rating for note being created and updates display.
     *
     * @param rating  the new rating to set
     */
    private void setRating(Integer rating) {
        this.rating = rating;

        // Display selected rating.
        if (this.rating != null) {
            this.rbRating.setRating((float) this.rating);
        } else {
            this.rbRating.setRating(0f);
        }

        this.validateInput();
    }

    /**
     * Sets tasting date for note being created and updates display.
     *
     * @param tastingDate  the new date to set
     */
    private void setTastingDate(Date tastingDate) {
        this.tastingDate = tastingDate;

        // Display selected tasting date.
        String dateText = DateUtils.convertDateToString(tastingDate);
        this.etTastingDate.setText(dateText);

        this.validateInput();
    }

    /**
     * Sets selected winery for note being created and updates display.
     *
     * @param winery  the new winery to set
     */
    private void setWinery(Winery winery) {
        // Only process change if given winery is different than currently selected winery.
        // Otherwise wine input will be reset.
        boolean isNewWinery;
        if (this.winery != null && winery != null) {
            isNewWinery = this.winery.getId() != winery.getId();
        } else {
            isNewWinery = this.winery != winery;
        }
        if (isNewWinery) {
            // Set winery and reset selected wine.
            this.winery = winery;
            this.setWine(null);

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

        this.validateInput();
    }

    /**
     * Sets selected wine for note being created and updates display.
     *
     * @param wine  the new wine to set
     */
    private void setWine(Wine wine) {
        this.wine = wine;

        // Display selected wine.
        String wineText;
        if (this.wine != null) {
            wineText = String.format("%s %s", this.wine.getName(), this.wine.getVintage());
        } else {
            wineText = "";
        }
        this.etWine.setText(wineText);

        this.validateInput();
    }

    /**
     * Validates user input.
     * Enables button to save note if all input has been entered.
     */
    private void validateInput() {
        boolean isValid = true;

        // Check tasting date.
        if (this.tastingDate == null || this.isInputEmpty(this.etTastingDate)) {
            isValid = false;
        }

        // Check winery.
        if (this.winery == null || this.isInputEmpty(this.etWinery)) {
            isValid = false;
        }

        // Check wine.
        if (this.wine == null || this.isInputEmpty(this.etWine)) {
            isValid = false;
        }

        // Check rating.
        if (this.rating == null || this.rating == 0) {
            isValid = false;
        }

        // Enable or disable save button based on user input.
        this.btnSave.setEnabled(isValid);
    }

}
