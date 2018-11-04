package com.sbcode.cake.injection.components.modules;

import com.sbcode.cake.data.platforms.manager.PlatformManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlatformManagerModule {

    private final PlatformManager mPlatformManager;

    public PlatformManagerModule(PlatformManager platformManager) {
        this.mPlatformManager = platformManager;
    }

    @Provides
    @Singleton
    PlatformManager providePlatformManager() {
        return mPlatformManager;
    }
}
