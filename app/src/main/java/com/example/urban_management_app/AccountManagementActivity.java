package com.example.urban_management_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class AccountManagementActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonUpdate;
    private Button buttonChangePassword;
    private Button buttonDelete;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonUpdate = findViewById(R.id.button_update);
        buttonChangePassword = findViewById(R.id.button_change_password);
        buttonDelete = findViewById(R.id.button_delete);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating account...");

        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            editTextName.setText(name);
            editTextEmail.setText(email);
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void updateAccount() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        if (currentUser != null) {
            currentUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AccountManagementActivity.this,
                                                            "Account updated successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AccountManagementActivity.this,
                                                            "Failed to update account",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(AccountManagementActivity.this,
                                        "Failed to update email",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void changePassword() {
        String newPassword = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Changing password...");
        progressDialog.show();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

