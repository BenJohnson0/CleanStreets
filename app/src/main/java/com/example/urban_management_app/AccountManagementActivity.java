package com.example.urban_management_app;

// necessary imports
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class AccountManagementActivity extends AppCompatActivity {

    // declare UI and Firebase elements
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        // initialize UI and Firebase elements
        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);

        Button buttonUpdate = findViewById(R.id.button_update);
        Button buttonChangePassword = findViewById(R.id.button_change_password);
        Button buttonDelete = findViewById(R.id.button_delete);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating account...");

        // check if currentUser is not null and set name and email
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            editTextName.setText(name);
            editTextEmail.setText(email);
        }

        // method to update user account information
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateAccount();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // method to change user password
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        // method for deleting an account
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void updateAccount() throws IOException {
        // get username and email
        String newUsername = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        // check if any field is empty
        if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // bad words check
        BadWordsFilter badWordsFilter = new BadWordsFilter(getResources().getAssets().open("bad-words.txt"));

        if (badWordsFilter.containsSwearWord(newUsername)) {
            Toast.makeText(this, "Name value contains inappropriate language", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        if (currentUser != null) {
            // update email
            currentUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // update username
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUsername)
                                        .build();

                                currentUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // update username in users database
                                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                                                    String userId = currentUser.getUid();
                                                    userRef.child(userId).child("username").setValue(newUsername)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressDialog.dismiss();
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(AccountManagementActivity.this,
                                                                                "Account Updated. Sign-in again to view changes",
                                                                                Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        Toast.makeText(AccountManagementActivity.this,
                                                                                "Failed to update username",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AccountManagementActivity.this,
                                                            "Failed to update username",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AccountManagementActivity.this,
                                        "Failed to update email",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void changePassword() {
        // get new password
        String newPassword = editTextPassword.getText().toString().trim();

        // check if new password is empty
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Changing password...");
        progressDialog.show();

        // update password if user exists
        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountManagementActivity.this,
                                        "Password changed successfully",
                                        Toast.LENGTH_SHORT).show();
                                editTextPassword.setText("");
                            } else {
                                Toast.makeText(AccountManagementActivity.this,
                                        "Failed to change password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteAccount() {
        progressDialog.setMessage("Deleting account...");
        progressDialog.show();

        if (currentUser != null) {
            currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountManagementActivity.this,
                                        "Account deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut(); //sign out user

                                //return to MainActivity
                                Intent intent = new Intent(AccountManagementActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AccountManagementActivity.this,
                                        "Failed to delete account",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

