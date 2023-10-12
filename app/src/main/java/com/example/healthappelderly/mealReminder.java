package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
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
    Boolean mealsExist;
    String username;

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

        username = getLocalString(USERNAME_KEY);
        Log.d("sharedpref user", username);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(new Date());

        mealsExist = false;
        setNextMeal(currentDate, currentTime);
        checkBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mealsExist) {
                    sendMealToHistory(currentDate, currentTime);
                }
            }
        });
    }

    private void sendMealToHistory(String cDate, String cTime) {
        DatabaseReference currentElderRef = rootRef.child("Elder").child(username).child("Meals").child(cDate);


    }

    private void setNextMeal(String cDate, String cTime) {
        Log.d("dateString", cDate);
        Log.d("timeString", cTime);

        DatabaseReference currentElderRef = rootRef.child("Elder").child(username).child("Meals").child(cDate);

        currentElderRef.addValueEventListener(new ValueEventListener() {
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
                    //Set meal info
                    if (nextMeal != null) {

                        tvMealType.setText(nextMeal.getType().toString());
                        tvMealTime.setText(nextMeal.getTime_of_day());
                        tvMealComment.setText(nextMeal.getComment());

                        checkBtn.setBackgroundTintList(null);
                        mealsExist = true;

                    } else {
                        // Handle the case when no meal is found
                        tvMealType.setText("No more Meals today!");
                        tvMealTime.setText("");
                        tvMealComment.setText("");
                        mealsExist = false;
                        int disabledColor = ContextCompat.getColor(mealReminder.this, R.color.gray);
                        checkBtn.setBackgroundTintList(ColorStateList.valueOf(disabledColor));

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
