package com.example.healthappelderly;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class AppNotificationManager {
    public static final int NOTIFICATION_POST_CODE = 1;
    private NotificationManagerCompat notif_mngr;
    private Context activity;
    public AppNotificationManager(Context activity) {
        this.activity = activity;
        notif_mngr = NotificationManagerCompat.from(activity);
    }



    public void discrete_notif(int n_id, String title, String message) {
        Notification notif = (new NotificationCompat.Builder(activity, App.channel_low_id)
                .setSmallIcon(R.drawable.baseline_fastfood_24)
                .setContentText(message)
                .setContentTitle(title)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
        ).build();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            notif_mngr.notify(n_id, notif);

    }

    public void medium_notif(int n_id, String title, String message) {
        Notification notif = (new NotificationCompat.Builder(activity, App.channel_medium_id)
                .setSmallIcon(R.drawable.baseline_fastfood_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        ).build();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            notif_mngr.notify(n_id, notif);
    }

    public void high_notif(int n_id,String title, String message) {
        Notification notif = (new NotificationCompat.Builder(activity, App.channel_high_id)
                .setSmallIcon(R.drawable.baseline_fastfood_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
        ).build();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            notif_mngr.notify(n_id, notif);
    }


}