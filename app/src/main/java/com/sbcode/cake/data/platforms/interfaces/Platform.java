package com.sbcode.cake.data.platforms.interfaces;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.utils.PlatformUtils;
import com.sbcode.cake.data.room.models.Slice;

public interface Platform {

    void initSignInButton(Activity activity, View view);

    PlatformAccount getAccount();

    PlatformRepository getRepository();

    PostsRequestMapper getPostsRequestMapper();

    UsersRequestMapper getUserRequestMapper();

    RequestHandler getRequestHandler();

    NewPostHandler getNewPostHandler();

    ResponseMapper getResponseMapper();

    boolean nativeAppInstalled();

    String getNativeAppInstallUrl();

    PlatformUtils getUtils();

    PlatformType getPlatformType();

    String getPlatformNativePackageId();

    Uri getNativePostUri(Slice slice);

    int getBadgeImageResourceId();

    int getPlatformVerbResourceId();

    int getLikesFormattedMessageResourceId();

    int getCommentsFormattedMessageResourceId();
}
