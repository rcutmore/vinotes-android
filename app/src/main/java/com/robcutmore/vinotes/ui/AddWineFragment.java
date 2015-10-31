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
import com.robcutmore.vinotes.model.Winery;
import com.robcutmore.vinotes.utils.InputUtils;


/**
 * AddWineFragment allows user to add a new wine and returns it to the calling activity.
 */
public class AddWineFragment extends DialogFragment {

    /**
     * Interface to be implemented by calling activity for returning newly added wine.
     */
    public interface OnWineAddedListener {
        void onWineAdded(Wine wine);
    }

    private OnWineAddedListener callbackListener;
    private Winery winery;
    private EditText etWineName;
    private EditText etVintage;
    private WineDataSource wineDataSource;

    /**
     * Constructor.
     * Empty constructor required for dialog fragment.
     */
    public AddWineFragment() {}

    /**
     * Sets up callback listener for returning added wine.
     *
     * @param activity  calling activity
     */
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

    /**
     * Sets up fragment and private variables.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                final Bundle savedInstanceState) {
        // Set layout.
        View view = inflater.inflate(R.layout.fragment_add_wine, container);

        // Get search text, if any, and selected winery from previous activity.
        Bundle args = getArguments();
        String searchText;
        if (args != null) {
            searchText = args.getString("searchText", "");
            this.winery = args.getParcelable("winery");
        } else {
            searchText = "";
        }

        // Initialize input and data source.
        this.etWineName = (EditText) view.findViewById(R.id.etWineName);
        this.etWineName.setText(searchText);
        this.etVintage = (EditText) view.findViewById(R.id.etVintage);
        this.wineDataSource = new WineDataSource(getActivity().getApplicationContext());

        // Add onClick handler for button.
        Button addWineButton = (Button) view.findViewById(R.id.btnAddWine);
        addWineButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Adds new wine.
             */
            @Override
            public void onClick(View v) {
                addWine();
            }
        });

        return view;
    }

    /**
     * Adds and returns new wine to calling activity.
     */
    private void addWine() {
        // Make sure input has been entered before adding wine.
        boolean hasWineInput = InputUtils.checkEditText(this.etWineName);
        boolean hasVintageInput = InputUtils.checkEditText(this.etVintage);
        if (hasWineInput && hasVintageInput) {
            Wine wineToAdd = new Wine(
                this.winery,
                this.etWineName.getText().toString(),
                Integer.parseInt(this.etVintage.getText().toString())
            );
            Wine newWine = this.wineDataSource.add(wineToAdd);
            this.callbackListener.onWineAdded(newWine);
            dismiss();
        }
    }

}
