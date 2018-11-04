package com.sbcode.cake.data.platforms.interfaces;

import android.app.Activity;

import com.sbcode.cake.data.PlatformType;

public interface PlatformAccount {

    /**
     * Sign in to service only if not already signed in.
     *
     * @param activity Activity context to be used if required.
     */
    void performSignIn(Activity activity);

    /**
     * Check if account is signed in.
     *
     * @return Signed in status of the account
     */
    boolean isSignedIn();

    /**
     * Sign out from service only if already signed in.
     *
     * @param activity Activity context to used if required.
     */
    void performSignOut(Activity activity);

    PlatformType getPlatform();
}
