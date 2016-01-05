package com.robcutmore.vinotes.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.robcutmore.vinotes.R;

/**
 * Handles authenticating user.
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public final static String KEY_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String KEY_ACCOUNT_PASSWORD = "ACCOUNT_PASSWORD";
    public final static String KEY_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String KEY_AUTH_TYPE = "AUTH_TYPE";
    public final static String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String KEY_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private final int REQUEST_CODE_REGISTER = 1;

    private AccountManager accountManager;
    private String authTokenType;

    /**
     * Sets up activity and private variables.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        this.accountManager = AccountManager.get(getBaseContext());

        // Get auth token type.
        this.authTokenType = getIntent().getStringExtra(this.KEY_AUTH_TYPE);
        if (this.authTokenType == null) {
            this.authTokenType = AccountUtils.AUTH_TOKEN_TYPE_FULL_ACCESS;
        }

        // Show account email address.
        String accountEmail = getIntent().getStringExtra(this.KEY_ACCOUNT_NAME);
        if (accountEmail != null) {
            ((EditText) findViewById(R.id.accountEmail)).setText(accountEmail);
        }

        // Set click event handlers.
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            /**
             * Authenticates user when v is clicked.
             *
             * @param v  UI element that was clicked
             */
            @Override
            public void onClick(View v) {
                login();
            }
        });
        findViewById(R.id.newUser).setOnClickListener(new View.OnClickListener() {
            /**
             * Opens registration screen when v is clicked.
             *
             * @param v  UI element that was clicked
             */
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getBaseContext(), RegisterActivity.class);
                register.putExtras(getIntent().getExtras());
                startActivityForResult(register, REQUEST_CODE_REGISTER);
            }
        });
    }

    /**
     * Authenticates user if registration was successful.
     *
     * @param requestCode  request code from registration activity
     * @param resultCode  result code from registration activity
     * @param data  data from registration activity
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Finish authenticator only if registration was successful.
        boolean registerSuccess = requestCode == this.REQUEST_CODE_REGISTER && resultCode == RESULT_OK;
        if (registerSuccess) {
            finishLogin(data);
        }
    }

    /**
     * Authenticates user.
     */
    public void login() {
        // Attempt authentication.
        new AsyncTask<Void, Void, Intent>() {
            final String email = ((EditText) findViewById(R.id.accountEmail)).getText().toString();
            final String password = ((EditText) findViewById(R.id.accountPassword)).getText().toString();

            /**
             * Attempts authenticator.
             *
             * @return intent with authenticator results
             */
            @Override
            protected Intent doInBackground(final Void... params) {
                Bundle authResult = new Bundle();
                try {
                    // Authenticate on server.
                    final String authToken = AccountUtils.serverAuthenticator.login(
                            email, password, authTokenType);

                    // Store account information to return.
                    authResult.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                    authResult.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
                    authResult.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    authResult.putString(KEY_ACCOUNT_PASSWORD, password);
                } catch (Exception e) {
                    // Store error message to return.
                    authResult.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                Intent result = new Intent();
                result.putExtras(authResult);
                return result;
            }

            /**
             * Handles result of authenticator attempt.
             *
             * @param result  intent with authenticator results
             */
            @Override
            protected void onPostExecute(final Intent result) {
                if (result.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(
                            getBaseContext(),
                            result.getStringExtra(KEY_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    finishLogin(result);
                }
            }
        }.execute();
    }

    /**
     * Finishes login for both new and existing accounts.
     *
     * @param authResult  intent with authenticator results
     */
    private void finishLogin(final Intent authResult) {
        // Get account information.
        String email = authResult.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String password = authResult.getStringExtra(this.KEY_ACCOUNT_PASSWORD);
        Account account = new Account(email, authResult.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        // Create account or set password.
        if (getIntent().getBooleanExtra(this.KEY_IS_ADDING_NEW_ACCOUNT, false)) {
            String authToken = authResult.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            this.accountManager.addAccountExplicitly(account, password, null);
            this.accountManager.setAuthToken(account, this.authTokenType, authToken);
        } else {
            this.accountManager.setPassword(account, password);
        }

        setAccountAuthenticatorResult(authResult.getExtras());
        setResult(RESULT_OK, authResult);
        finish();
    }

}
