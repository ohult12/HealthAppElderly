package com.example.healthappelderly;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String channel_low_id = "CH_LOW";
    public static final String channel_medium_id = "CH_MEDIUM";
    public static final String channel_high_id = "CH_HIGH";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        LocaleHelper.setLocale(this, LocaleHelper.getLocale(this));

    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel_low = new NotificationChannel(
                    channel_low_id,
                    "channel low importance",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel_low.setDescription("This is for notifications with low importance");

            NotificationChannel channel_medium = new NotificationChannel(
                    channel_medium_id,
                    "channel medium importance",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel_medium.setDescription("This is for notifications with medium importance");

            NotificationChannel channel_high = new NotificationChannel(
                    channel_high_id,
                    "channel high importance",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel_high.setDescription("This is for notifications with high importance");

            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel_low);
            manager.createNotificationChannel(channel_medium);
            manager.createNotificationChannel(channel_high);


        }
    }
}
