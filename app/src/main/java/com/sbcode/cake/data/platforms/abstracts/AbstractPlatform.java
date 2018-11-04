package com.sbcode.cake.data.platforms.abstracts;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.interfaces.PlatformAccount;
import com.sbcode.cake.data.platforms.interfaces.PlatformRepository;

import timber.log.Timber;

public abstract class AbstractPlatform implements Platform {

    private static final String GOOGLE_PLAY_STORE_LINK_PREFIX =
            "https://play.google.com/store/apps/details?id=";

    private final PlatformAccount mAccount;
    private final PlatformRepository mRepository;
    private final String mNativePackageId;

    protected AbstractPlatform(PlatformAccount account, PlatformRepository repository,
                               String nativePackageId) {
        this.mAccount = account;
        this.mRepository = repository;
        this.mNativePackageId = nativePackageId;
    }

    @Override
    public boolean nativeAppInstalled() {
        try {
            ApplicationInfo applicationInfo = CakeApplication
                    .getInstance()
                    .getPackageManager()
                    .getApplicationInfo(mNativePackageId, 0);
            Timber.i("Application info = %s", applicationInfo.toString());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getNativeAppInstallUrl() {
        return GOOGLE_PLAY_STORE_LINK_PREFIX + mNativePackageId;
    }

    @Override
    public String getPlatformNativePackageId() {
        return mNativePackageId;
    }

    @Override
    public PlatformAccount getAccount() {
        return mAccount;
    }

    @Override
    public PlatformRepository getRepository() {
        return mRepository;
    }
}
