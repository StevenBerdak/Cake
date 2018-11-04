package com.sbcode.cake.data.platforms.abstracts;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CallSuper;

import com.sbcode.cake.data.platforms.interfaces.PlatformAccount;

public abstract class AbstractPlatformAccount implements PlatformAccount {

    public static final String ACCOUNTS_BROADCAST_ACTION = "com.sbcode.cake.broadcast.accounts";
    public static final String KEY_PLATFORM = "platform";

    @Override
    @CallSuper
    public void performSignIn(Activity activity) {
        broadcastStatus(activity);
    }

    @Override
    @CallSuper
    public void performSignOut(Activity activity) {
        broadcastStatus(activity);
    }

    private void broadcastStatus(Activity activity) {
        //Send broadcast of account status change.
        Intent accountStatusIntent = new Intent(ACCOUNTS_BROADCAST_ACTION);
        accountStatusIntent.putExtra(KEY_PLATFORM, getPlatform().name());
        activity.sendBroadcast(accountStatusIntent);
    }
}
