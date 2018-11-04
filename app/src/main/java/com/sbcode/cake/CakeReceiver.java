package com.sbcode.cake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sbcode.cake.data.room.services.DatabaseMaintenanceJobIntentService;

import timber.log.Timber;

/**
 * Receives broadcast messages destined for the Cake app.
 */
public class CakeReceiver extends BroadcastReceiver {

    public CakeReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            Timber.d("CakeReceiver intent is null");
            return;
        }

        Timber.d("Broadcast action: %s", intent.getAction());

        if (intent.getAction() != null && intent.getAction().equals(
                CakeApplication.getInstance().getString(R.string.uri_android_boot_completed))) {

            /* Create a notification when this broadcast is received. */

            Timber.d("Database maintenance job initialized");

            CakeApplication.getInstance().initMaintenanceJob();

        } else if (intent.getAction().equals(CakeApplication.getInstance()
                .getString(R.string.uri_cake_start_maintenance_job))) {

            /* Initialize the maintenance job service when this broadcast is received.
               For use with build versions lower than 21 only. */

            Timber.d("Database maintenance job started");

            Intent extras = new Intent();

            DatabaseMaintenanceJobIntentService.enqueueWork(CakeApplication.getInstance(),
                    DatabaseMaintenanceJobIntentService.class,
                    DatabaseMaintenanceJobIntentService.JOB_ID,
                    extras);
        }
    }
}
