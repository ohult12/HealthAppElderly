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


        setNextMeal(currentDate);
    }

    private void setNextMeal(String cDate) {
        String username = getLocalString(USERNAME_KEY);
        Log.d("user", username);
        Log.d("dateString", cDate);
        Meal nextMeal = new Meal();
        DatabaseReference currentElderRef = rootRef.child("Elder").child(username);
        currentElderRef.child("Meals").child(cDate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Meal meal = task.getResult().child("lunch").getValue(Meal.class);
                    if (meal != null) {
                        tvMealType.setText(meal.getType().toString());
                        tvMealTime.setText(meal.getTime_of_day());
                        tvMealComment.setText(meal.getComment());
                    }
                }

            }

        });
    }

    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences(SHAREDPREF_KEY, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}
