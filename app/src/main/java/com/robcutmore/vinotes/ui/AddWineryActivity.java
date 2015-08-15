package com.robcutmore.vinotes.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Winery;


public class AddWineryActivity extends ActionBarActivity {

    private EditText etWineryName;
    private WineryDataSource wineryDataSource;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_winery);

        this.etWineryName = (EditText) findViewById(R.id.etWineryName);
        this.wineryDataSource = new WineryDataSource(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
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

    public void addWinery(final View view) {
        // Add new winery to API and local database.
        String wineryName = this.etWineryName.getText().toString();
        Winery winery = this.wineryDataSource.add(wineryName);

        // Return new winery's id.
        Intent intent = new Intent(AddWineryActivity.this, AddNoteActivity.class);
        intent.putExtra("id", winery.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

}
