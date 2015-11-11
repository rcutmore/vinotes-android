package com.robcutmore.vinotes.fragments;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.robcutmore.vinotes.R;
import com.robcutmore.vinotes.models.Winery;
import com.robcutmore.vinotes.tasks.AddWineryTask;
import com.robcutmore.vinotes.utils.InputUtils;


/**
 * Provides input to add a new winery and return it to calling activity.
 */
public class AddWineryFragment extends DialogFragment implements AddWineryTask.TaskListener {

    /**
     * Interface to be implemented by calling activity for returning newly added winery.
     */
    public interface OnWineryAddedListener {
        void onWineryAdded(Winery winery);
    }

    private OnWineryAddedListener callbackListener;
    private EditText etWineryName;

    /**
     * Constructor.
     * Empty constructor required for dialog fragment.
     */
    public AddWineryFragment() {}

    /**
     * Sets up callback listener for returning added winery.
     *
     * @param activity  calling activity
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        // Make sure callback is set up to return new winery.
        try {
            this.callbackListener = (OnWineryAddedListener) activity;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                "%s must implement OnWineryAddedListener.",
                activity.toString()
            );
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
        View view = inflater.inflate(R.layout.fragment_add_winery, container);

        // Get search text, if any, from previous activity.
        String searchText = (getArguments() != null) ? getArguments().getString("searchText", "") : "";

        // Initialize input and data source.
        this.etWineryName = (EditText) view.findViewById(R.id.etWineryName);
        this.etWineryName.setText(searchText);

        // Add onClick handler for button.
        Button addWineryButton = (Button) view.findViewById(R.id.btnAddWinery);
        addWineryButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Adds new winery.
             */
            @Override
            public void onClick(View v) {
                addWinery();
            }
        });

        return view;
    }

    /**
     * Sets size when activity is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Set size based on screen orientation.
        Context appContext = getActivity().getApplicationContext();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = InputUtils.getDialogFragmentHeightPixels(appContext);
        params.width = InputUtils.getDialogFragmentWidthPixels(appContext);
        getDialog().getWindow().setAttributes(params);
    }

    /**
     * Sends new winery to callback listener.
     *
     * @param winery  new winery
     */
    @Override
    public void onTaskFinished(Winery winery) {
        this.callbackListener.onWineryAdded(winery);
        dismiss();
    }

    /**
     * Adds new winery.
     */
    private void addWinery() {
        // Make sure input has been entered before adding winery.
        boolean hasNameInput = InputUtils.checkEditText(this.etWineryName);
        if (hasNameInput) {
            Winery wineryToAdd = new Winery(this.etWineryName.getText().toString());
            AddWineryTask task = new AddWineryTask(this.getActivity(), this);
            task.execute(wineryToAdd);
        }
    }

}
