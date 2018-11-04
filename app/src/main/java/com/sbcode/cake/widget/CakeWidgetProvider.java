package com.sbcode.cake.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sbcode.cake.R;
import com.sbcode.cake.ui.slices.SlicesActivity;

import timber.log.Timber;

public class CakeWidgetProvider extends AppWidgetProvider {

    private static final String ANDROID_APP_WIDGET_UPDATE = AppWidgetManager.ACTION_APPWIDGET_UPDATE;
    private static final String UPDATES_NEW_LIKES = "updates_new_likes";
    private static final String UPDATES_NEW_COMMENTS = "updates_new_comments";

    public CakeWidgetProvider() {
    }

    /**
     * Called when app is added to home screen and also called when time elapsed specified
     * updatePeriodMillis in xml config has been reached.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    /**
     * This is called when an instance the App Widget is created for the first time. For example,
     * if the user adds two instances of your App Widget, this is only called the first time.
     * If you need to open a new database or perform other setup that only needs to occur once for
     * all App Widget instances, then this is a good place to do it.
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }


    /**
     * This is called when the last instance of your App Widget is deleted from the App Widget
     * host. This is where you should clean up any work done in onEnabled(Context), such as delete
     * a temporary database.
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                intent.getAction().equals(ANDROID_APP_WIDGET_UPDATE)) {

            Timber.d("CakeWidgetProvider receiver received broadcast");

            /* Get app widget manager instance */
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            /* Get id of each instantiated widget for this provider */
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, CakeWidgetProvider.class)
            );

            int newLikes = intent.getIntExtra(UPDATES_NEW_LIKES, 0);
            int newComments = intent.getIntExtra(UPDATES_NEW_COMMENTS, 0);

            /* Apply the data to the views */

            for (int appWidgetId : appWidgetIds) {

                PendingIntent pendingIntent = SlicesActivity.getPendingIntent(context);

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.widget_stats_layout);
                remoteViews.setOnClickPendingIntent(R.id.cake_widget_root_layout, pendingIntent);
                remoteViews.setTextViewText(R.id.cake_widget_new_likes_number_tv,
                        Integer.toString(newLikes));
                remoteViews.setTextViewText(R.id.cake_widget_new_comments_number_tv,
                        Integer.toString(newComments));

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }

        super.onReceive(context, intent);
    }

    public static Intent intentBuilder(int newLikes, int newComments) {
        Intent extras = new Intent(ANDROID_APP_WIDGET_UPDATE);
        extras.putExtra(UPDATES_NEW_LIKES, newLikes);
        extras.putExtra(UPDATES_NEW_COMMENTS, newComments);
        return extras;
    }
}
