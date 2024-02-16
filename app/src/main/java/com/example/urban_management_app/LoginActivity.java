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

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonHome, buttonForgotPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonHome = findViewById(R.id.buttonHome);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            private int loginAttempts = 0;

            @Override
            public void onClick(View v) {
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

                // only allow the user 5 tries to login successfully
                if (loginAttempts >= 5) {
                    Toast.makeText(LoginActivity.this, "You have exceeded the maximum login attempts. Please try again later.", Toast.LENGTH_SHORT).show();
                    // redirect to HomeActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    return;
                }

                // increment the counter after each try
                loginAttempts++;

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // login success and reset counter
                                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    loginAttempts = 0;

                                    // launch HomeActivity
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                } else {
                                    // login failed
                                    Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return to the MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch PasswordResetActivity
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
            }
        });
    }

    // function for user input validation
    // valid password length + email
    private boolean validateFields() throws IOException {
        boolean valid = true;

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

        return valid;
    }
}
