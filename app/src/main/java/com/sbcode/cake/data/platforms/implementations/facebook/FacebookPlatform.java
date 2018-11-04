package com.sbcode.cake.data.platforms.implementations.facebook;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatform;
import com.sbcode.cake.data.platforms.implementations.facebook.handlers.FacebookJsonRequestHandler;
import com.sbcode.cake.data.platforms.implementations.facebook.handlers.FacebookNewPostHandler;
import com.sbcode.cake.data.platforms.implementations.facebook.mappers.request.FacebookMePostsJsonPostsRequestMapper;
import com.sbcode.cake.data.platforms.implementations.facebook.mappers.request.FacebookUsersJsonRequestMapper;
import com.sbcode.cake.data.platforms.implementations.facebook.mappers.response.FacebookJsonResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.JsonResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.NewPostHandler;
import com.sbcode.cake.data.platforms.interfaces.PlatformAccount;
import com.sbcode.cake.data.platforms.interfaces.PlatformRepository;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.RequestHandler;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;
import com.sbcode.cake.data.platforms.utils.FacebookUtils;
import com.sbcode.cake.data.platforms.utils.PlatformUtils;
import com.sbcode.cake.data.room.models.Slice;

/**
 * Platform specific methods for the Facebook platform.
 */
public class FacebookPlatform extends AbstractPlatform {

    private static final String NATIVE_PACKAGE_ID = "com.facebook.katana";

    public FacebookPlatform(PlatformAccount account, PlatformRepository repository) {
        super(account, repository, NATIVE_PACKAGE_ID);
    }

    @Override
    public void initSignInButton(Activity activity, View view) {
        view.setOnClickListener(v -> {
            if (!getAccount().isSignedIn())
                getAccount().performSignIn(activity);
            else
                getAccount().performSignOut(activity);
        });
    }

    @Override
    public PostsRequestMapper getPostsRequestMapper() {
        return new FacebookMePostsJsonPostsRequestMapper();
    }

    @Override
    public UsersRequestMapper getUserRequestMapper() {
        return new FacebookUsersJsonRequestMapper();
    }

    @Override
    public RequestHandler getRequestHandler() {
        return new FacebookJsonRequestHandler();
    }

    @Override
    public NewPostHandler getNewPostHandler() {
        return new FacebookNewPostHandler();
    }

    @Override
    public JsonResponseMapper getResponseMapper() {
        return new FacebookJsonResponseMapper();
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.FACEBOOK;
    }

    @Override
    public Uri getNativePostUri(Slice slice) {
        return Uri.parse("fb://facewebmodal/f?href=" + slice.postLinkUrl);
    }

    @Override
    public PlatformUtils getUtils() {
        return new FacebookUtils();
    }

    @Override
    public int getBadgeImageResourceId() {
        return R.drawable.badge_facebook;
    }

    @Override
    public int getPlatformVerbResourceId() {
        return R.string.platform_verb_facebook;
    }

    @Override
    public int getLikesFormattedMessageResourceId() {
        return R.string.facebook_ps_likes;
    }

    @Override
    public int getCommentsFormattedMessageResourceId() {
        return R.string.facebook_ps_comments;
    }
}
