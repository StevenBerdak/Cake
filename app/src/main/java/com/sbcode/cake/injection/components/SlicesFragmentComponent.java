package com.sbcode.cake.injection.components;

import com.sbcode.cake.injection.components.modules.CakeApplicationModule;
import com.sbcode.cake.injection.components.modules.SlicesActivityModule;
import com.sbcode.cake.injection.components.modules.SlicesFragmentModule;
import com.sbcode.cake.injection.components.modules.SlicesViewModelModule;
import com.sbcode.cake.ui.slices.fragments.SlicesFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules =
        {CakeApplicationModule.class, SlicesActivityModule.class, SlicesFragmentModule.class,
                SlicesViewModelModule.class}
)
@Singleton
public interface SlicesFragmentComponent {

    void inject(SlicesFragment slicesFragment);
}
