package com.sbcode.cake.injection.components;

import com.sbcode.cake.injection.components.modules.CakeApplicationModule;
import com.sbcode.cake.injection.components.modules.SlicesActivityModule;
import com.sbcode.cake.injection.components.modules.SlicesViewModelModule;
import com.sbcode.cake.ui.slices.SlicesActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {CakeApplicationModule.class, SlicesActivityModule.class,
        SlicesViewModelModule.class})
@Singleton
public interface SlicesActivityComponent {

    void inject(SlicesActivity slicesActivity);

    SlicesActivity provideSlicesActivity();
}
