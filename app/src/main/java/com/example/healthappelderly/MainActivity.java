package com.example.healthappelderly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    String PREF_KEY = "ElderApp_User_Email";
    EditText loginCode;
    Button firstLoginBtn, loginBtn;
    String email;
    FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), ElderView.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = getLocalString();
        firstLoginBtn = findViewById(R.id.firstTimeLoginBtn);
        loginBtn = findViewById(R.id.loginButton);
        loginCode = findViewById(R.id.pin);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pcode;
                pcode = String.valueOf(loginCode.getText());
                pcode = "00" + pcode;
                if(!isFormCorrect(email, pcode)){
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ElderView.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        firstLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FirstLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getLocalString() {
        SharedPreferences preferences = getSharedPreferences("EldercareApp", Context.MODE_PRIVATE);
        return preferences.getString(PREF_KEY, null);
    }

    private boolean isFormCorrect(String personalNbr, String pin) {
        if (TextUtils.isEmpty(personalNbr)){
            Toast.makeText(MainActivity.this, "Email not correct, Log in new Elderly", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(pin)){
            Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
