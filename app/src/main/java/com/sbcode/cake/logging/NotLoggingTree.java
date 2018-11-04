package com.sbcode.cake.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

/**
 * A blank logging tree for use when a logger is not desirable so that an implementation can
 * still be provided when required when different implementations are provided dependent on the
 * build version which was installed.
 */
public class NotLoggingTree extends Timber.Tree {
    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message,
                       @Nullable Throwable t) {

    }
}
