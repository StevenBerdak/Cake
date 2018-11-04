package com.sbcode.cake.data.room.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;

import com.sbcode.cake.CakeApplication;
import com.sbcode.cake.data.platforms.interfaces.Platform;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.models.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DatabaseMaintenanceJobService extends JobService {

    private static final int DATABASE_SLICES_MAX = 3000;
    public static final int JOB_ID = 529198610;

    /**
     * Called to indicate that the job has begun executing.  Override this method with the
     * logic for your job.  Like all other component lifecycle callbacks, this method executes
     * on your application's main thread.
     * <p>
     * Return {@code true} from this method if your job needs to continue running.  If you
     * do this, the job remains active until you call
     * {@link #jobFinished(JobParameters, boolean)} to tell the system that it has completed
     * its work, or until the job's required constraints are no longer satisfied.  For
     * example, if the job was scheduled using
     * {@link JobInfo.Builder#setRequiresCharging(boolean) setRequiresCharging(true)},
     * it will be immediately halted by the system if the user unplugs the device from power,
     * the job's {@link #onStopJob(JobParameters)} callback will be invoked, and the app
     * will be expected to shut down all ongoing work connected with that job.
     * <p>
     * The system holds a wakelock on behalf of your app as long as your job is executing.
     * This wakelock is acquired before this method is invoked, and is not released until either
     * you call {@link #jobFinished(JobParameters, boolean)}, or after the system invokes
     * {@link #onStopJob(JobParameters)} to notify your job that it is being shut down
     * prematurely.
     * <p>
     * Returning {@code false} from this method means your job is already finished.  The
     * system's wakelock for the job will be released, and {@link #onStopJob(JobParameters)}
     * will not be invoked.
     *
     * @param params Parameters specifying info about this job, including the optional
     *               extras configured with {@link JobInfo.Builder#setExtras(PersistableBundle).
     *               This object serves to identify this specific running job instance when calling
     *               {@link #jobFinished(JobParameters, boolean)}.
     * @return {@code true} if your service will continue running, using a separate thread
     * when appropriate.  {@code false} means that this job has completed its work.
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        CakeDatabase mCakeDatabase =
                ((CakeApplication) getApplicationContext()).component().getDatabase();

        CakePlatformManager cakePlatformManager =
                ((CakeApplication) getApplicationContext()).component().getPlatformManager();

        /* Check if app is in the foreground */
        if (CakeApplication.isInForeground()) {
            Timber.d("Cake application is in foreground, stopping job service");
            stopSelf();
            return true;
        }

        try {
            new TrimDatabaseAsyncTask(mCakeDatabase, cakePlatformManager).execute().get();
        } catch (ExecutionException e) {
            Timber.d("TrimDatabase execution exception");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Timber.d("TrimDatabase interrupted exception");
            e.printStackTrace();
        }

        /* Refresh all platform data */

        for (Platform platform : cakePlatformManager.getPlatformsSignedIn()) {
            Intent extras = DatabaseRefreshDataService.intentBuilder(platform.getPlatformType());
            DatabaseRefreshDataService.enqueueWork(getApplicationContext(),
                    DatabaseRefreshDataService.class, DatabaseRefreshDataService.JOB_ID,
                    extras
            );
        }
        return false;
    }

    /**
     * This method is called if the system has determined that you must stop execution of your job
     * even before you've had a chance to call {@link #jobFinished(JobParameters, boolean)}.
     * <p>
     * <p>This will happen if the requirements specified at schedule time are no longer met. For
     * example you may have requested WiFi with
     * {@link JobInfo.Builder#setRequiredNetworkType(int)}, yet while your
     * job was executing the user toggled WiFi. Another example is if you had specified
     * {@link JobInfo.Builder#setRequiresDeviceIdle(boolean)}, and the phone left its
     * idle maintenance window. You are solely responsible for the behavior of your application
     * upon receipt of this message; your app will likely start to misbehave if you ignore it.
     * <p>
     * Once this method returns, the system releases the wakelock that it is holding on
     * behalf of the job.</p>
     *
     * @param params The parameters identifying this job, as supplied to
     *               the job in the {@link #onStartJob(JobParameters)} callback.
     * @return {@code true} to indicate to the JobManager whether you'd like to reschedule
     * this job based on the retry criteria provided at job creation-time; or {@code false}
     * to end the job entirely.  Regardless of the value returned, your job must stop executing.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    static class TrimDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

        final CakeDatabase mCakeDatabase;
        final CakePlatformManager mCakePlatformManager;

        TrimDatabaseAsyncTask(CakeDatabase cakeDatabase,
                              CakePlatformManager mCakePlatformManager) {
            this.mCakeDatabase = cakeDatabase;
            this.mCakePlatformManager = mCakePlatformManager;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... voids) {
            List<Slice> allSlices = mCakeDatabase.sliceDao().queryAllForList();

            if (allSlices != null && allSlices.size() > DATABASE_SLICES_MAX) {
                List<Slice> slicesToDelete = new ArrayList<>();

                for (int i = DATABASE_SLICES_MAX; i < allSlices.size(); ++i) {
                    slicesToDelete.add(allSlices.get(i));
                }

                mCakeDatabase.sliceDao().delete(slicesToDelete);
            }
            return null;
        }
    }
}
