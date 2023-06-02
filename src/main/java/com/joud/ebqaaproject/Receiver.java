package com.joud.ebqaaproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Receiver extends BroadcastReceiver {
    public static int NOTIFICATION_ID = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        Bundle bundle = intent.getExtras();
        String name = (String) bundle.get("name");
        String days = (String) bundle.get("days");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Notification.CHANNEL_ID)
                .setSmallIcon(R.drawable.toolbar_logo)
                .setContentTitle("Ebqaa: An item is expiring soon!")
                .setContentText(name + " will expire in " + days + " days.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);
        managerCompat.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}