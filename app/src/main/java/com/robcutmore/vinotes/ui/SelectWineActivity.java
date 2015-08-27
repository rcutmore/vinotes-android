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
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.model.Wine;

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
    private Long wineryId;
    private WineDataSource wineDataSource;
    private ArrayList<Wine> wines;
    private ArrayAdapter<Wine> winesAdapter;

    // Filters lvWines as user types in etWineSearch.
    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Filter list view.
            winesAdapter.getFilter().filter(s);
        }
    };

    // Returns ID of selected wine to calling activity.
    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Wine wine = (Wine) parent.getAdapter().getItem(position);
            returnSelectedWine(wine.getId());
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wine);

        // Set up user input.
        this.etWineSearch = (EditText) findViewById(R.id.etWineSearch);
        this.etWineSearch.addTextChangedListener(this.searchWatcher);
        this.lvWines = (ListView) findViewById(R.id.lvWines);
        this.lvWines.setOnItemClickListener(this.clickListener);

        // Get ID of selected winery (sent from previous activity).
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        this.wineryId = (args != null) ? args.getLong("wineryId") : null;

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
        this.etWineSearch.removeTextChangedListener(this.searchWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_wine, menu);
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
    public void onWineAdded(final long wineId) {
        this.returnSelectedWine(wineId);
    }

    /**
     * Displays dialog to add new wine.
     */
    public void showAddWineDialog(final View view) {
        // Send any search text that's been entered, along with selected winery ID.
        Bundle args = new Bundle();
        args.putString("searchText", this.etWineSearch.getText().toString());
        args.putLong("wineryId", this.wineryId);

        // Create and show fragment.
        AddWineFragment newFragment = new AddWineFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "wineAdder");
    }

    /**
     * Returns selected wine ID to calling activity.
     * @param wineId  ID of selected wine
     */
    public void returnSelectedWine(final long wineId) {
        Intent intent = new Intent(SelectWineActivity.this, AddNoteActivity.class);
        intent.putExtra("id", wineId);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sets up required wine components for activity if needed.
     */
    private void setupActivity() {
        // Set up data source.
        if (this.wineDataSource == null) {
            this.wineDataSource = new WineDataSource(this.getApplicationContext());
        }

        // Set up wine list.
        if (this.wines == null) {
            this.wines = this.wineDataSource.getAllForWinery(this.wineryId);
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
