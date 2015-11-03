package com.robcutmore.vinotes.utils;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.EditText;

import com.robcutmore.vinotes.R;


/**
 * InputUtils contains user input utility methods.
 */
public final class InputUtils {

    // EditText methods

    /**
     * Sets error for editText if empty.
     *
     * @param editText  EditText to check
     * @return true if editText contains input otherwise false
     */
    public static boolean checkEditText(EditText editText) {
        boolean hasInput = !isEditTextEmpty(editText);
        if (!hasInput) {
            editText.setError("Input must be entered");
        } else {
            editText.setError(null);
        }
        return hasInput;
    }

    /**
     * Determines whether or not editText is empty.
     *
     * @param editText  EditText to check
     * @return true if editText is empty otherwise false
     */
    public static boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    // DialogFragment methods

    /**
     * Gets height in pixels for DialogFragment based on current screen orientation.
     *
     * @param context  application context
     * @return height in pixels
     */
    public static int getDialogFragmentHeightPixels(final Context context) {
        Resources resources = context.getResources();

        // Get height percentage for current screen orientation.
        TypedValue value = new TypedValue();
        int screenOrientation = resources.getConfiguration().orientation;
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                resources.getValue(R.dimen.dialog_fragment_height_percent_landscape, value, true);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                resources.getValue(R.dimen.dialog_fragment_height_percent_portrait, value, true);
                break;
            default:
                resources.getValue(R.dimen.dialog_fragment_height_percent_default, value, true);
                break;
        }

        // Calculate height in pixels based on percentage.
        float heightPercent = value.getFloat();
        return Math.round(resources.getDisplayMetrics().heightPixels * heightPercent);

    }

    /**
     * Gets width in pixels for DialogFragment based on current screen orientation.
     *
     * @param context  application context
     * @return width in pixels
     */
    public static int getDialogFragmentWidthPixels(final Context context) {
        Resources resources = context.getResources();

        // Get width percentage for current screen orientation.
        TypedValue value = new TypedValue();
        int screenOrientation = resources.getConfiguration().orientation;
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                resources.getValue(R.dimen.dialog_fragment_width_percent_landscape, value, true);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                resources.getValue(R.dimen.dialog_fragment_width_percent_portrait, value, true);
                break;
            default:
                resources.getValue(R.dimen.dialog_fragment_width_percent_default, value, true);
                break;
        }

        // Calculate width in pixels based on percentage.
        float widthPercent = value.getFloat();
        return Math.round(resources.getDisplayMetrics().widthPixels * widthPercent);
    }

}
