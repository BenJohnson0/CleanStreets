package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister, buttonReturn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonReturn = findViewById(R.id.buttonReturn);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // validate user inputs
                try {
                    if (!validateFields()) {
                        return;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // register the user with Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // get the generated user ID
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    // store user details in user profile database
                                    User user = new User(username, email);
                                    FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(user);

                                    // registration success
                                    Toast.makeText(RegistrationActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                    // launch HomeActivity
                                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                } else {
                                    // registration failed
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return to MainActivity
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }

    // function for user input validation
    // valid password length + email
    private boolean validateFields() throws IOException {
        boolean valid = true;

        String username = editTextUsername.getText().toString().trim();
        if (username.isEmpty()) {
            editTextUsername.setError("Please enter your username");
            valid = false;
        }

        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter your email address");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            valid = false;
        }

        String password = editTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            editTextPassword.setError("Please enter your password");
            valid = false;
        } else if (password.length() < 8) {
            editTextPassword.setError("Password must be at least 8 characters long");
            valid = false;
        }

        // bad words check
        BadWordsFilter badWordsFilter = new BadWordsFilter(getResources().getAssets().open("bad-words.txt"));

        if (badWordsFilter.containsSwearWord(username)) {
            Toast.makeText(this, "Do not use inappropriate language, try again.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}


