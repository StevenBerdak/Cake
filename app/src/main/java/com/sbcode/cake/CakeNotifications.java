package com.sbcode.cake;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.sbcode.cake.ui.slices.SlicesActivity;

/**
 * Handles notifications for the Cake.
 */
public class CakeNotifications {

    private static final String NOTIFICATION_CHANNEL_ID = "CakeNotificationChannel";
    private static final int NOTIFICATION_ID = 529198651;


    @RequiresApi(api = Build.VERSION_CODES.O)
    static void createChannel(Context context, NotificationManager notificationManager) {
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_desc);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);

        notificationManager.createNotificationChannel(channel);

    }

    private static void sendNotification(Context context, NotificationManager notificationManager,
                                         String title, String message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_cake_opaque)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(SlicesActivity.getPendingIntent(context));
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void sendNewLikesCommentsNotification(Context context, int newLikes,
                                                        int newComments) {
        if (newLikes > 0 || newComments > 0) {
            Resources r = context.getResources();
            StringBuilder stringBuilder = new StringBuilder(r.getString(R.string.you_have));

            if (newLikes > 0)
                stringBuilder.append(newLikes).append(r.getString(R.string.new_favorites));

            if (newComments > 0 && newLikes > 0)
                stringBuilder.append(r.getString(R.string.and));

            if (newComments > 0)
                stringBuilder.append(newComments).append(r.getString(R.string.new_comments));

            stringBuilder.append(r.getString(R.string.exclamation));

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

            if (notificationManager != null)
                sendNotification(context, notificationManager,
                        r.getString(R.string.news_for_you), stringBuilder.toString());
        }
    }
}
