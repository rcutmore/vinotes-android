package com.robcutmore.vinotes.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
                                implements DatePickerDialog.OnDateSetListener {

    public DatePickerFragment() {
        // Required empty public constructor.
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

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final String tastingDate = String.format("%d/%d/%d", month+1, day, year);
        AddNoteActivity callingActivity = (AddNoteActivity) getActivity();
        callingActivity.setTastingDate(tastingDate);
    }

}