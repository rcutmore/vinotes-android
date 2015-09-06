package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.model.Trait;

import java.util.ArrayList;


/**
 * SelectTraitsActivity allows user to select traits for tasting note.
 * The type of trait is determined by calling activity (color, nose, taste, or finish).
 */
public class SelectTraitsActivity extends ActionBarActivity {

    // User input
    private EditText etSearch;
    private ListView lvTraits;

    // Trait components
    private TraitDataSource traitDataSource;
    private ArrayList<Trait> traits;
    private ArrayAdapter<Trait> traitsAdapter;

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
        this.lvTraits.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        this.setupTraitComponents();
    }

    /**
     * Returns selected traits to calling activity.
     *
     * @param view  button that was clicked
     */
    public void returnSelectedTraits(final View view) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("traits", getSelectedTraits());
        Intent intent = getIntent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Fetches all selected traits.
     *
     * @return ArrayList contained all selected traits
     */
    private ArrayList<Trait> getSelectedTraits() {
        // Determine positions of checked traits.
        SparseBooleanArray checked = this.lvTraits.getCheckedItemPositions();

        // Collect and return all checked traits.
        ArrayList<Trait> selectedTraits = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                int position = checked.keyAt(i);
                selectedTraits.add(this.traitsAdapter.getItem(position));
            }
        }
        return selectedTraits;
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

    /**
     * Sets up trait components if needed.
     * Trait components include data source, list, and list adapter.
     */
    private void setupTraitComponents() {
        // Set up data source.
        if (this.traitDataSource == null) {
            this.traitDataSource = new TraitDataSource(this.getApplicationContext());
        }

        // Set up trait list.
        if (this.traits == null) {
            this.traits = this.traitDataSource.getAll(false);
        }

        // Set up trait adapter.
        if (this.traitsAdapter == null) {
            this.traitsAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    this.traits
            );
        }
        this.lvTraits.setAdapter(this.traitsAdapter);
    }

}
