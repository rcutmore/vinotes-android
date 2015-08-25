package com.robcutmore.vinotes.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.robcutmore.vinotes.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;


/**
 * Displays DatePicker and returns selected date to calling activity.
 */
public class DatePickerFragment extends DialogFragment
                                implements DatePickerDialog.OnDateSetListener {

    /**
     * Callback interface through which the fragment will return the selected date.
     */
    public interface OnDateSelectedListener {
        void onDateSelected(Date tastingDate);
    }

    private OnDateSelectedListener callbackListener;

    // Empty constructor required for DialogFragment.
    public DatePickerFragment() {}

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        // Make sure callback is set up to return date.
        try {
            this.callbackListener = (OnDateSelectedListener) activity;
        } catch (ClassCastException e) {
            String errorMessage = String.format(
                "%s must implement OnDateSelectedListener.", activity.toString());
            Log.w(activity.getClass().getName(), errorMessage);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Sets tasting date in calling activity once selected in DatePicker.
     */
    public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
        // Get selected date.
        String dateString = String.format("%d/%d/%d", year, month+1, day);
        Date date = DateUtils.parseDateFromString(dateString);

        // Return date to calling activity.
        this.callbackListener.onDateSelected(date);
    }

}