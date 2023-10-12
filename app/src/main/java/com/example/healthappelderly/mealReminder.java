package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.Toast;

public class mealReminder extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbTime;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListView listView;
    Button checkBtn;
    String USERNAME_KEY = "ElderApp_Username";
    private String meal;
    private String mealTime;


    @Override
    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mealreminder_elderly);

        listView = findViewById(R.id.listview);
        checkBtn = findViewById(R.id.checkBtn);
        //getTime();
        setMeal("breakfast");
        //getDataFromDatabase();

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDatabase();
                //change_meal(getTime(), getMeal());
                String change_meal = change_meal_view(getMeal());//getTime(), getMeal());
                setMeal(change_meal);
            }
        });

    }
    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences("EldercareApp", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
    //Database
    private void getDataFromDatabase() {
        String username = user.getDisplayName();
        String date = "2023-10-11"; //getDateTime();
        String meals = getMeal();
        String time = getTime();

        String email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Elder").child(username).child("Meals").child(date).child(getMeal());
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        //Meal meal = new Meal();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                //list.add(snapshot.getKey().toString());
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue().toString());
                    //setMeal(String.valueOf(snapshot.child("type").getValue(String.class)));
                    mealTime = String.valueOf(snapshot.child("time_of_day").getValue(String.class));
                    setMealTime(mealTime);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getTime() {
        SimpleDateFormat sdf=new SimpleDateFormat("HH mm");
        String currentDateTimeString = sdf.format(new Date());
        Toast.makeText(mealReminder.this, "Timeeesss: " + currentDateTimeString, Toast.LENGTH_SHORT).show();
        return currentDateTimeString;
    }

    /*private String change_meal(String time, String meal) {
        if(time.compareTo(getMealTime()) > 0) {
            Toast.makeText(mealReminder.this, "Time meal: " + getMealTime(), Toast.LENGTH_SHORT).show();
            meal = change_meal_view(getMeal());
        } else {
            Toast.makeText(mealReminder.this, "Time for this meal is not over yet", Toast.LENGTH_SHORT).show();
        }
        return meal;
    }*/

    private String change_meal_view(String meal) {
        switch(meal) {
            case "breakfast":
                meal = "snack";
                break;
            case "snack":
                meal = "lunch";
                break;
            case "lunch":
                meal = "dinner";
                break;
            default:
                meal = "No more meals today";
                break;
        }
        return meal;
    }

    public String getMeal() {
        return meal;
    }
    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
}
