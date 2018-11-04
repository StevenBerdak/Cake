package com.sbcode.cake.ui.slices.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.transition.Slide;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbcode.cake.R;
import com.sbcode.cake.ui.slices.SlicesActivity;

import butterknife.ButterKnife;

/**
 * Fragment for the 'About' screen.
 */
public class AboutFragment extends CakeFragment {

    public static final String FRAGMENT_TAG = "about_fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void updateOptionsMenu() {
        if (getActivity() == null) {
            return;
        }

        SlicesActivity slicesActivity = (SlicesActivity) getActivity();
        slicesActivity.setAppBarState(FRAGMENT_TAG, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOptionsMenu();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition enterTransition() {
        return new Slide();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition exitTransition() {
        return new Slide();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected int transitionDuration() {
        return 300;
    }
}
