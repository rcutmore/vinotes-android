package com.robcutmore.vinotes.authentication;


import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.robcutmore.vinotes.R;


/**
 * Handles registering new user.
 * When finished control is sent back to activity for authenticating user.
 */
public class RegisterActivity extends Activity {

    private EditText uiEmail;
    private EditText uiPassword;
    private EditText uiPasswordRepeat;

    private String accountType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        this.accountType = getIntent().getStringExtra(LoginActivity.KEY_ACCOUNT_TYPE);

        this.uiEmail = (EditText) findViewById(R.id.accountEmail);
        this.uiPassword = (EditText) findViewById(R.id.accountPassword);
        this.uiPasswordRepeat = (EditText) findViewById(R.id.accountPasswordRepeat);

        // Set click event handlers.
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            /**
             * Registers user when v is clicked.
             *
             * @param v  UI element that was clicked
             */
            @Override
            public void onClick(View v) {
                register();
            }
        });
        findViewById(R.id.existingUser).setOnClickListener(new View.OnClickListener() {
            /**
             * Sends user back to login screen when v is clicked.
             *
             * @param v  UI element that was clicked
             */
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * Registers new user.
     */
    private void register() {
        final String accountType = this.accountType;

        AsyncTask registerTask = new AsyncTask<Void, Void, Intent>() {
            final String email = uiEmail.getText().toString();
            final String password = uiPassword.getText().toString();

            /**
             * Attempts registration.
             *
             * @return intent with registration results
             */
            @Override
            protected Intent doInBackground(final Void... params) {
                Bundle registrationResult = new Bundle();
                try {
                    // Register new account on server.
                    final String authToken = AccountUtils.serverAuthenticator.register(
                            email, password, AccountUtils.AUTH_TOKEN_TYPE_FULL_ACCESS);

                    // Store account information to return.
                    registrationResult.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                    registrationResult.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    registrationResult.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    registrationResult.putString(LoginActivity.KEY_ACCOUNT_PASSWORD, password);
                } catch (Exception e) {
                    // Store error message to return.
                    registrationResult.putString(LoginActivity.KEY_ERROR_MESSAGE, e.getMessage());
                }

                Intent result = new Intent();
                result.putExtras(registrationResult);
                return result;
            }

            /**
             * Handles result of registration attempt.
             *
             * @param result  intent with registration results
             */
            @Override
            protected void onPostExecute(final Intent result) {
                if (result.hasExtra(LoginActivity.KEY_ERROR_MESSAGE)) {
                    String errorMessage = result.getStringExtra(LoginActivity.KEY_ERROR_MESSAGE);
                    Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        };

        if (this.passwordsMatch()) {
            registerTask.execute();
        }
    }

    /**
     * Checks to see if password inputs match.
     * If password inputs do not match an error is displayed.
     *
     * @return whether or not password inputs match
     */
    private boolean passwordsMatch() {
        final String password = this.uiPassword.getText().toString();
        final String passwordRepeat = this.uiPasswordRepeat.getText().toString();
        if (passwordRepeat.equals(password)) {
            return true;
        } else {
            this.uiPasswordRepeat.setError(getString(R.string.registration_error_password_mismatch));
            return false;
        }
    }

}
