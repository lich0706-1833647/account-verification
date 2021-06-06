package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private Button signout;
    private CountDownTimer timer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mStore;
    private DocumentReference ref;
    private String uuid;
    private static final String TAG = "MyDebugTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signout = findViewById(R.id.btn_Logout);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        String emailAcount = mUser.getEmail();
        ref = mStore.collection("users").document(emailAcount);
        uuid = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot >() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String uniqueID = snapshot.get("UUID").toString();
                    if (!uniqueID.equals(uuid)) {
                        setSignout();
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }

            }
        });

        signout.setOnClickListener((v) -> {
            setSignout();
        });
    }


    protected void setSignout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}