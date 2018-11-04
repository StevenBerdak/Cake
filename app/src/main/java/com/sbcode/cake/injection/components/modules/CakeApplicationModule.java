package com.sbcode.cake.injection.components.modules;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CakeApplicationModule {

    @Provides
    @Singleton
    CakeApplication provideApplicationContext() {
        return CakeApplication.getInstance();
    }

    @Provides
    @Singleton
    CakeDatabase provideCakeDatabase(CakeApplication cakeApplication) {
        return CakeApplication.provideCakeDatabase(cakeApplication);
    }

    @Provides
    @Singleton
    CakePlatformManager provideCakePlatformManager() {
        return new CakePlatformManager();
    }
}
