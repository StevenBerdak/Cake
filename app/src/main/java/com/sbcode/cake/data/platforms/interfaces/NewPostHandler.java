package com.sbcode.cake.data.platforms.interfaces;

import android.net.Uri;

import com.sbcode.cake.ui.slices.CakeActivity;

public interface NewPostHandler {

    int IMAGE_SELECT_REQUEST_CODE = 11000;

    void handleNewPostMessage(CakeActivity activity);

    void initChooseNewPostImage(CakeActivity activity);

    void handleNewPostImage(CakeActivity activity, Uri uri);
}
