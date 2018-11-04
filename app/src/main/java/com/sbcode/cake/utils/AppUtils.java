package com.sbcode.cake.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sbcode.cake.R;

import timber.log.Timber;

public class AppUtils {

    public static void summonSnackBarSelfClosingWithRunnable(final View rootView, String message,
                                                             String buttonMessage,
                                                             Runnable runnable) {

        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(buttonMessage,
                v -> {
                    snackbar.dismiss();
                    runnable.run();
                });
        snackbar.show();
    }

    public static void seamlessLoadFromUrlToContainer(ImageView imageView, String urlString,
                                                      View containerView) {
        Glide.with(imageView)
                .asBitmap()
                .load(urlString)
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                Transition<? super Bitmap> transition) {
                        Timber.d("Glide resource received");

                        imageView.setImageBitmap(bitmap);

                        containerView.setVisibility(View.VISIBLE);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                        alphaAnimation.setDuration(500);
                        alphaAnimation.setRepeatMode(Animation.RESTART);
                        containerView.startAnimation(alphaAnimation);
                    }
                });
    }

    public static long minutesToMillis(long minutes) {
        return (minutes / 60) / 1000;
    }
}
