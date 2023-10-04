package com.example.healthappelderly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class ElderView extends AppCompatActivity {
    TextView loggedInStr;
    Button btnLogout;
    FirebaseAuth mAuth;
    TextView elder;
    RadioGroup rgLanguage;
    RadioButton rbEnglish, rbSwedish;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_view);

        String loginStr, email;
        btnLogout = findViewById(R.id.logoutButton);
        loggedInStr = findViewById(R.id.loggedInUser);
        elder = findViewById(R.id.header_text);
        rgLanguage = findViewById(R.id.radiog1);
        rbEnglish = findViewById(R.id.rb_english1);
        rbSwedish = findViewById(R.id.rb_swedish1);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email = user.getEmail();

        loginStr = String.valueOf(loggedInStr.getText());
        loginStr = loginStr + email;
        loggedInStr.setText(loginStr);

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_english1) {
                    LocaleHelper.setLocale(ElderView.this,"en");
                    rbEnglish.setChecked(true);
                    loggedInStr.setText(R.string.logged_in_as);
                    elder.setText(R.string.elder_view);
                    btnLogout.setText(R.string.log_out);
                } else if (i == R.id.rb_swedish1) {
                    LocaleHelper.setLocale(ElderView.this, "sv");
                    rbSwedish.setChecked(true);
                    loggedInStr.setText(R.string.logged_in_as);
                    elder.setText(R.string.elder_view);
                    btnLogout.setText(R.string.log_out);
                }
            }
        });
    }
}