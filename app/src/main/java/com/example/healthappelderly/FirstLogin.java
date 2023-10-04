package com.example.healthappelderly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class FirstLogin extends AppCompatActivity {

    EditText etEmail, etPIN;
    Button sendBtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login);

        etEmail = findViewById(R.id.etEmail);
        etPIN = findViewById(R.id.etPIN);
        sendBtn = findViewById(R.id.sendVerificationBtn);
        mAuth = FirebaseAuth.getInstance();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String personalNbr, pin;
                personalNbr = String.valueOf(etEmail.getText());
                pin = String.valueOf(etPIN.getText());
                pin = "00" + pin;

                if(!isFormCorrect(personalNbr, pin)){
                    return;
                }
                mAuth.signInWithEmailAndPassword(personalNbr, pin)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(FirstLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), ElderView.class));
                                    finish();
                                } else {
                                    Toast.makeText(FirstLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }

    private boolean isFormCorrect(String personalNbr, String pin) {
        if (TextUtils.isEmpty(personalNbr)){
            Toast.makeText(FirstLogin.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(pin)){
            Toast.makeText(FirstLogin.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}