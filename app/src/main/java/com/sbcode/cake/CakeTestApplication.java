package com.sbcode.cake;

import android.annotation.SuppressLint;

import com.sbcode.cake.data.platforms.manager.CakePlatformManager;

/**
 * Cake Application test class.
 */
@SuppressLint("Registered")
public class CakeTestApplication extends CakeBaseApplication {

    @Override
    public CakePlatformManager getPlatformManager() {
        return new CakePlatformManager();
    }
}
