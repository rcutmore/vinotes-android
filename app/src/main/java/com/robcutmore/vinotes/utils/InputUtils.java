package com.robcutmore.vinotes.utils;


import android.widget.EditText;


/**
 * InputUtils contains user input utility methods.
 */
public final class InputUtils {

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

}
