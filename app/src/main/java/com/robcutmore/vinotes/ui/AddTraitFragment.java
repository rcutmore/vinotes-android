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
import com.robcutmore.vinotes.dao.TraitDataSource;
import com.robcutmore.vinotes.model.Trait;
import com.robcutmore.vinotes.utils.InputUtils;


/**
 * AddTraitFragment allows user to add a new trait and returns it to the calling activity.
 */
public class AddTraitFragment extends DialogFragment {

    /**
     * Interface to be implemented by calling activity for returning newly added trait.
     */
    public interface OnTraitAddedListener {
        void onTraitAdded(Trait trait);
    }

    private OnTraitAddedListener callbackListener;
    private EditText etName;
    private TraitDataSource traitDataSource;

    /**
     * Constructor.
     * Empty constructor required for dialog fragment.
     */
    public AddTraitFragment() {}

    /**
     * Set up callback listener for returning added trait.
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
                    "%s must implement OnTraitAddedListener.", activity.toString());
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
        this.traitDataSource = new TraitDataSource(getActivity().getApplicationContext());

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
     * Adds and returns new trait to calling activity.
     */
    private void addTrait() {
        boolean hasNameInput = !InputUtils.isEditTextEmpty(this.etName);

        if (hasNameInput) {
            // Add new trait to API and local database.
            String traitName = this.etName.getText().toString();
            Trait newTrait = this.traitDataSource.add(traitName);

            // Return new trait.
            this.callbackListener.onTraitAdded(newTrait);
            dismiss();
        }
    }

}
