package com.sbcode.cake.data.platforms.implementations.twitter.handlers;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.NewPostHandler;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.ui.slices.CakeActivity;
import com.sbcode.cake.utils.AppUtils;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import timber.log.Timber;

/**
 * Handles posts made to Twitter via the TweetComposer api with web url backup.
 */
public class TwitterNewPostHandler implements NewPostHandler {

    @Override
    public void handleNewPostMessage(CakeActivity activity) {
        Platform platform =
                CakeApplication
                        .getInstance()
                        .component()
                        .getPlatformManager()
                        .get(PlatformType.TWITTER);

        if (platform.nativeAppInstalled()) {
            TweetComposer.Builder builder = new TweetComposer.Builder(activity);
            builder.text("");
            builder.show();
        } else {
            displaySnackbar(activity, platform);
        }
    }

    @Override
    public void initChooseNewPostImage(CakeActivity activity) {
        Platform platform =
                CakeApplication
                        .getInstance()
                        .component()
                        .getPlatformManager()
                        .get(PlatformType.TWITTER);

        if (platform.nativeAppInstalled()) {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            if (imageIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(imageIntent, IMAGE_SELECT_REQUEST_CODE);
            } else {
                Timber.e("Could not resolve activity for image chooser intent");
            }
        } else {
            if (activity.getCurrentFocus() != null) {
                displaySnackbar(activity, platform);
            }
        }
    }

    @Override
    public void handleNewPostImage(CakeActivity activity, Uri uri) {
        TweetComposer.Builder builder = new TweetComposer.Builder(activity);
        builder.image(uri);
        builder.show();
    }

    private void displaySnackbar(CakeActivity activity, Platform platform) {
        View view = activity.findViewById(R.id.activity_slices_root_view);

        AppUtils.summonSnackBarSelfClosingWithRunnable(view,
                String.format(activity.getString(R.string.snackbar_app_image_err),
                        activity.getString(R.string.twitter_title)),
                activity.getString(R.string.text_install),
                () -> activity.startActivity(
                        new Intent(Intent.ACTION_DEFAULT,
                                Uri.parse(platform.getNativeAppInstallUrl()))
                )
        );
    }
}
