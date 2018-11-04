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
 * Service for refreshing data on a provided platform.
 */
public class DatabaseRefreshDataService extends JobIntentService {

    private static final String PLATFORM_TYPE = "platform_type";
    public static final int JOB_ID = 529198607;

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

        Timber.d("Refresh data for platform service, %s requested", platformType.name());

        List<Slice> initialSlices = mCakeDatabase
                .sliceDao()
                .queryAllByPlatformForList(platformType);

        if (initialSlices.size() == 0) {
            return;
        }

        PlatformUtils utils = mCakePlatformManager
                .get(platformType)
                .getUtils();

        long afterBoundary = utils.getIndicatorLong(initialSlices.get(initialSlices.size() - 1));

        Timber.d("Refresh data service performing query on %s with bounds: after = %s",
                platformType, afterBoundary);

        mCakePlatformManager.get(platformType).getRepository().queryAfter(
                getApplicationContext(), afterBoundary
        );
    }

    public static Intent intentBuilder(PlatformType platformType) {
        Intent extraData = new Intent();
        extraData.putExtra(PLATFORM_TYPE, platformType.name());
        return extraData;
    }
}
