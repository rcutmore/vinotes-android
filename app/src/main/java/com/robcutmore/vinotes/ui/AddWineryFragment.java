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
import com.robcutmore.vinotes.dao.WineryDataSource;
import com.robcutmore.vinotes.model.Winery;


public class AddWineryFragment extends DialogFragment {

    public interface OnWineryAddedListener {
        void onWineryAdded(long wineryId);
    }

    private OnWineryAddedListener callbackListener;
    private EditText etWineryName;
    private WineryDataSource wineryDataSource;

    // Empty constructor required for DialogFragment.
    public AddWineryFragment() {}

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        // Make sure callback is set up to return new winery.
        try {
            this.callbackListener = (OnWineryAddedListener) activity;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                "%s must implement OnWineryPickedListener.", activity.toString());
            Log.w(activity.getClass().getName(), errorMessage);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Set layout.
        View view = inflater.inflate(R.layout.fragment_add_winery, container);

        // Get search text, if any, from previous activity.
        String searchText = (getArguments() != null) ? getArguments().getString("searchText", "") : "";

        // Initialize input and data source.
        this.etWineryName = (EditText) view.findViewById(R.id.etWineryName);
        this.etWineryName.setText(searchText);
        this.wineryDataSource = new WineryDataSource(getActivity().getApplicationContext());

        // Add onClick handler for button.
        Button addWineryButton = (Button) view.findViewById(R.id.btnAddWinery);
        addWineryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWinery();
            }
        });

        return view;
    }

    private void addWinery() {
        // Add new winery to API and local database.
        String wineryName = this.etWineryName.getText().toString();
        Winery winery = this.wineryDataSource.add(wineryName);

        // Return new winery's id.
        this.callbackListener.onWineryAdded(winery.getId());
    }

}
