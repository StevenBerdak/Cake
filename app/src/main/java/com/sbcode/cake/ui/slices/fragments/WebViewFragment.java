package com.sbcode.cake.ui.slices.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sbcode.cake.R;
import com.sbcode.cake.ui.slices.SlicesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Fragment for the 'WebView' screen.
 */
public class WebViewFragment extends CakeFragment {

    public static final String FRAGMENT_TAG = "web_view_fragment";
    public static final String ARGUMENT_BUNDLE_URL = "fragment_bundle_argument";

    @BindView(R.id.cake_web_view)
    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getArguments() == null) {
            dismiss();
        } else {
            String url = getArguments().getString(ARGUMENT_BUNDLE_URL);

            if (url == null) {
                dismiss();
            }

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setUseWideViewPort(true);

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Timber.d("On page finished");
                }
            });

            mWebView.loadUrl(url);
        }
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

    private void dismiss() {
        Timber.e("Web View fragment created without proper url, dismissing");

        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition enterTransition() {
        return new Fade();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected Transition exitTransition() {
        return new Fade();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected int transitionDuration() {
        return 300;
    }
}
