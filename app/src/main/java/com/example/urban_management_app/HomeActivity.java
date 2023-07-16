package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in
            updateNavigationDrawer(currentUser.getDisplayName(), currentUser.getEmail());
        } else {
            // User is not logged in
            updateNavigationDrawer("Anon", null);
        }
    }

    private void updateNavigationDrawer(String userName, String userEmail) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name_textview);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email_textview);

        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);

        Menu navMenu = navigationView.getMenu();

        // Show/hide user-specific options based on login status
        if (userEmail != null) {
            // User is logged in
            navMenu.findItem(R.id.nav_home).setVisible(true);
            navMenu.findItem(R.id.nav_add_report).setVisible(true);
            navMenu.findItem(R.id.nav_statistics).setVisible(true);
            navMenu.findItem(R.id.nav_saved_reports).setVisible(true);
            navMenu.findItem(R.id.nav_your_reports).setVisible(true);
            navMenu.findItem(R.id.nav_map).setVisible(true);
            navMenu.findItem(R.id.nav_education).setVisible(true);
            navMenu.findItem(R.id.nav_account).setVisible(true);
            navMenu.findItem(R.id.nav_settings).setVisible(true);
            navMenu.findItem(R.id.nav_sign_out).setVisible(true);
            navMenu.findItem(R.id.nav_register).setVisible(false); // Hide "Register" option
            navMenu.findItem(R.id.nav_login).setVisible(false); // Hide "Login" option
        } else {
            navMenu.findItem(R.id.nav_home).setVisible(true);
            navMenu.findItem(R.id.nav_add_report).setVisible(true);
            navMenu.findItem(R.id.nav_statistics).setVisible(true);
            navMenu.findItem(R.id.nav_saved_reports).setVisible(false);
            navMenu.findItem(R.id.nav_your_reports).setVisible(false);
            navMenu.findItem(R.id.nav_map).setVisible(true);
            navMenu.findItem(R.id.nav_education).setVisible(true);
            navMenu.findItem(R.id.nav_account).setVisible(false);
            navMenu.findItem(R.id.nav_settings).setVisible(true);
            navMenu.findItem(R.id.nav_sign_out).setVisible(false);
            navMenu.findItem(R.id.nav_register).setVisible(true); // show "Register" option
            navMenu.findItem(R.id.nav_login).setVisible(true); // show "Login" option
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // Handle Home option
                showToast("Home selected");
                break;
            case R.id.nav_account:
                // Handle Account management option (visible only to logged-in users)
                showToast("Account selected");
                break;
            case R.id.nav_settings:
                // Handle App settings option
                showToast("Settings selected");
                break;
            case R.id.nav_statistics:
                // Handle View statistics dashboard option
                showToast("Statistics selected");
                break;
            case R.id.nav_saved_reports:
                // Handle View saved reports option (visible only to logged-in users)
                showToast("Saved reports selected");
                break;
            case R.id.nav_add_report:
                // Handle Add new report option
                startActivity(new Intent(HomeActivity.this, AddReportActivity.class));
                break;
            case R.id.nav_your_reports:
                // Handle User-created reports option (visible only to logged-in users)
                showToast("User reports selected");
                break;
            case R.id.nav_education:
                // Handle User-created reports option (visible only to logged-in users)
                showToast("Education selected");
                break;
            case R.id.nav_sign_out:
                // Handle Sign out option (visible only to logged-in users)
                signOut();
                break;
            case R.id.nav_map:
                // Handle View saved reports option (visible only to logged-in users)
                showToast("Map selected");
                break;
            case R.id.nav_register:
                // Handle View saved reports option (visible only to anonymous users)
                showToast("Register selected");
                break;
            case R.id.nav_login:
                // Handle View saved reports option (visible only to anonymous users)
                showToast("Login selected");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        firebaseAuth.signOut();
        showToast("Signed out");
        // Redirect to LoginActivity or any other desired activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
