package com.sbcode.cake.injection.components.modules;

import com.sbcode.cake.ui.reporting.StatusReporter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StatusReporterModule {

    private final StatusReporter mStatusReporter;

    public StatusReporterModule() {
        mStatusReporter = new StatusReporter();
    }

    @Provides
    @Singleton
    StatusReporter provideStatusReporter() {
        return mStatusReporter;
    }
}
