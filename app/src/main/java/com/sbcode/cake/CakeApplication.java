package com.sbcode.cake;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sbcode.cake.data.MockDataProvider;
import com.sbcode.cake.data.room.services.DatabaseMaintenanceJobService;
import com.sbcode.cake.data.platforms.manager.CakePlatformManager;
import com.sbcode.cake.data.room.CakeDatabase;
import com.sbcode.cake.data.room.services.DatabaseDeleteUsersService;
import com.sbcode.cake.data.room.services.DatabaseQueryNewPostsService;
import com.sbcode.cake.data.room.services.DatabaseRefreshDataService;
import com.sbcode.cake.data.webservices.ApiRequestWebService;
import com.sbcode.cake.data.webservices.JsonRequestWebService;
import com.sbcode.cake.injection.components.modules.CakeApplicationModule;
import com.sbcode.cake.injection.components.modules.PlatformManagerModule;
import com.sbcode.cake.injection.components.modules.StatusReporterModule;
import com.sbcode.cake.logging.NotLoggingTree;
import com.sbcode.cake.ui.dialog.PostDialogFragment;
import com.sbcode.cake.ui.reporting.StatusReporter;
import com.sbcode.cake.ui.slices.fragments.AccountsFragment;
import com.sbcode.cake.utils.AppUtils;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import javax.inject.Singleton;

import dagger.Component;
import timber.log.Timber;

/**
 * Cake Application class which handles application setup.
 */
public class CakeApplication extends CakeBaseApplication {

    private static final long MAINTENANCE_JOB_INTERVAL = AppUtils.minutesToMillis(60 * 4);

    private static int mActivityCountStack;
    private static CakeApplication mInstance;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private CakeApplicationComponent mCakeApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        initMaintenanceJob();
        initializeByBuildConfig();

        sAnalytics = GoogleAnalytics.getInstance(this);
        registerActivityLifecycleCallbacks(new CakeActivityLifecycleCallbacks());

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                CakeNotifications.createChannel(this, notificationManager);
            } else {
                Timber.e("Could not get notification manager instance");
            }
        }

        mCakeApplicationComponent = DaggerCakeApplication_CakeApplicationComponent
                .builder()
                .statusReporterModule(new StatusReporterModule())
                .build();
    }

    private void initializeByBuildConfig() {
        TwitterConfig twitterConfig;

        if (BuildConfig.DEBUG) {
            Stetho.Initializer initializer = Stetho.newInitializerBuilder(this)
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .build();
            Stetho.initialize(initializer);

            Timber.plant(new Timber.DebugTree());
            twitterConfig = new TwitterConfig.Builder(this)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_KEY,
                            BuildConfig.TWITTER_CONSUMER_SECRET))
                    .debug(true).build();
        } else {
            Timber.plant(new NotLoggingTree());
            twitterConfig = new TwitterConfig.Builder(this)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_KEY,
                            BuildConfig.TWITTER_CONSUMER_SECRET))
                    .debug(false).build();
        }

        Twitter.initialize(twitterConfig);
    }

    public static CakeDatabase provideCakeDatabase(Context context) {
        return Room.databaseBuilder(context, CakeDatabase.class, CakeDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration().build();
    }

    public static CakeApplication getInstance() {
        if (mInstance == null)
            mInstance = new CakeApplication();

        return mInstance;
    }

    public CakeApplicationComponent component() {
        return mCakeApplicationComponent;
    }

    public static boolean isInForeground() {
        return mActivityCountStack > 0;
    }

    void initMaintenanceJob() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobInfo maintenanceJobInfo =
                    new JobInfo
                            .Builder(DatabaseMaintenanceJobService.JOB_ID,
                            new ComponentName(CakeApplication.this,
                                    DatabaseMaintenanceJobService.class.getName()))
                            .setBackoffCriteria(AppUtils.minutesToMillis(5),
                                    JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                            .setPeriodic(AppUtils.minutesToMillis(MAINTENANCE_JOB_INTERVAL))
                            .setPersisted(true)
                            .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            if (jobScheduler != null) {
                jobScheduler.schedule(maintenanceJobInfo);
            } else {
                Timber.e("Could not get job scheduler instance");
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent startMaintenanceJobIntent = new Intent(this, CakeReceiver.class);
            startMaintenanceJobIntent.setAction(getString(R.string.uri_cake_start_maintenance_job));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    startMaintenanceJobIntent, 0);

            if (alarmManager != null) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() +
                                AppUtils.minutesToMillis(MAINTENANCE_JOB_INTERVAL),
                        pendingIntent);
            } else {
                Timber.e("Could not get alarm manager instance");
            }
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    @Override
    public CakePlatformManager getPlatformManager() {
        return component().getPlatformManager();
    }

    @Component(modules =
            {CakeApplicationModule.class, StatusReporterModule.class,
                    PlatformManagerModule.class}
    )
    @Singleton
    public interface CakeApplicationComponent {
        StatusReporter getStatusReporter();

        CakeApplication getContext();

        CakeDatabase getDatabase();

        CakePlatformManager getPlatformManager();

        void inject(AccountsFragment accountsFragment);

        void inject(JsonRequestWebService jsonRequestService);

        void inject(ApiRequestWebService apiRequestService);

        void inject(DatabaseDeleteUsersService databaseDeleteUsersService);

        void inject(DatabaseRefreshDataService databaseRefreshDataService);

        void inject(DatabaseQueryNewPostsService databaseQueryNewPostsService);

        void inject(PostDialogFragment postDialogFragment);
    }

    class CakeActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            mActivityCountStack++;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            mActivityCountStack--;
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    public static class MockDataTask extends AsyncTask<CakeDatabase, Void, Void> {

        @Override
        protected Void doInBackground(CakeDatabase... databases) {
            databases[0].personsDao().insert(MockDataProvider.provideMockPerson());
            databases[0].sliceDao().insert(MockDataProvider.provideMockSliceData(50));
            return null;
        }
    }
}
