package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import android.os.Bundle;
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
import java.util.Date;
import java.text.SimpleDateFormat;

public class mealReminder extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListView listView;
    String USERNAME_KEY = "ElderApp_Username";

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mealreminder_elderly);

        listView = findViewById(R.id.listview);

        getDataFromDatabase();

           /* @Override
            public void onClick(View view) {

        }*/

    }
    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences("EldercareApp", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
    //Database
    private void getDataFromDatabase() {
        String username = user.getDisplayName();
        String date = getDateTime();

        String email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference("Elder").child(username).child("Meals").child(date).child("Lunch");
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);
        //Meal meal = new Meal();


        /*
            Skrolldown-menu med olika måltider? Trycka på den måltid man vill åt och lägga till en referens utifrån det. Information kommer upp.
            Eller bara ha en hel sida med all information från "Meals" i databasen för specifik person.
        */

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
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
}
