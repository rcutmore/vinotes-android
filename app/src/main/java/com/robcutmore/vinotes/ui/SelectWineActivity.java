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


public class SelectWineActivity extends ActionBarActivity {

    private EditText etWineSearch;
    private ListView lvWines;

    private WineDataSource wineDataSource;
    private ArrayList<Wine> wines;
    private ArrayAdapter<Wine> winesAdapter;

    // This will watch for any typing in etWineSearch and filter lvWines.
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

    // This will return the selected wine id to the calling activity.
    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Wine wine = (Wine) parent.getAdapter().getItem(position);
            Intent intent = new Intent(SelectWineActivity.this, AddNoteActivity.class);
            intent.putExtra("id", wine.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wine);

        // Get references to user input.
        this.etWineSearch = (EditText) findViewById(R.id.etWineSearch);
        this.etWineSearch.addTextChangedListener(searchWatcher);
        this.lvWines = (ListView) findViewById(R.id.lvWines);
        this.lvWines.setOnItemClickListener(clickListener);

        // Get ID of selected winery (sent from previous activity).
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long wineryId = (bundle != null) ? bundle.getLong("wineryId") : null;

        // Set up wine list.
        this.wineDataSource = new WineDataSource(this.getApplicationContext());
        this.wines = this.wineDataSource.getAllForWinery(wineryId);
        this.winesAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            this.wines
        );
        this.lvWines.setAdapter(this.winesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_wine, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.etWineSearch.removeTextChangedListener(this.searchWatcher);
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

}