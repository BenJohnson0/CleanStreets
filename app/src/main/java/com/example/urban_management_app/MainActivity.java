package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonRegister, buttonLogin, buttonAnon, route_finder_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonAnon = findViewById(R.id.anonymous_login_button);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch RegistrationActivity
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        buttonAnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call signInAnonymously() function
                signInAnonymously();
            }
        });
    }

    // function that allows anonymous sign-in from Firebase
    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // anonymous login successful
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // anonymous login failed
                        Toast.makeText(MainActivity.this, "Anonymous login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


