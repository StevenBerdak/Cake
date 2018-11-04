package com.sbcode.cake.data.platforms.implementations.twitter;

import android.content.Context;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatformRepository;
import com.sbcode.cake.data.platforms.interfaces.ApiPostsRequestMapper;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Handles making requests on the Twitter Android API.
 */
public class TwitterRepository extends AbstractPlatformRepository {

    public TwitterRepository(PlatformManager platformManager, int queryLimit,
                             int requestsPerMinute) {
        super(platformManager, queryLimit, requestsPerMinute);
    }

    @Override
    public void queryInitial(Context context) {
        PostsRequestMapper requestMapper =
                getPlatformManager().get(PlatformType.TWITTER).getPostsRequestMapper();
        requestMapper.setLimit(mQueryResultsLimit);

        TwitterSession twitterApiClient =
                TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterApiClient != null) {
            requestMapper.setUserId(String.valueOf(twitterApiClient.getUserId()));
        }

        queryApiRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryBefore(Context context, long postId) {
        ApiPostsRequestMapper requestMapper = (ApiPostsRequestMapper)
                getPlatformManager().get(PlatformType.TWITTER).getPostsRequestMapper();
        requestMapper.setLimit(mQueryResultsLimit);
        requestMapper.postsBefore(postId);

        TwitterSession twitterApiClient =
                TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterApiClient != null) {
            requestMapper.setUserId(String.valueOf(twitterApiClient.getUserId()));
        }

        queryApiRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryAfter(Context context, long postId) {
        ApiPostsRequestMapper requestMapper = (ApiPostsRequestMapper)
                getPlatformManager().get(PlatformType.TWITTER).getPostsRequestMapper();
        requestMapper.setLimit(mQueryResultsLimit);
        requestMapper.postsAfter(postId);

        TwitterSession twitterApiClient =
                TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterApiClient != null) {
            requestMapper.setUserId(String.valueOf(twitterApiClient.getUserId()));
        }

        queryApiRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryRange(Context context, long startRange, long stopRange) {
        ApiPostsRequestMapper requestMapper = (ApiPostsRequestMapper)
                getPlatformManager().get(PlatformType.TWITTER).getPostsRequestMapper();
        requestMapper.setLimit(mQueryResultsLimit);
        requestMapper.postsBefore(startRange);
        requestMapper.postsAfter(stopRange);

        TwitterSession twitterApiClient =
                TwitterCore.getInstance().getSessionManager().getActiveSession();

        if (twitterApiClient != null) {
            requestMapper.setUserId(String.valueOf(twitterApiClient.getUserId()));
        }

        queryApiRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void deleteAllData(Context context) {
        deleteDataService(context, getPlatform());
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.TWITTER;
    }
}
