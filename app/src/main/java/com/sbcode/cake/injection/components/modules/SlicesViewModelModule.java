package com.sbcode.cake.injection.components.modules;

import android.arch.lifecycle.ViewModelProviders;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.ui.slices.SlicesActivity;
import com.sbcode.cake.ui.slices.SlicesViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SlicesViewModelModule {

    @Provides
    @Singleton
    SlicesViewModel provideSlicesViewModel(CakeApplication application,
                                           SlicesActivity slicesActivity,
                                           CakePlatformManager manager) {
        SlicesViewModel viewModel = ViewModelProviders.of(slicesActivity)
                .get(SlicesViewModel.class);
        viewModel.setCakeDatabase(application.component().getDatabase());
        viewModel.setPlatformManager(manager);
        return viewModel;
    }
}
