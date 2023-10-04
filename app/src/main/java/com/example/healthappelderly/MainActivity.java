package com.example.healthappelderly;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
    EditText loginCode;
    Button firstLoginBtn, loginBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstLoginBtn = findViewById(R.id.firstTimeLoginBtn);
        loginBtn = findViewById(R.id.loginButton);
        loginCode = findViewById(R.id.pin);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pcode;
                pcode = String.valueOf(loginCode.getText());
                if(TextUtils.isEmpty(pcode)){
                    Toast.makeText(MainActivity.this, "Felaktig kod", Toast.LENGTH_SHORT).show();
                } else {

                }
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
}
