package com.robcutmore.vinotes.utils;


import android.widget.EditText;


/**
 * InputUtils contains user input utility methods.
 */
public final class InputUtils {

    /**
     * Returns whether or not edit text is empty.
     * Sets error in edit text if empty.
     *
     * @param editText  edit text to check
     * @return true if edit text is empty otherwise false
     */
    public static boolean isEditTextEmpty(EditText editText) {
        boolean isEmpty = editText.getText().toString().trim().length() == 0;
        if (isEmpty) {
            editText.setError("Input must be entered");
        } else {
            editText.setError(null);
        }
        return isEmpty;
    }

}
