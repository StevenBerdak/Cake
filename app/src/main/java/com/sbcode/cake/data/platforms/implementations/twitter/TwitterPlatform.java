package com.sbcode.cake.data.platforms.implementations.twitter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.sbcode.cake.R;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatform;
import com.sbcode.cake.data.platforms.implementations.twitter.handlers.TwitterNewPostHandler;
import com.sbcode.cake.data.platforms.implementations.twitter.handlers.TwitterApiRequestHandler;
import com.sbcode.cake.data.platforms.implementations.twitter.mappers.request.TwitterUserTimelineRequestMapper;
import com.sbcode.cake.data.platforms.implementations.twitter.mappers.request.TwitterUsersLookupRequestMapper;
import com.sbcode.cake.data.platforms.implementations.twitter.mappers.response.TwitterApiResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.ApiResponseMapper;
import com.sbcode.cake.data.platforms.interfaces.NewPostHandler;
import com.sbcode.cake.data.platforms.interfaces.PlatformAccount;
import com.sbcode.cake.data.platforms.interfaces.PlatformRepository;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.RequestHandler;
import com.sbcode.cake.data.platforms.interfaces.UsersRequestMapper;
import com.sbcode.cake.data.platforms.utils.PlatformUtils;
import com.sbcode.cake.data.platforms.utils.TwitterUtils;
import com.sbcode.cake.data.room.models.Slice;

/**
 * Platform specific methods for the Twitter platform.
 */
public class TwitterPlatform extends AbstractPlatform {

    private static final String NATIVE_PACKAGE_ID = "com.twitter.android";

    public TwitterPlatform(PlatformAccount account, PlatformRepository repository) {
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
        return new TwitterUserTimelineRequestMapper();
    }

    @Override
    public UsersRequestMapper getUserRequestMapper() {
        return new TwitterUsersLookupRequestMapper();
    }

    @Override
    public RequestHandler getRequestHandler() {
        return new TwitterApiRequestHandler();
    }

    @Override
    public NewPostHandler getNewPostHandler() {
        return new TwitterNewPostHandler();
    }

    @Override
    public ApiResponseMapper getResponseMapper() {
        return new TwitterApiResponseMapper();
    }

    @Override
    public PlatformUtils getUtils() {
        return new TwitterUtils();
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.TWITTER;
    }

    @Override
    public Uri getNativePostUri(Slice slice) {
        return Uri.parse("twitter://status?status_id=" + slice.platformPostId);
    }

    @Override
    public int getBadgeImageResourceId() {
        return R.drawable.badge_twitter;
    }

    @Override
    public int getPlatformVerbResourceId() {
        return R.string.platform_verb_twitter;
    }

    @Override
    public int getLikesFormattedMessageResourceId() {
        return R.string.twitter_ps_favorties;
    }

    @Override
    public int getCommentsFormattedMessageResourceId() {
        return R.string.twitter_ps_retweets;
    }
}
