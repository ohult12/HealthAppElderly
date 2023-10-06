package com.example.healthappelderly;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DbLib {

    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private Context context;
    public final String Elder = "Elder";
    public final String Caregiver = "Caregiver";
    public DbLib(Context context) {
        this.context = context;
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }
}




