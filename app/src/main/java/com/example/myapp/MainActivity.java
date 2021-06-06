package com.example.myapp;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button signout;
    private CountDownTimer timer;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uuid;
    private static final String TAG = "MyDebugTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signout = findViewById(R.id.btn_Logout);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uuid = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        getLoginStatus();

        signout.setOnClickListener((v) -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void getLoginStatus() {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: " + 1/1000);
            }

            @Override
            public void onFinish() {
                timer.start();
                String userID = mAuth.getCurrentUser().getEmail();
                FirebaseFirestore.getInstance().collection("users")
                        .document(userID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snap) {
                                String uniqueID = snap.get("UUID").toString();
                                if (!uniqueID.equals(uuid)) {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(MainActivity.this, "Multiple Device Detected", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }.start();
    }

}