package com.robcutmore.vinotes.authentication;



public class ServerAuthenticator {

    /**
     * Registers new user.
     *
     * @param email  email address for new user
     * @param password  password for new user
     * @return authenticator token
     */
    public String register(
            final String email,
            final String password,
            final String authTokenType
    ) throws Exception {

        return "testregistertoken";
    }

    /**
     * Authenticates user.
     *
     * @param email  email address of existing user
     * @param password  password of existing user
     * @return authenticator token
     */
    public String login(
            final String email,
            final String password,
            final String authTokenType
    ) throws Exception {

        return "testlogintoken";
    }

}
