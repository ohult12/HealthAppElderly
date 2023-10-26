package com.example.healthappelderly;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

enum warnings {
    MISSED,
    CRITICAL,
    MEDIUM,
    EARLY
}

public class ForegroundService extends Service {
    private static final String TAG = "FOREGROUND_SERVICE";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final int work_delay = 30000;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private Thread the_service_thread;
    private boolean thread_may_run = true;
    private AppNotificationManager notif_manager;
    private long time_stamp = System.currentTimeMillis();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        notif_manager = new AppNotificationManager(this);

        Notification notification = new NotificationCompat.Builder(this, App.channel_medium_id)
                .setContentTitle(getString(R.string.foreground_service))
                .setContentText(getString(R.string.service_is_running))
                .setSmallIcon(R.drawable.baseline_fastfood_24)
                .setContentIntent(pendingIntent).build();

        start_service();

        startForeground(1, notification);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread_may_run = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private void start_service() {
        the_service_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (thread_may_run){
                    if (mAuth.getCurrentUser() != null) {
                        do_work();
                        try {
                            Thread.sleep(work_delay);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "ForegroundService.start_service: ", e);
                        }
                    }
                }
            }
        });
        the_service_thread.start();
    }

    private long minutes_since_meal(DataSnapshot meal) {
        try {
            long diff = System.currentTimeMillis() - dateTimeFormat
                    .parse(meal.child("date").getValue(String.class)+" "+meal.child("time_of_day").getValue(String.class))
                    .getTime();
            return diff/60000L;
        } catch (ParseException e) {
            Log.e(TAG, "minutes_since_meal: ", e);
            return -1L;
        }
    }


    private void do_work() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTimeInMillis());
        String username = mAuth.getCurrentUser().getDisplayName();
        DatabaseReference ref = db.getReference(String.format("Elder/%s/Meals/%s", username, date));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot meals) {
                for (DataSnapshot meal: meals.getChildren()) {
                    long minutes = minutes_since_meal(meal);
                    Log.d(TAG, meal.getKey());

                    if (!meal.hasChild("e_warn_stat"))
                        if (minutes >= 0) {
                            notif_manager.high_notif(0, getString(R.string.time_to_eat), meal.getKey()+" "+meal.child("type").getValue(String.class));
                            meal.child("e_warn_stat").getRef().setValue(warnings.EARLY);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ",error.toException() );
            }
        });
    }
}
