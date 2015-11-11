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
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.fragments.AddWineryFragment;
import com.robcutmore.vinotes.models.Winery;

import java.util.ArrayList;


/**
 * SelectWineryActivity allows user to select an existing winery or add and select a new winery.
 */
public class SelectWineryActivity extends ActionBarActivity
                                  implements AddWineryFragment.OnWineryAddedListener {

    // User input
    private EditText etWinerySearch;
    private ListView lvWineries;

    // Winery components
    private WineryDataSource wineryDataSource;
    private ArrayList<Winery> wineries;
    private ArrayAdapter<Winery> wineriesAdapter;

    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        /**
         * Filters wineries list view as user types.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            wineriesAdapter.getFilter().filter(s);
        }
    };

    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Return clicked winery to calling activity.
            Winery winery = (Winery) parent.getAdapter().getItem(position);
            returnSelectedWinery(winery);
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
        setContentView(R.layout.activity_select_winery);

        // Set up user input.
        this.etWinerySearch = (EditText) findViewById(R.id.etWinerySearch);
        this.etWinerySearch.addTextChangedListener(this.searchWatcher);
        this.lvWineries = (ListView) findViewById(R.id.lvWineries);
        this.lvWineries.setOnItemClickListener(this.clickListener);

        this.setupWineryComponents();
    }

    /**
     * Sets up winery components when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.setupWineryComponents();
    }

    /**
     * Removes listener for search box before destroying activity.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etWinerySearch.removeTextChangedListener(this.searchWatcher);
    }

    /**
     * Sets up option menu.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_winery, menu);
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
     * Returns newly added winery.
     *
     * @param winery  new winery
     */
    @Override
    public void onWineryAdded(final Winery winery) {
        this.returnSelectedWinery(winery);
    }

    /**
     * Displays dialog to add a new winery.
     *
     * @param view  button that was clicked
     */
    public void showAddWineryDialog(final View view) {
        // Send any search text that's been entered.
        Bundle args = new Bundle();
        args.putString("searchText", this.etWinerySearch.getText().toString());

        // Create and show fragment.
        AddWineryFragment newFragment = new AddWineryFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "wineryAdder");
    }

    /**
     * Returns selected winery to calling activity.
     *
     * @param winery  selected winery
     */
    private void returnSelectedWinery(final Winery winery) {
        Bundle args = new Bundle();
        args.putParcelable("winery", winery);
        Intent intent = getIntent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sets up required winery components for activity if needed.
     */
    private void setupWineryComponents() {
        // Set up data source.
        if (this.wineryDataSource == null) {
            this.wineryDataSource = new WineryDataSource(this.getApplicationContext());
        }

        // Set up winery list.
        if (this.wineries == null) {
            this.wineries = this.wineryDataSource.getAll(false);
        }

        // Set up winery adapter.
        if (this.wineriesAdapter == null) {
            this.wineriesAdapter = new ArrayAdapter<Winery>(
                    this,
                    android.R.layout.simple_list_item_1,
                    this.wineries
            );
        }
        this.lvWineries.setAdapter(this.wineriesAdapter);
    }

}
