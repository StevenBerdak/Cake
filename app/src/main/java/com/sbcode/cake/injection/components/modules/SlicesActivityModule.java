package com.sbcode.cake.injection.components.modules;

import com.sbcode.cake.ui.slices.SlicesActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SlicesActivityModule {

    private final SlicesActivity mSlicesActivity;

    public SlicesActivityModule(SlicesActivity slicesActivity) {
        this.mSlicesActivity = slicesActivity;
    }

    @Provides
    @Singleton
    SlicesActivity providesSlicesActivity() {
        return mSlicesActivity;
    }
}
