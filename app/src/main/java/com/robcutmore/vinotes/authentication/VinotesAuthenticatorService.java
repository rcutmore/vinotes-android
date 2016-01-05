package com.robcutmore.vinotes.authentication;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for application's authenticator.
 */
public class VinotesAuthenticatorService extends Service {

    /**
     * Fetches communication channel to this service.
     *
     * @param intent  the Intent used to bind this service
     * @return IBinder for communicating to this service
     */
    @Override
    public IBinder onBind(final Intent intent) {
        // If action to perform is for authenticator then return object for binding to service.
        if (intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            VinotesAuthenticator authenticator = new VinotesAuthenticator(this);
            return authenticator.getIBinder();
        } else {
            return null;
        }
    }

}
