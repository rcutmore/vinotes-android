package com.robcutmore.vinotes.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class VinotesAuthenticator extends AbstractAccountAuthenticator {

    private final Context context;

    /**
     * Constructor.
     */
    public VinotesAuthenticator(final Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Adds new account.
     *
     * @param response  response to send to AccountManager
     * @param accountType  type of account to add
     * @param authTokenType  type of authenticator token for account
     * @param requiredFeatures  authenticator-specific features account must support
     * @param options  authenticator-specific options
     * @return bundle containing result
     * @throws NetworkErrorException if network error prevents authenticator from honoring request
     */
    @Override
    public Bundle addAccount(
            final AccountAuthenticatorResponse response,
            final String accountType,
            final String authTokenType,
            final String[] requiredFeatures,
            final Bundle options
    ) throws NetworkErrorException {

        // Create and return intent for adding account.
        Intent login = new Intent(this.context, LoginActivity.class);
        login.putExtra(LoginActivity.KEY_ACCOUNT_TYPE, accountType);
        login.putExtra(LoginActivity.KEY_AUTH_TYPE, authTokenType);
        login.putExtra(LoginActivity.KEY_IS_ADDING_NEW_ACCOUNT, true);
        login.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, login);
        return bundle;
    }

    /**
     * Fetches authenticator token for account.
     *
     * @param response  response to send to AccountManager
     * @param account  account to fetch authenticator token for
     * @param authTokenType  type of authenticator token for account
     * @param options  authenticator-specific options
     * @return bundle containing result
     * @throws NetworkErrorException if network error prevents authenticator from honoring request
     */
    @Override
    public Bundle getAuthToken(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String authTokenType,
            final Bundle options
    ) throws NetworkErrorException {

        // Return error if auth token type is invalid.
        if (!authTokenType.equals(AccountUtils.AUTH_TOKEN_TYPE_FULL_ACCESS)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid auth token type");
            return result;
        }

        // Get account information from the account manager.
        AccountManager accountManager = AccountManager.get(this.context);
        String authToken = accountManager.peekAuthToken(account, authTokenType);

        // Authenticate user with existing password if auth token not found.
        if (TextUtils.isEmpty(authToken)) {
            final String password = accountManager.getPassword(account);
            if (password != null) {
                try {
                    authToken = AccountUtils.serverAuthenticator.login(account.name, password, authTokenType);
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), "Error attempting server authenticator", e);
                }
            }
        }

        // Return auth token if valid otherwise re-prompt user to authenticate.
        if (!TextUtils.isEmpty(authToken)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        } else {
            Intent login = new Intent(this.context, LoginActivity.class);
            login.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            login.putExtra(LoginActivity.KEY_ACCOUNT_TYPE, account.type);
            login.putExtra(LoginActivity.KEY_AUTH_TYPE, authTokenType);
            login.putExtra(LoginActivity.KEY_ACCOUNT_NAME, account.name);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, login);
            return bundle;
        }
    }

    /**
     * @param authTokenType  type of auth token
     * @return label for given auth token type
     */
    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        switch(authTokenType) {
            case AccountUtils.AUTH_TOKEN_TYPE_FULL_ACCESS:
                return AccountUtils.AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL;
            default:
                return String.format("%s %s", AccountUtils.AUTH_TOKEN_TYPE_UNRECOGNIZED, authTokenType);
        }
    }

    /**
     * Checks if authenticator supports all given features.
     * Not needed for this app, always returns false in bundle.
     *
     * @param response  response to send to AccountManager
     * @param account  account to check
     * @param features  features to check
     * @return bundle containing result
     * @throws NetworkErrorException if network error prevents authenticator from honoring request
     */
    @Override
    public Bundle hasFeatures(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String[] features
    ) throws NetworkErrorException {

        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    /**
     * Returns bundle with intent of activity for editing the properties.
     * Not needed for this app, always returns null.
     *
     * @param response  response to use for setting result
     * @param accountType  account type to edit properties for
     * @return bundle containing result
     */
    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
        return null;
    }

    /**
     * Checks that user knows credentials of given account.
     * Not needed for this app, always returns null.
     *
     * @param response  response to send result back to AccountManager
     * @param account  account to check credentials for
     * @param options  bundle of authenticator-specific options
     * @return bundle containing result
     * @throws NetworkErrorException if network error prevents authenticator from honoring request
     */
    @Override
    public Bundle confirmCredentials(
            final AccountAuthenticatorResponse response,
            final Account account,
            final Bundle options
    ) throws NetworkErrorException {
        return null;
    }

    /**
     * Updates locally stored credentials for given accounts.
     * Not needed for this app, always returns null.
     *
     * @param response  response to send result back to AccountManager
     * @param account  account to update credentials for
     * @param authTokenType  type of authenticator token to retrieve after update
     * @param options  bundle containing authenticator-specific options
     * @return bundle containing result
     * @throws NetworkErrorException if network error prevents authenticator from honoring request
     */
    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response,
            final Account account,
            final String authTokenType,
            final Bundle options
    ) throws NetworkErrorException {
        return null;
    }

}
