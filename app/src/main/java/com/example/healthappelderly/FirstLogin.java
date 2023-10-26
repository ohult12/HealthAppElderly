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

public class FirstLogin extends AppCompatActivity {
    String EMAIL_KEY = "ElderApp_User_Email";
    String USERNAME_KEY = "ElderApp_Username";
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
                String email, pin;
                email = String.valueOf(etEmail.getText());
                pin = String.valueOf(etPIN.getText());
                pin = "00" + pin;
                if(!isFormCorrect(email, pin)){
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            saveStringLocally(EMAIL_KEY, email);
                            saveStringLocally(USERNAME_KEY, mAuth.getCurrentUser().getDisplayName());

                            Toast.makeText(FirstLogin.this, R.string.toast_login_successful, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ElderView.class));
                            finish();
                        } else {
                            Toast.makeText(FirstLogin.this, R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
                        }
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

    private boolean isFormCorrect(String personalNbr, String pin) {
        if (TextUtils.isEmpty(personalNbr)){
            Toast.makeText(FirstLogin.this, R.string.toast_enter_email, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(pin)){
            Toast.makeText(FirstLogin.this, R.string.toast_enter_pin_code, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}