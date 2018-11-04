package com.sbcode.cake.data.room.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.platforms.utils.PlatformUtils;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.models.Slice;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Service from querying new posts for the provided platform.
 */
public class DatabaseQueryNewPostsService extends JobIntentService {

    private static final String PLATFORM_TYPE = "platform_type";
    public static final int JOB_ID = 529198615;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakeDatabase mCakeDatabase;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakePlatformManager mCakePlatformManager;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        ((CakeApplication) getApplicationContext()).component()
                .inject(this);

        PlatformType platformType = PlatformType.valueOf(intent.getStringExtra(PLATFORM_TYPE));

        Timber.d("Query new posts for platform service, %s requested",
                platformType.name());

        List<Slice> platformSlices = mCakeDatabase.sliceDao()
                .queryAllByPlatformForList(platformType);

        if (platformSlices.size() == 0) {

            /* If no data attempt to run initial query */

            mCakePlatformManager.get(platformType).getRepository().queryInitial(this);

        } else {

            /* If data attempt to run query for only new items */

            PlatformUtils utils = mCakePlatformManager
                    .get(platformType)
                    .getUtils();

            long afterBoundary = utils.getIndicatorLong(platformSlices.get(0));

            mCakePlatformManager.get(platformType).getRepository().queryAfter(
                    this, afterBoundary);
        }
    }

    public static Intent intentBuilder(PlatformType platformType) {
        Intent extras = new Intent();
        extras.putExtra(PLATFORM_TYPE, platformType.name());
        return extras;
    }
}
