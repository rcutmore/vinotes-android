package com.robcutmore.vinotes.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.fragments.AddWineFragment;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.ArrayList;


/**
 * Activity for selecting a wine stored in local database.
 */
public class SelectWineActivity extends ActionBarActivity
                                implements AddWineFragment.OnWineAddedListener {

    // User input
    private EditText etWineSearch;
    private ListView lvWines;

    // Wine components
    private Winery winery = null;
    private WineDataSource wineDataSource;
    private ArrayList<Wine> wines;
    private ArrayAdapter<Wine> winesAdapter;

    // Filters lvWines as user types in etWineSearch.
    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        /**
         * Filters wines list as user types.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Filter list view.
            winesAdapter.getFilter().filter(s);
        }
    };

    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Return clicked wine to calling activity.
            Wine wine = (Wine) parent.getAdapter().getItem(position);
            returnSelectedWine(wine);
        }
    };

    /**
     * Sets up activity and private variables.
     *
     * @param savedInstanceState  bundle containing any previously saved activity data
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wine);

        // Set up user input.
        this.etWineSearch = (EditText) findViewById(R.id.etWineSearch);
        this.etWineSearch.addTextChangedListener(this.searchWatcher);
        this.lvWines = (ListView) findViewById(R.id.lvWines);
        this.lvWines.setOnItemClickListener(this.clickListener);

        // Get selected winery from calling activity.
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        if (args != null) {
            this.winery = args.getParcelable("winery");
        }

        this.setupWineComponents();
    }

    /**
     * Sets up wine components when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.setupWineComponents();
    }

    /**
     * Removes listener for search box before destroying activity.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etWineSearch.removeTextChangedListener(this.searchWatcher);
    }

    /**
     * Sets up option menu.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_wine, menu);
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
     * Returns newly added wine.
     *
     * @param wine  new wine
     */
    @Override
    public void onWineAdded(final Wine wine) {
        this.returnSelectedWine(wine);
    }

    /**
     * Displays dialog to add a new wine.
     *
     * @param view  button that was clicked
     */
    public void showAddWineDialog(final View view) {
        // Send any search text that's been entered, along with selected winery ID.
        Bundle args = new Bundle();
        args.putString("searchText", this.etWineSearch.getText().toString());
        args.putParcelable("winery", this.winery);

        // Create and show fragment.
        AddWineFragment newFragment = new AddWineFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "wineAdder");
    }

    /**
     * Returns selected wine to calling activity.
     *
     * @param wine  selected wine
     */
    public void returnSelectedWine(final Wine wine) {
        Bundle args =  new Bundle();
        args.putParcelable("wine", wine);
        Intent intent = getIntent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sets up required wine components for activity if needed.
     */
    private void setupWineComponents() {
        // Set up data source.
        if (this.wineDataSource == null) {
            this.wineDataSource = new WineDataSource(this.getApplicationContext());
        }

        // Set up wine list.
        if (this.wines == null) {
            this.wines = this.wineDataSource.getAllForWinery(this.winery.getId());
        }

        // Set up wine adapter.
        if (this.winesAdapter == null) {
            this.winesAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    this.wines
            );
            this.lvWines.setAdapter(this.winesAdapter);
        }
    }

}
