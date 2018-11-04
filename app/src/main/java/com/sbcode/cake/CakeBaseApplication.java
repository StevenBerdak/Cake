package com.sbcode.cake;

import android.support.multidex.MultiDexApplication;

import com.sbcode.cake.data.platforms.manager.CakePlatformManager;

/**
 * Base class for a Cake Application.
 */
public abstract class CakeBaseApplication extends MultiDexApplication {

    public abstract CakePlatformManager getPlatformManager();
}
