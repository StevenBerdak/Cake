package com.sbcode.cake.data.platforms.implementations.facebook;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatformAccount;

import java.util.Collections;

import javax.inject.Singleton;

/**
 * Functions used in managing a user's Facebook account.
 */
@Singleton
public class FacebookAccount extends AbstractPlatformAccount {

    private static final String FACEBOOK_PERMISSIONS = "user_posts, user_photos";

    private final LoginManager mLoginManager;

    public FacebookAccount() {
        mLoginManager = LoginManager.getInstance();
    }

    @Override
    public void performSignIn(Activity activity) {
        if (isSignedIn()) {
            return;
        }

        mLoginManager.logInWithReadPermissions(activity,
                Collections.singletonList(FACEBOOK_PERMISSIONS));
        super.performSignIn(activity);
   }

    @Override
    public boolean isSignedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    @Override
    public void performSignOut(Activity activity) {
        if (!isSignedIn()) {
            return;
        }

        mLoginManager.logOut();
        super.performSignOut(activity);
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.FACEBOOK;
    }
}
