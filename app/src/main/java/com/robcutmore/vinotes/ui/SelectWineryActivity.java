package com.robcutmore.vinotes.ui;


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
import com.robcutmore.vinotes.model.Winery;

import java.util.ArrayList;


/**
 * Activity for selecting a winery stored in local database.
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

    // Filters lvWineries as user types in etWinerySearch.
    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            wineriesAdapter.getFilter().filter(s);
        }
    };

    // Returns ID of selected winery to calling activity.
    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Winery winery = (Winery) parent.getAdapter().getItem(position);
            returnSelectedWinery(winery.getId());
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_winery);

        // Set up user input.
        this.etWinerySearch = (EditText) findViewById(R.id.etWinerySearch);
        this.etWinerySearch.addTextChangedListener(this.searchWatcher);
        this.lvWineries = (ListView) findViewById(R.id.lvWineries);
        this.lvWineries.setOnItemClickListener(this.clickListener);

        this.setupActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setupActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etWinerySearch.removeTextChangedListener(this.searchWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_winery, menu);
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
    public void onWineryAdded(final long wineryId) {
        this.returnSelectedWinery(wineryId);
    }

    /**
     * Displays dialog to add a new winery.
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
     * Returns selected winery ID to calling activity.
     * @param wineryId  ID of selected winery
     */
    private void returnSelectedWinery(final long wineryId) {
        Intent intent = new Intent(SelectWineryActivity.this, AddNoteActivity.class);
        intent.putExtra("id", wineryId);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sets up required winery components for activity if needed.
     */
    private void setupActivity() {
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
