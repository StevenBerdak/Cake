package com.sbcode.cake.ui.slices.fragments;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.transition.Transition;

/**
 * A Cake Fragment abstract class.
 */
public abstract class CakeFragment extends Fragment {

    CakeFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition = enterTransition();
            enterTransition.setDuration(transitionDuration());
            Transition exitTransition = exitTransition();
            exitTransition.setDuration(transitionDuration());

            setEnterTransition(enterTransition);
            setExitTransition(exitTransition);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected abstract Transition enterTransition();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected abstract Transition exitTransition();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected abstract int transitionDuration();

    public abstract void updateOptionsMenu();
}
