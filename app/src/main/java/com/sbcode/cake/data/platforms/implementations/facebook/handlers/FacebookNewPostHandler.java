package com.sbcode.cake.data.platforms.implementations.facebook.handlers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.facebook.share.model.ShareMedia;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.NewPostHandler;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.ui.slices.CakeActivity;
import com.sbcode.cake.utils.AppUtils;

import java.io.IOException;

import timber.log.Timber;

/**
 * Handles new posts made to the Facebook Graph API.
 */
public class FacebookNewPostHandler implements NewPostHandler {

    @Override
    public void handleNewPostMessage(CakeActivity activity) {
        Platform platform =
                CakeApplication
                        .getInstance()
                        .component()
                        .getPlatformManager()
                        .get(PlatformType.FACEBOOK);

        PackageManager packageManager = CakeApplication.getInstance().getPackageManager();

        if (platform.nativeAppInstalled()) {
            Intent newFacebookMessageIntent = new Intent(Intent.ACTION_SEND);
            newFacebookMessageIntent.setPackage(platform.getPlatformNativePackageId());
            newFacebookMessageIntent.setType("text/plain");

            if (newFacebookMessageIntent.resolveActivity(packageManager) != null) {
                activity.startActivity(newFacebookMessageIntent);
            }
        } else {
            installSnackbar(activity, platform);
        }
    }

    @Override
    public void initChooseNewPostImage(CakeActivity activity) {
        Platform platform =
                CakeApplication
                        .getInstance()
                        .component()
                        .getPlatformManager()
                        .get(PlatformType.FACEBOOK);

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
            installSnackbar(activity, platform);
        }
    }

    @Override
    public void handleNewPostImage(CakeActivity activity, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (IOException e) {
            Timber.e("Error retrieving image from media store on Android device");
            e.printStackTrace();
        }

        ShareDialog shareDialog = new ShareDialog(activity);
        ShareMedia shareMedia = new SharePhoto.Builder().setBitmap(bitmap).build();
        ShareMediaContent shareMediaContent =
                new ShareMediaContent.Builder().addMedium(shareMedia).build();
        shareDialog.show(shareMediaContent, ShareDialog.Mode.AUTOMATIC);
    }

    private void installSnackbar(CakeActivity activity, Platform platform) {
        View view = activity.findViewById(R.id.activity_slices_root_view);

        AppUtils.summonSnackBarSelfClosingWithRunnable(view,
                String.format(activity.getString(R.string.snackbar_app_image_err),
                        activity.getString(R.string.facebook_title)),
                activity.getString(R.string.text_install),
                () -> activity.startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(platform.getNativeAppInstallUrl()))
                )
        );
    }
}
