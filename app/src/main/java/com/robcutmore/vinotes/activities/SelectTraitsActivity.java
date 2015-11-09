package com.robcutmore.vinotes.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.fragments.AddTraitFragment;
import com.robcutmore.vinotes.model.Trait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * SelectTraitsActivity allows user to select traits for tasting note.
 * The type of trait is determined by calling activity (color, nose, taste, or finish).
 */
public class SelectTraitsActivity extends ActionBarActivity
                                  implements AddTraitFragment.OnTraitAddedListener {

    // User input
    private EditText etSearch;
    private ListView lvTraits;

    // Trait components
    private TraitDataSource traitDataSource;
    private ArrayList<Trait> traits;
    private ArrayAdapter<Trait> traitsAdapter;
    private ArrayList<Trait> selectedTraits = new ArrayList<>();

    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        /**
         * Filters traits list view as user types.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            lvTraits.clearChoices();
            traitsAdapter.getFilter().filter(s, new Filter.FilterListener() {
                /**
                 * Checks any selected traits currently viewable in list view.
                 *
                 * @param count  number of values computed by filter
                 */
                @Override
                public void onFilterComplete(int count) {
                    selectTraits(selectedTraits);
                }
            });
        }
    };

    private final Comparator<Trait> traitComparator = new Comparator<Trait>() {
        /**
         * Sorts traits alphabetically.
         *
         * @param t1 first trait
         * @param t2 second trait
         * @return result of name comparison
         */
        @Override
        public int compare(Trait t1, Trait t2) {
            return t1.getName().compareToIgnoreCase(t2.getName());
        }
    };

    /**
     * Sets up activity and private variables.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_traits);

        // Set activity title.
        Bundle args = getIntent().getExtras();
        String traitType = (args != null) ? args.getString("traitType", "") : "";
        this.setTraitTitle(traitType);

        // Get references to user input.
        this.etSearch = (EditText) findViewById(R.id.etSearch);
        this.etSearch.addTextChangedListener(this.searchWatcher);
        this.lvTraits = (ListView) findViewById(R.id.lvTraits);
        this.lvTraits.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        // Display traits and select any previously selected traits.
        this.setupTraitComponents();
        if (args != null) {
            ArrayList<Trait> previousSelection = args.getParcelableArrayList("traits");
            this.selectTraits(previousSelection);
            this.selectedTraits = previousSelection;
        }
    }

    /**
     * Removes listener for search box before destroying activity.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etSearch.removeTextChangedListener(this.searchWatcher);
    }

    /**
     * De-selects any selected traits in list view.
     *
     * @param view  button that was clicked
     */
    public void clearSelectedTraits(final View view) {
        this.lvTraits.clearChoices();
        this.lvTraits.requestLayout();
        this.selectedTraits.clear();
    }

    /**
     * Adds and selects new trait in traits list.
     *
     * @param trait  new trait to select
     */
    @Override
    public void onTraitAdded(final Trait trait) {
        // Add to trait list and sort by trait name.
        this.traits.add(trait);
        Collections.sort(this.traits, this.traitComparator);
        this.traitsAdapter.notifyDataSetChanged();

        // Select new trait.
        ArrayList<Trait> traitsToSelect = new ArrayList<>();
        traitsToSelect.add(trait);
        this.selectTraits(traitsToSelect);
        this.selectedTraits.add(trait);
    }

    /**
     * Returns selected traits to calling activity.
     *
     * @param view  button that was clicked
     */
    public void returnSelectedTraits(final View view) {
        Bundle args = new Bundle();
        Collections.sort(this.selectedTraits, this.traitComparator);
        args.putParcelableArrayList("traits", this.selectedTraits);
        Intent intent = getIntent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Displays dialog to add new trait.
     *
     * @param view  button that was clicked
     */
    public void showAddTraitDialog(final View view) {
        // Send any search text that's been entered.
        Bundle args = new Bundle();
        args.putString("searchText", this.etSearch.getText().toString());

        // Create and show fragment.
        AddTraitFragment newFragment = new AddTraitFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "traitAdder");
    }

    /**
     * Selects all given traits if they are found in trait list.
     *
     * @param traitsToSelect  list of traits to select
     */
    private void selectTraits(final ArrayList<Trait> traitsToSelect) {
        for (Trait traitToSelect : traitsToSelect) {
            int position = this.traitsAdapter.getPosition(traitToSelect);
            if (position > -1) {
                this.lvTraits.setItemChecked(position, true);
            }
        }
    }

    /**
     * Sets title of activity based on trait type specified by calling activity.
     *
     * @param traitType  type of trait to set title for
     */
    private void setTraitTitle(final String traitType) {
        // Set title based on type of trait.
        switch (traitType) {
            case "color":
                setTitle(R.string.title_traits_color_select);
                break;
            case "nose":
                setTitle(R.string.title_traits_nose_select);
                break;
            case "taste":
                setTitle(R.string.title_traits_taste_select);
                break;
            case "finish":
                setTitle(R.string.title_traits_finish_select);
                break;
            default:
                setTitle(R.string.title_traits_default_select);
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

        // Set up list view.
        this.lvTraits.setAdapter(this.traitsAdapter);
        this.lvTraits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Toggles selection for trait at given position.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateTraitSelection(position);
            }
        });
    }

    /**
     * Adds or removes clicked trait to/from selected traits.
     *
     * @param position  position of clicked trait in trait list view
     */
    private void updateTraitSelection(final int position) {
        Trait clickedTrait = (Trait) this.lvTraits.getItemAtPosition(position);
        boolean isChecked = this.lvTraits.isItemChecked(position);
        if (isChecked) {
            this.selectedTraits.add(clickedTrait);
        } else {
            this.selectedTraits.remove(clickedTrait);
        }
    }

}
