package com.robcutmore.vinotes.authentication;

public final class AccountUtils {

    // Account ID
    public static final String ACCOUNT_TYPE = "com.robcutmore.vinotes.auth.account";

    // Auth token types
    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a Vinotes account";
    public static final String AUTH_TOKEN_TYPE_UNRECOGNIZED = "Unrecognized authentication token type: ";

    public static final ServerAuthenticator serverAuthenticator = new ServerAuthenticator();

}
