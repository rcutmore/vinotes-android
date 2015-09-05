package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.ListView;

import com.robcutmore.vinotes.R;


/**
 * SelectTraitsActivity allows user to select traits for tasting note.
 * The type of trait is determined by calling activity (color, nose, taste, or finish).
 */
public class SelectTraitsActivity extends ActionBarActivity {

    // User input
    private EditText etSearch;
    private ListView lvTraits;

    /**
     * Sets up activity and private variables.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_traits);
        this.setTraitTitle();

        // Get references to user input.
        this.etSearch = (EditText) findViewById(R.id.etSearch);
        this.lvTraits = (ListView) findViewById(R.id.lvTraits);
    }

    /**
     * Sets title of activity based on trait type specified by calling activity.
     */
    private void setTraitTitle() {
        // Determine type of trait.
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        String traitType = (args != null) ? args.getString("traitType", "") : "";

        // Set title based on type of trait.
        switch (traitType) {
            case "color":
                setTitle(R.string.title_activity_select_traits_color);
                break;
            case "nose":
                setTitle(R.string.title_activity_select_traits_nose);
                break;
            case "taste":
                setTitle(R.string.title_activity_select_traits_taste);
                break;
            case "finish":
                setTitle(R.string.title_activity_select_traits_finish);
                break;
            default:
                setTitle(R.string.title_activity_select_traits_default);
                break;
        }
    }

}
