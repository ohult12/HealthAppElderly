package com.example.healthappelderly;

import static androidx.constraintlayout.widget.StateSet.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ElderlySignUp extends AppCompatActivity {
    String USERNAME_KEY = "ElderApp_Username";
    EditText etFullName, etEmail, etPhone, etAddress, etAllergies, etUsername, etPin;
    Button btnSignUp;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DbLib dbLib;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_sign_up);

        dbRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        etFullName = findViewById(R.id.fullname);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phoneNumber);
        etAddress = findViewById(R.id.address);
        etAllergies = findViewById(R.id.allergies);
        etUsername = findViewById(R.id.username);
        etPin = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.signUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String fullName = String.valueOf(etFullName.getText());
                final String email = String.valueOf(etEmail.getText());
                final String phone = String.valueOf(etPhone.getText());
                final String address = String.valueOf(etAddress.getText());
                final String allergies = String.valueOf(etAllergies.getText());
                final String username = String.valueOf(etUsername.getText());
                    //Set beginning of pincode to 00 to satisfy Firebase min size password req of 6.
                final String pin = "00" + String.valueOf(etPin.getText());

                if(!isFormCorrect(fullName, phone, email, address, username, pin)) {
                    return;
                }

                dbRef.child("Elder").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(username)){
                            Toast.makeText(ElderlySignUp.this, "Username already exists, choose another one", Toast.LENGTH_SHORT).show();

                        } else {
                            mAuth.createUserWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        addElderToDatabase(fullName, phone, email, address, allergies, username);
                                        user =  FirebaseAuth.getInstance().getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {

                                                }
                                            }
                                        });

                                        saveStringLocally(USERNAME_KEY, username);
                                        changeActivity(MainActivity.class);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }
    private void saveStringLocally(String key, String value) {
        SharedPreferences preferences = getSharedPreferences("EldercareApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void changeActivity(Class activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
        finish();
    }

    private boolean isFormCorrect(String name, String phone, String email, String address, String username, String PIN){
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ElderlySignUp.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(PIN)) {
            Toast.makeText(ElderlySignUp.this, "Enter PIN-code", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(name)){
            Toast.makeText(ElderlySignUp.this, "Enter name", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(ElderlySignUp.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(username)) {
            Toast.makeText(ElderlySignUp.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(address)){
            Toast.makeText(ElderlySignUp.this, "Enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void addElderToDatabase(String name, String number, String mail, String address, String allergies, String username) {
        Elderly newElder = new Elderly();
        newElder.setName(name);
        newElder.setMobile_nr(number);
        newElder.setEmail(mail);
        newElder.setAddress(address);
        newElder.setAllergies(allergies);
        //lägg till tom meal (date?)
        //lägg till tom lista över caregivers
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Databas
                dbRef.child("Elder").child(username).setValue(newElder);
                Toast.makeText(ElderlySignUp.this, "New Elder Registered!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlySignUp.this, "ElderlySignUp error" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
