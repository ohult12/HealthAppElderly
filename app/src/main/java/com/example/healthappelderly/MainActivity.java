package com.example.healthappelderly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String PREF_KEY = "ElderApp_User_Email";
    TextView twUserEmail;
    EditText loginCode;
    Button firstLoginBtn, loginBtn, signUpNewElderBtn;
    String email;
    FirebaseAuth mAuth;
    Locale locale;
    RadioGroup rgLanguage;
    RadioButton rbEnglish, rbSwedish;
    TextView welcome;
    TextView info;
    String text;
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
        requestNotificationPermissions();
        Intent intent1 = new Intent(this, ForegroundService.class);
        startService(intent1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twUserEmail = findViewById(R.id.logInAs);
        firstLoginBtn = findViewById(R.id.firstTimeLoginBtn);
        signUpNewElderBtn = findViewById(R.id.signUpNewElder);
        loginBtn = findViewById(R.id.loginButton);
        loginCode = findViewById(R.id.pin);
        mAuth = FirebaseAuth.getInstance();
        rgLanguage = findViewById(R.id.radiog);
        rbEnglish = findViewById(R.id.rb_english);
        rbSwedish = findViewById(R.id.rb_swedish);
        welcome = findViewById(R.id.welcomeText);
        info = findViewById(R.id.infoText);

        email = getLocalString(PREF_KEY);

        String lang = LocaleHelper.getLocale(MainActivity.this);
        LocaleHelper.setLocale(MainActivity.this, lang);
        welcome.setText(R.string.welcome);
        info.setText(R.string.enter_4_digit_pin);
        loginCode.setHint(R.string.input_4_digit_pin);
        loginBtn.setText(R.string.login);

        if(email == null){
            twUserEmail.setText("");
        } else {
            text = String.valueOf(twUserEmail.getText());
            text = text + " " + email;
            twUserEmail.setText(text);
        }
        signUpNewElderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ElderlySignUp.class);
                startActivity(intent);
                finish();
            }
        });

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
                            Toast.makeText(MainActivity.this, R.string.toast_login_successful, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ElderView.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
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
        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_english) {
                    LocaleHelper.setLocale(MainActivity.this,"en");
                    LocaleHelper.saveLocale(MainActivity.this, "en");
                    rbEnglish.setChecked(true);
                    welcome.setText(R.string.welcome);
                    info.setText(R.string.enter_4_digit_pin);
                    loginCode.setHint(R.string.input_4_digit_pin);
                    loginBtn.setText(R.string.login);
                    firstLoginBtn.setText(R.string.login_elderly);
                    signUpNewElderBtn.setText(R.string.sign_up_new_elder);
                    if(email == null){
                        twUserEmail.setText("");
                    } else {
                        twUserEmail.setText(R.string.log_in_as);
                        text = String.valueOf(twUserEmail.getText());
                        text = text + " " + email;
                        twUserEmail.setText(text);
                    }
                } else if (i == R.id.rb_swedish) {
                    LocaleHelper.setLocale(MainActivity.this, "sv");
                    LocaleHelper.saveLocale(MainActivity.this, "sv");
                    rbSwedish.setChecked(true);
                    welcome.setText(R.string.welcome);
                    info.setText(R.string.enter_4_digit_pin);
                    loginCode.setHint(R.string.input_4_digit_pin);
                    loginBtn.setText(R.string.login);
                    firstLoginBtn.setText(R.string.login_elderly);
                    signUpNewElderBtn.setText(R.string.sign_up_new_elder);
                    if(email == null){
                        twUserEmail.setText("");
                    } else {
                        twUserEmail.setText(R.string.log_in_as);
                        text = String.valueOf(twUserEmail.getText());
                        text = text + " " + email;
                        twUserEmail.setText(text);
                    }
                }
            }
        });

    }
    private String getLocalString(String key) {
        SharedPreferences preferences = getSharedPreferences("EldercareApp", Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private boolean isFormCorrect(String personalNbr, String pin) {
        if (TextUtils.isEmpty(personalNbr)){
            Toast.makeText(MainActivity.this, R.string.toast_email_not_correct, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(pin)){
            Toast.makeText(MainActivity.this, R.string.toast_enter_pin_code, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //NOTIFICATIONPART
    private void requestNotificationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.baseline_fastfood_24)
                        .setTitle(R.string.permission_request)
                        .setMessage(R.string.notif_post_perm)
                        .setPositiveButton(R.string.perm_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, AppNotificationManager.NOTIFICATION_POST_CODE);
                            }
                        })
                        .setNegativeButton(R.string.perm_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, AppNotificationManager.NOTIFICATION_POST_CODE);
            }
        }
    }
}
