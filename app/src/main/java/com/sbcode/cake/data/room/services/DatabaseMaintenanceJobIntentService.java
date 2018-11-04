package com.sbcode.cake.data.room.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.CakeBaseApplication;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.models.Slice;
import com.sbcode.cake.logic.callbacks.CallbackManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for performing data maintenance on the SQL database.
 */
public class DatabaseMaintenanceJobIntentService extends JobIntentService {

    private static final int DATABASE_SLICES_MAX = 3000;
    public static final int JOB_ID = 529198611;

    private final CallbackManager<TestCallback> mCallbackManager;
    private CakeDatabase mCakeDatabase;

    public DatabaseMaintenanceJobIntentService() {
        mCallbackManager = new CallbackManager<>();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        CakeDatabase cakeDatabase;

        if (mCakeDatabase == null) {
            cakeDatabase =
                    ((CakeApplication) getApplicationContext()).component().getDatabase();
        } else {
            cakeDatabase = mCakeDatabase;
        }

        if (cakeDatabase == null) {
            return;
        }

        CakePlatformManager cakePlatformManager =
                ((CakeBaseApplication) getApplicationContext()).getPlatformManager();

        /* Check if app is in the foreground */
        if (CakeApplication.isInForeground()) {
            stopSelf();
            return;
        }

        List<Slice> allSlices = cakeDatabase.sliceDao().queryAllForList();

        if (allSlices != null && allSlices.size() > DATABASE_SLICES_MAX) {
            List<Slice> slicesToDelete = new ArrayList<>();

            for (int i = DATABASE_SLICES_MAX; i < allSlices.size(); ++i) {
                slicesToDelete.add(allSlices.get(i));
            }

            cakeDatabase.sliceDao().delete(slicesToDelete);
        }

        /* Refresh all platform data */

        for (Platform platform : cakePlatformManager.getPlatformsSignedIn()) {
            Intent extras = DatabaseRefreshDataService.intentBuilder(platform.getPlatformType());
            DatabaseRefreshDataService.enqueueWork(getApplicationContext(),
                    DatabaseRefreshDataService.class, DatabaseRefreshDataService.JOB_ID,
                    extras
            );
        }

        for (TestCallback cb : mCallbackManager.getCallbacks()) {
            cb.onServiceCompleted();
        }
    }

    public void provideDatabase(@NonNull CakeDatabase cakeDatabase) {
        this.mCakeDatabase = cakeDatabase;
    }

    public void manualStartService(@NonNull Intent intent) {
        onHandleWork(intent);
    }

    public CallbackManager<TestCallback> getCallbackManager() {
        return mCallbackManager;
    }

    public interface TestCallback extends CallbackManager.Callback {
        void onServiceCompleted();
    }
}
