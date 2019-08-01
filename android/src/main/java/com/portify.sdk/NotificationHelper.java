package com.portify.sdk;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {
    private static String channelId = "com_fip_drive_channel_no_sound";

    public static void createNotificationChannelIfNeeded(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            notificationChannel = new NotificationChannel(channelId, "Portify GO reseanalys", importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static Notification getCompatNotification(Application application, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, channelId);
        int iconId = application.getResources().getIdentifier("ic_launcher", "mipmap", application.getPackageName());

        builder
                .setSmallIcon(iconId)
                .setContentTitle("DriveQuant")
                .setContentText("Pour des mesures de sécurité votre position est suivie au cours de votre mission")
                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), iconId))
                .setSound(null)
                .setTicker("Pour des mesures de sécurité votre position est suivie au cours de votre mission")
                .setWhen(System.currentTimeMillis());

        Intent startIntent = new Intent(context, DriveQuantSdkRunner.class);
        PendingIntent contentIntent = PendingIntent.getActivity(application, 1000, startIntent, 0);
        builder.setContentIntent(contentIntent);

        return builder.build();
    }

    public static Notification getBluetoothNotification(Application application, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, channelId);
        int iconId = application.getResources().getIdentifier("ic_launcher", "mipmap", application.getPackageName());

        builder
                .setSmallIcon(iconId)
                .setContentTitle("FIP Drive")
                .setContentText("Bluetooth har stängts av, Portify GO kommer att avslutas.")
                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(), iconId))
                .setSound(null)
                .setTicker("Bluetooth har stängts av, Portify GO kommer att avslutas.")
                .setWhen(System.currentTimeMillis());

        Intent startIntent = new Intent(context, DriveQuantSdkRunner.class);
        PendingIntent contentIntent = PendingIntent.getActivity(application, 1000, startIntent, 0);
        builder.setContentIntent(contentIntent);

        return builder.build();
    }
}
