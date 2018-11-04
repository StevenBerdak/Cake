package com.sbcode.cake.data.room.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.PlatformType;
import com.sbcode.cake.data.room.CakeDatabase;

import javax.inject.Inject;

/**
 * Service for deleting data from the Users table.
 */
public class DatabaseDeleteUsersService extends JobIntentService {

    private static final String PLATFORM_TYPE = "platform_type";
    public static final int JOB_ID = 529198603;

    @SuppressWarnings("WeakerAccess")
    @Inject
    CakeDatabase mCakeDatabase;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        ((CakeApplication) getApplicationContext()).component().inject(this);

        mCakeDatabase.personsDao()
                .deletePlatformUsersByPlatform(intent.getStringExtra(PLATFORM_TYPE));
    }

    public static Intent intentBuilder(PlatformType platformType) {
        Intent extraData = new Intent();
        extraData.putExtra(PLATFORM_TYPE, platformType.name());
        return extraData;
    }
}
