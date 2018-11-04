package com.sbcode.cake.data.platforms.implementations.facebook;

import android.content.Context;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.abstracts.AbstractPlatformRepository;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.sbcode.cake.data.platforms.utils.FacebookUtils;

import timber.log.Timber;

/**
 * Handles making requests on the Facebook Graph API.
 */
public class FacebookRepository extends AbstractPlatformRepository {

    public FacebookRepository(PlatformManager platformManager, int queryLimit,
                              int requestsPerMinute) {
        super(platformManager, queryLimit, requestsPerMinute);
    }

    @Override
    public void queryInitial(Context context) {
        PostsRequestMapper requestMapper = getPlatformManager().get(getPlatform())
                .getPostsRequestMapper();
        requestMapper.setLimit(mQueryResultsLimit);

        queryJsonRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryBefore(Context context, long date) {
        date = new FacebookUtils().getFacebookDate(date);
        PostsRequestMapper requestMapper = getPlatformManager().get(getPlatform())
                .getPostsRequestMapper();
        requestMapper.postsBefore(date);
        requestMapper.setLimit(mQueryResultsLimit);
        queryJsonRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryAfter(Context context, long date) {
        Timber.d("Facebook repository query after %s", date);
        date = new FacebookUtils().getFacebookDate(date);
        PostsRequestMapper requestMapper = getPlatformManager().get(getPlatform())
                .getPostsRequestMapper();
        requestMapper.postsAfter(date);
        requestMapper.setLimit(mQueryResultsLimit);
        queryJsonRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void queryRange(Context context, long startRange, long stopRange) {
        FacebookUtils utils = new FacebookUtils();

        startRange = utils.getFacebookDate(startRange);
        stopRange = utils.getFacebookDate(stopRange);

        PostsRequestMapper requestMapper = getPlatformManager().get(getPlatform())
                .getPostsRequestMapper();
        requestMapper.postsBefore(stopRange);
        requestMapper.postsAfter(startRange);
        requestMapper.setLimit(mQueryResultsLimit);
        queryJsonRequestService(context, getPlatform(), requestMapper);
    }

    @Override
    public void deleteAllData(Context context) {
        deleteDataService(context, getPlatform());
    }

    @Override
    public PlatformType getPlatform() {
        return PlatformType.FACEBOOK;
    }
}


