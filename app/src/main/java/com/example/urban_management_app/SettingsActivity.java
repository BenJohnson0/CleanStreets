package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout accountOption = findViewById(R.id.accountOption);
        LinearLayout notificationsOption = findViewById(R.id.notificationsOption);
        LinearLayout accessibilityOption = findViewById(R.id.accessibilityOption);

        // set a click listener for the Account option
        accountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch AccountManagementActivity
                showToast("Account");
                Intent intent = new Intent(SettingsActivity.this, AccountManagementActivity.class);
                startActivity(intent);
            }
        });

        // set a click listener for the Accessibility option (placeholder)
        accessibilityOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Launch the AccessibilityActivity (which is currently blank)
                showToast("Accessibility");
                Intent intent = new Intent(SettingsActivity.this, AccessibilityActivity.class);
                startActivity(intent);
            }
        });


        notificationsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Launch the NotificationsActivity (which is currently blank)
                showToast("Notifications");
                Intent intent = new Intent(SettingsActivity.this, AccessibilityActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
