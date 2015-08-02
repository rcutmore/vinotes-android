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

import java.sql.SQLException;
import java.util.ArrayList;


public class SelectWineryActivity extends ActionBarActivity {

    private EditText etWinerySearch;
    private ListView lvWineries;

    private WineryDataSource wineryDataSource;
    private ArrayList<Winery> wineries;
    private ArrayAdapter<Winery> wineriesAdapter;

    // This will watch for any typing in etWinerySearch and filter lvWineries.
    private final TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Filter list view.
            wineriesAdapter.getFilter().filter(s);
        }
    };

    // This will return the selected winery id to the calling activity.
    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Winery winery = (Winery) parent.getAdapter().getItem(position);
            Intent intent = new Intent(SelectWineryActivity.this, AddNoteActivity.class);
            intent.putExtra("id", winery.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_winery);

        this.initializeDataSources();

        this.etWinerySearch = (EditText) findViewById(R.id.etWinerySearch);
        this.etWinerySearch.addTextChangedListener(this.searchWatcher);
        this.lvWineries = (ListView) findViewById(R.id.lvWineries);
        this.lvWineries.setOnItemClickListener(this.clickListener);

        this.wineries = this.wineryDataSource.getAll(false);
        this.wineriesAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            this.wineries
        );
        this.lvWineries.setAdapter(this.wineriesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_winery, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etWinerySearch.removeTextChangedListener(this.searchWatcher);
        this.wineryDataSource.close();
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

    private void initializeDataSources() {
        this.wineryDataSource = new WineryDataSource(this.getApplicationContext());
        try {
            this.wineryDataSource.open();
        } catch (SQLException e) {
            // handle error
        }
    }

}
