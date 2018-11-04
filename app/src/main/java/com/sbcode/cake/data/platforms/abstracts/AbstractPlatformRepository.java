package com.sbcode.cake.data.platforms.abstracts;

import android.content.Context;
import android.content.Intent;

import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.interfaces.PlatformRepository;
import com.sbcode.cake.data.platforms.interfaces.PostsRequestMapper;
import com.sbcode.cake.data.platforms.manager.PlatformManager;
import com.sbcode.cake.data.room.services.DatabaseDeleteUsersService;
import com.sbcode.cake.data.webservices.ApiRequestWebService;
import com.sbcode.cake.data.webservices.JsonRequestWebService;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public abstract class AbstractPlatformRepository implements PlatformRepository {

    private final PlatformManager mPlatformManager;

    private final int mRequestsPerMinute;
    protected final int mQueryResultsLimit;

    private long mRollingThrottleDate;

    protected AbstractPlatformRepository(PlatformManager platformManager,
                                         int queryLimit,
                                         int requestPerMinute) {
        mPlatformManager = platformManager;
        mQueryResultsLimit = queryLimit;
        mRequestsPerMinute = requestPerMinute;
    }

    protected void queryJsonRequestService(Context context, PlatformType platformType,
                                           PostsRequestMapper jsonPostsRequestMapper) {

        if (throttleLimitReached()) {
            Timber.w("Throttle limit reached, limit = %d current value = %d, Skipping request for %s",
                    mRequestsPerMinute, convertMillisToMinutes(getThrottleValue()), platformType);
            return;
        }

        increaseThrottleValue();

        Intent extras = JsonRequestWebService.intentBuilder(
                platformType.name(),
                jsonPostsRequestMapper.toString()
        );

        Timber.d(
                "Query Json Request service method called in AbstractPlatformRepository, request string is: %s",
                jsonPostsRequestMapper.toString());

        JsonRequestWebService.enqueueWork(context, JsonRequestWebService.class,
                JsonRequestWebService.JOB_ID, extras);
    }

    protected void queryApiRequestService(Context context, PlatformType platformType,
                                          PostsRequestMapper postsRequestMapper) {

        if (throttleLimitReached()) {
            Timber.w("Throttle limit reached, limit = %d current value = %d, Skipping request for %s",
                    mRequestsPerMinute, convertMillisToMinutes(getThrottleValue()), platformType);
            return;
        }

        Timber.d("PlatformType.name, request mapper id = %s, %s", platformType.name(), postsRequestMapper.getUserId());

        increaseThrottleValue();

        Intent extras = ApiRequestWebService.intentBuilder(platformType.name(), postsRequestMapper);

        ApiRequestWebService.enqueueWork(context, ApiRequestWebService.class,
                ApiRequestWebService.JOB_ID, extras);
    }

    protected void deleteDataService(Context context, PlatformType platformType) {
        Intent extras = DatabaseDeleteUsersService.intentBuilder(platformType);

        DatabaseDeleteUsersService.enqueueWork(context, DatabaseDeleteUsersService.class,
                DatabaseDeleteUsersService.JOB_ID, extras);
    }

    @Override
    public void increaseThrottleValue() {
        if (mRollingThrottleDate == 0) {
            mRollingThrottleDate = System.currentTimeMillis();
        }

        mRollingThrottleDate += TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public boolean throttleLimitReached() {
        return getThrottleValue() > TimeUnit.MINUTES.toMillis(mRequestsPerMinute);
    }

    private long getThrottleValue() {
        return mRollingThrottleDate - System.currentTimeMillis();
    }

    private long convertMillisToMinutes(long value) {
        return TimeUnit.MILLISECONDS.toMinutes(value);
    }

    protected PlatformManager getPlatformManager() {
        return mPlatformManager;
    }
}
