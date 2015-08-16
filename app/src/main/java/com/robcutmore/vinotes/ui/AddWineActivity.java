package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.model.Wine;


public class AddWineActivity extends ActionBarActivity {

    private Long wineryId;
    private EditText etWineName;
    private EditText etVintage;
    private WineDataSource wineDataSource;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wine);

        // Get ID of selected winery.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.wineryId = (bundle != null) ? bundle.getLong("wineryId") : null;

        // Get search text from previous activity, if any.
        String searchText = (bundle != null) ? bundle.getString("searchText", "") : "";

        this.etWineName = (EditText) findViewById(R.id.etWineName);
        this.etWineName.setText(searchText);
        this.etVintage = (EditText) findViewById(R.id.etVintage);
        this.wineDataSource = new WineDataSource(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_wine, menu);
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

    public void addWine(final View view) {
        // Add new wine to API and local database.
        String wineName = this.etWineName.getText().toString();
        int vintage = Integer.parseInt(this.etVintage.getText().toString());
        Wine wine = this.wineDataSource.add(this.wineryId, wineName, vintage);

        // Return new wine's id.
        Intent intent = new Intent(AddWineActivity.this, SelectWineActivity.class);
        intent.putExtra("id", wine.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

}
