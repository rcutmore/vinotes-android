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
import com.robcutmore.vinotes.models.Trait;
import com.robcutmore.vinotes.tasks.AddTraitTask;
import com.robcutmore.vinotes.utils.InputUtils;


/**
 * Provides input to add a new trait and return it to calling activity.
 */
public class AddTraitFragment extends DialogFragment implements AddTraitTask.TaskListener {

    /**
     * Interface to be implemented by calling activity for returning newly added trait.
     */
    public interface OnTraitAddedListener {
        void onTraitAdded(Trait trait);
    }

    private OnTraitAddedListener callbackListener;
    private EditText etName;

    /**
     * Constructor.
     * Empty constructor required for dialog fragment.
     */
    public AddTraitFragment() {}

    /**
     * Sets up callback listener for returning new trait.
     *
     * @param activity  calling activity
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        // Make sure callback is set up to return new trait.
        try {
            this.callbackListener = (OnTraitAddedListener) activity;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                "%s must implement OnTraitAddedListener.",
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
        View view = inflater.inflate(R.layout.fragment_add_trait, container);

        // Get search text, if any, from calling activity.
        Bundle args = getArguments();
        String searchText = (args != null) ? args.getString("searchText", "") : "";

        // Initialize input and data source.
        this.etName = (EditText) view.findViewById(R.id.etName);
        this.etName.setText(searchText);

        // Add onClick handler for button.
        Button addTraitButton = (Button) view.findViewById(R.id.btnAdd);
        addTraitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Adds new trait.
             */
            @Override
            public void onClick(View v) {
                addTrait();
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
     * Sends new trait to callback listener.
     *
     * @param trait  new trait
     */
    @Override
    public void onTaskFinished(Trait trait) {
        this.callbackListener.onTraitAdded(trait);
        dismiss();
    }

    /**
     * Adds new trait.
     */
    private void addTrait() {
        // Make sure input has been entered before adding trait.
        boolean hasNameInput = InputUtils.checkEditText(this.etName);
        if (hasNameInput) {
            Trait traitToAdd = new Trait(this.etName.getText().toString());
            AddTraitTask task = new AddTraitTask(this.getActivity(), this);
            task.execute(traitToAdd);
        }
    }

}
