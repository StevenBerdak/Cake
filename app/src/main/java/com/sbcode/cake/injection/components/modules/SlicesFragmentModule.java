package com.sbcode.cake.injection.components.modules;

import android.support.v7.widget.LinearLayoutManager;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.ui.slices.fragments.SlicesFragment;
import com.sbcode.cake.ui.slices.adapter.SlicesAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SlicesFragmentModule {

    private final SlicesFragment mSlicesFragment;

    public SlicesFragmentModule(SlicesFragment fragment) {
        this.mSlicesFragment = fragment;
    }

    @Provides
    @Singleton
    SlicesFragment provideSlicesActivityFragment() {
        return mSlicesFragment;
    }

    @Provides
    @Singleton
    SlicesAdapter provideSlicesAdapter(CakePlatformManager cakePlatformManager,
                                       SlicesFragment slicesFragment) {
        return new SlicesAdapter(cakePlatformManager, slicesFragment);

    }

    @Provides
    @Singleton
    LinearLayoutManager provideLinearLayoutManager(CakeApplication cakeApplication) {
        return new LinearLayoutManager(cakeApplication);
    }
}
