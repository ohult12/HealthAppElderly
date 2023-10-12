package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class mealReminder extends AppCompatActivity {
    DatabaseReference rootRef;
    Button checkBtn;
    TextView tvMealType, tvMealTime, tvMealComment;
    String USERNAME_KEY = "ElderApp_Username";
    String SHAREDPREF_KEY = "EldercareApp";
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mealreminder_elderly);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        checkBtn = findViewById(R.id.checkBtn);
        tvMealType = findViewById(R.id.tvMealType);
        tvMealTime = findViewById(R.id.tvMealTime);
        tvMealComment = findViewById(R.id.tvMealComment);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(new Date());

        setNextMeal(currentDate, currentTime);
    }

    private void setNextMeal(String cDate, String cTime) {
        String username = getLocalString(USERNAME_KEY);
        Log.d("user", username);
        Log.d("dateString", cDate);
        Log.d("timeString", cTime);

        DatabaseReference currentElderRef = rootRef.child("Elder").child(username).child("Meals").child(cDate);

        currentElderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Meal nextMeal = null;
                    Log.d("datasnapshot", dataSnapshot.getValue().toString());
                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        Log.d("datasnapshot_children", mealSnapshot.getValue().toString());
                        Meal meal = mealSnapshot.getValue(Meal.class);
                        if (meal != null) {
                            String mealTime = meal.getTime_of_day();

                            // Compare the time_of_day in the meal with the current time
                            if (mealTime != null && mealTime.compareTo(cTime) > 0) {
                                nextMeal = meal;
                                break; // Exit the loop when the next meal is found
                            }
                        }
                    }

                    if (nextMeal != null) {
                        tvMealType.setText(nextMeal.getType().toString());
                        tvMealTime.setText(nextMeal.getTime_of_day());
                        tvMealComment.setText(nextMeal.getComment());
                        checkBtn.setActivated(true);
                    } else {
                        // Handle the case when no meal is found
                        tvMealType.setText("No more Meals today!");
                        tvMealTime.setText("");
                        tvMealComment.setText("");
                        checkBtn.setActivated(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }


    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences(SHAREDPREF_KEY, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}
