package com.robcutmore.vinotes.ui;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.dao.WineDataSource;
import com.robcutmore.vinotes.model.Wine;


public class AddWineFragment extends DialogFragment {

    public interface OnWineAddedListener {
        void onWineAdded(long wineId);
    }

    private OnWineAddedListener callbackListener;
    private Long wineryId;
    private EditText etWineName;
    private EditText etVintage;
    private WineDataSource wineDataSource;

    // Empty constructor required for DialogFragment.
    public AddWineFragment() {}

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        // Make sure callback is set up to return new winery.
        try {
            this.callbackListener = (OnWineAddedListener) activity;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                    "%s must implement OnWineAddedListener.", activity.toString());
            Log.w(activity.getClass().getName(), errorMessage);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                final Bundle savedInstanceState) {
        // Set layout.
        View view = inflater.inflate(R.layout.fragment_add_wine, container);

        // Get search text, if any, and ID of selected winery from previous activity.
        Bundle args = getArguments();
        String searchText;
        if (getArguments() != null) {
            searchText = getArguments().getString("searchText", "");
            this.wineryId = args.getLong("wineryId");
        } else {
            searchText = "";
            this.wineryId = null;
        }

        // Initialize input and data source.
        this.etWineName = (EditText) view.findViewById(R.id.etWineName);
        this.etWineName.setText(searchText);
        this.etVintage = (EditText) view.findViewById(R.id.etVintage);
        this.wineDataSource = new WineDataSource(getActivity().getApplicationContext());

        // Add onClick handler for button.
        Button addWineButton = (Button) view.findViewById(R.id.btnAddWine);
        addWineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWine();
            }
        });

        return view;
    }

    public void addWine() {
        // Add new wine to API and local database.
        String wineName = this.etWineName.getText().toString();
        int vintage = Integer.parseInt(this.etVintage.getText().toString());
        Wine wine = this.wineDataSource.add(this.wineryId, wineName, vintage);

        // Return new wine's id.
        this.callbackListener.onWineAdded(wine.getId());
    }

}
