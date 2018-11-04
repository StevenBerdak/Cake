package com.sbcode.cake.data.platforms.implementations.twitter;

import android.app.Activity;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatformAccount;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import javax.inject.Singleton;

/**
 * Functions used in managing a user's Twitter account.
 */
@Singleton
public class TwitterAccount extends AbstractPlatformAccount {

    /**
     * Sign in to service only if not already signed in.
     *
     * @param activity Activity context to be used if required.
     */
    @Override
    public void performSignIn(Activity activity) {
        if (isSignedIn()) {
            return;
        }

        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.authorize(activity,
                new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {

                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
        super.performSignIn(activity);
    }

    @Override
    public boolean isSignedIn() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        return session != null && session.getUserId() != 0;
    }

    /**
     * Sign out from service only if already signed in.
     *
     * @param activity Activity context to used if required.
     */
    @Override
    public void performSignOut(Activity activity) {
        if (!isSignedIn()) {
            return;
        }

        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        super.performSignOut(activity);
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.TWITTER;
    }
}
