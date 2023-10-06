package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ElderlySignUp extends AppCompatActivity {

    EditText etFullName, etEmail, etPhone, etAddress, etAllergies, etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_sign_up);


    }
}