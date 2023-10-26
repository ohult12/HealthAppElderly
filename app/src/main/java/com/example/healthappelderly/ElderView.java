package com.example.healthappelderly;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ElderView extends AppCompatActivity {
    TextView loggedInStr, name;
    Button btnLogout;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    Button checkBtn;
    TextView tvMealType, tvMealTime, tvMealComment;
    String USERNAME_KEY = "ElderApp_Username";
    String SHAREDPREF_KEY = "EldercareApp";
    Boolean mealsExist;
    String username;
    RadioGroup rgLanguage;
    RadioButton rbEnglish, rbSwedish;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_view);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        checkBtn = findViewById(R.id.checkBtn);
        tvMealType = findViewById(R.id.tvMealType);
        tvMealTime = findViewById(R.id.tvMealTime);
        tvMealComment = findViewById(R.id.tvMealComment);
        String loginStr, email;
        btnLogout = findViewById(R.id.logoutButton);
        loggedInStr = findViewById(R.id.loggedInUser);
        rgLanguage = findViewById(R.id.radiog1);
        rbEnglish = findViewById(R.id.rb_english1);
        rbSwedish = findViewById(R.id.rb_swedish1);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email = user.getEmail();

        loginStr = String.valueOf(loggedInStr.getText());
        loginStr = loginStr + ": " + email;
        loggedInStr.setText(loginStr);

        //Get username from phone locally
        username = getLocalString(USERNAME_KEY);
        Log.d("SharedPrefs:", username);
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
                    tvMealType.setText(R.string.no_more_meals_today);
                    tvMealTime.setText("");
                    tvMealComment.setText("");
                } else {
                    tvMealType.setText(R.string.no_more_meals_today);
                    tvMealTime.setText("");
                    tvMealComment.setText("");
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ElderView.this);
                builder.setMessage(R.string.do_you_want_to_log_out);
                builder.setTitle(R.string.log_out);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_english1) {
                    LocaleHelper.setLocale(ElderView.this,"en");
                    LocaleHelper.saveLocale(ElderView.this,"en");
                    rbEnglish.setChecked(true);
                    loggedInStr.setText(R.string.logged_in_as);
                    String mailStr = String.valueOf(loggedInStr.getText());
                    mailStr = mailStr + ": " + email;
                    loggedInStr.setText(mailStr);
                    btnLogout.setText(R.string.log_out);
                    checkBtn.setText(R.string.check);
                    tvMealType.setText(R.string.no_meal_planned);
                    name.setText(R.string.meal_reminder);
                    tvMealType.setText(R.string.no_more_meals_today);

                } else if (i == R.id.rb_swedish1) {
                    LocaleHelper.setLocale(ElderView.this, "sv");
                    LocaleHelper.saveLocale(ElderView.this, "sv");
                    rbSwedish.setChecked(true);
                    loggedInStr.setText(R.string.logged_in_as);
                    String mailStr = String.valueOf(loggedInStr.getText());
                    mailStr = mailStr + ": " + email;
                    loggedInStr.setText(mailStr);
                    btnLogout.setText(R.string.log_out);
                    checkBtn.setText(R.string.check);
                    tvMealType.setText(R.string.no_meal_planned);
                    name.setText(R.string.meal_reminder);
                    tvMealType.setText(R.string.no_more_meals_today);
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

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String currentTime = sdfTime.format(new Date());

        boolean ate;
        mealType type = convertStringToMeal(typeStr);

        MealHistoryItem doneMeal = new MealHistoryItem(type, time, cDate, comment, currentTime, true);
        DatabaseReference mealHistoryRef = FirebaseDatabase.getInstance().getReference().child("Elder").child(username).child("meal_history");
        String key = mealHistoryRef.push().getKey();
        mealHistoryRef.child(key).setValue(doneMeal);
        //REMOVES CHILD
        currentElderRef.child(time).setValue(null);

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

                        int enabledColor = ContextCompat.getColor(ElderView.this, R.color.blue);
                        checkBtn.setBackgroundTintList(ColorStateList.valueOf(enabledColor));
                        mealsExist = true;

                    } else {
                        // Handle the case when no meal is found
                        tvMealType.setText(R.string.no_more_meals_today);
                        tvMealTime.setText("");
                        tvMealComment.setText("");
                        mealsExist = false;
                        int disabledColor = ContextCompat.getColor(ElderView.this, R.color.gray);
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