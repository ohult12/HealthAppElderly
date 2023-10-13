package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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

import java.text.ParseException;
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

        //Get username from phone locally

        username = getLocalString(USERNAME_KEY);
        if(username == null){
            reAuthUser();
        }


        //Get todays date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(new Date());

        mealsExist = false;

        //call to show next meal
        setNextMeal(currentDate, currentTime);
        checkBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mealsExist) {
                    sendMealToHistory(currentDate, currentTime);
                    tvMealType.setText("No more Meals today!");
                    tvMealTime.setText("");
                    tvMealComment.setText("");
                } else {
                    tvMealType.setText("No more Meals today!");
                    tvMealTime.setText("");
                    tvMealComment.setText("");
                }
            }
        });
    }

    private void reAuthUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        username = user.getDisplayName();
        Log.d("username from auth", username);
        if(username == null) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void sendMealToHistory(String cDate, String cTime) {
        DatabaseReference currentElderRef = rootRef.child("Elder").child(username).child("Meals").child(cDate);

        String comment, typeStr, time;
        time = String.valueOf(tvMealTime.getText());
        typeStr = String.valueOf(tvMealType.getText());
        comment = String.valueOf(tvMealComment.getText());

        boolean ate;
        mealType type = convertStringToMeal(typeStr);

        MealHistoryItem doneMeal = new MealHistoryItem(type, time, cDate, comment, cTime, true);

        //REMOVES CHILD
        currentElderRef.child(time).removeValue();

    }

    private void setNextMeal(String cDate, String cTime) {
        Log.d("dateString", cDate);
        Log.d("timeString", cTime);

        DatabaseReference currentElderRef = rootRef.child("Elder").child(username).child("Meals").child(cDate);

        currentElderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("timeStringOnEvent", cTime);
                if (dataSnapshot.exists()) {
                    Meal nextMeal = null;
                    Log.d("datasnapshot", dataSnapshot.getValue().toString());
                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        Log.d("datasnapshot_children", mealSnapshot.getValue().toString());
                        //Hämta Meal ur databas
                        Meal meal = mealSnapshot.getValue(Meal.class);
                        if (meal != null) {
                            String mealTime = meal.getTime_of_day();

                            // Compare the time_of_day in the meal with the current time
                            if (mealTime != null /*&& mealTime.compareTo(cTime) > 0*/) {
                                //Now the next meal is only showed if the user has pressed CHECK
                                //mealTime.compareTo(cTime) does not update unless activity is reloaded (or database node of cDate)
                                //Calculate time 45 minutes ahead of mealTime.
                                setAlert(mealTime);

                                nextMeal = meal;
                                break; // Exit the loop when the next meal is found
                            }
                        }
                    }
                    //Set meal info
                    if (nextMeal != null) {

                        tvMealType.setText(nextMeal.getType().toString().toUpperCase());
                        tvMealTime.setText(nextMeal.getTime_of_day());
                        tvMealComment.setText(nextMeal.getComment());

                        int enabledColor = ContextCompat.getColor(mealReminder.this, R.color.blue);
                        checkBtn.setBackgroundTintList(ColorStateList.valueOf(enabledColor));
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

    private void setAlert(String mealTime) {
        Calendar alertTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            Date dMealTime = timeFormat.parse(mealTime);
            alertTime.setTime(dMealTime);
            alertTime.add(Calendar.MINUTE, 45);

        } catch (ParseException e){
            e.printStackTrace();
        }
    }


    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences(SHAREDPREF_KEY, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
    public static mealType convertStringToMeal(String mealString){
        try {
            return mealType.valueOf(mealString.toLowerCase());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid meal string: " + mealString);
        }
    }
}
