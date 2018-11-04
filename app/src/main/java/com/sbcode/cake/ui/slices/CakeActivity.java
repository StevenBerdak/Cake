package com.sbcode.cake.ui.slices;

import android.support.v7.app.AppCompatActivity;

import com.sbcode.cake.data.PlatformType;

/**
 * Abstract Cake Activity.
 */
public abstract class CakeActivity extends AppCompatActivity {

    protected PlatformType mActivityResultMetaPlatform;

    public abstract void loadUrl(String url);

    public void setActivityResultMetaPlatform(PlatformType mActivityResultMetaPlatform) {
        this.mActivityResultMetaPlatform = mActivityResultMetaPlatform;
    }
}
