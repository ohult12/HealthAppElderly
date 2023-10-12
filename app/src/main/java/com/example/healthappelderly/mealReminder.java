package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import android.os.Bundle;
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
        setMeal("Breakfast");

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDatabase();
                //change_meal(getTime(), mealTime);
                String change_meal = change_meal_view(meal);
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
        Toast.makeText(mealReminder.this, "Tid: " + time, Toast.LENGTH_SHORT).show();


        String email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Elder").child(username).child("Meals").child(date).child(meals);
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        //Meal meal = new Meal();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                list.add(snapshot.getKey().toString());
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(dataSnapshot.getValue().toString());
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
        Date d=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);
        return currentDateTimeString;
    }

    private void change_meal(String time, String mealtime) {
        if(time.compareTo(mealtime) > mealtime.compareTo(time)) {
            change_meal_view(getMeal());
        } else {
            Toast.makeText(mealReminder.this, "Time for this meal is not over yet", Toast.LENGTH_SHORT).show();

        }
    }

    private String change_meal_view(String meal) {
        switch(meal) {
            case "Breakfast":
                meal = "Small meal";
                break;
            case "Small meal":
                meal = "Lunch";
                break;
            case "Lunch":
                meal = "Dinner";
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
}
