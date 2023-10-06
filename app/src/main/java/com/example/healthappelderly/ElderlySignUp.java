package com.example.healthappelderly;

import static androidx.constraintlayout.widget.StateSet.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ElderlySignUp extends AppCompatActivity {

    EditText etFullName, etEmail, etPhone, etAddress, etAllergies, etPin;
    Button btnSignUp;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    dbLib dbLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_sign_up);
        dbLib = new dbLib(this);
        dbRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        etFullName = findViewById(R.id.fullname);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phoneNumber);
        etAddress = findViewById(R.id.address);
        etAllergies = findViewById(R.id.allergies);
        etPin = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.signUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName, email, phone, address, allergies, pin;
                fullName = String.valueOf(etFullName.getText());
                email = String.valueOf(etEmail.getText());
                phone = String.valueOf(etPhone.getText());
                address = String.valueOf(etAddress.getText());
                allergies = String.valueOf(etAllergies.getText());
                pin = String.valueOf(etPin.getText());
                pin = "00" + pin;

                if(!isFormCorrect(fullName, phone, email, address, pin)) {
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            addElderToDatabase(fullName, phone, email, address, allergies);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });

            }
        });
    }

    private boolean isFormCorrect(String name, String phone, String email, String address, String PIN){
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
        }else if (TextUtils.isEmpty(address)){
            Toast.makeText(ElderlySignUp.this, "Enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void addElderToDatabase(String name, String number, String mail, String address, String allergies) {
        String eUID = mAuth.getUid();
        Elderly newElder = new Elderly();
        newElder.setName(name);
        newElder.setMobile_nr(number);
        newElder.setEmail(mail);
        newElder.setAddress(address);
        newElder.setAllergies(allergies);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Databas
                dbRef.child(eUID).setValue(newElder);
                Log.i("EYO:", "efter inlägg i databas: " + mAuth.getCurrentUser().getEmail());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlySignUp.this, "ElderlySignUp error" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
