package com.example.urban_management_app;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recentReportsRecyclerView;
    private RecentReportsAdapter recentReportsAdapter;
    private List<Report> recentReportsList;

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

        // verify if user is logged in with a non-anonymous account
        if (currentUser != null && !currentUser.isAnonymous()) {
            updateNavigationDrawer(currentUser.getDisplayName(), currentUser.getEmail());
        }
        // otherwise the user is anonymous / guest
        else {
            updateNavigationDrawer("Guest", null);
        }

        // initialize the RecyclerView and set layout
        recentReportsRecyclerView = findViewById(R.id.recent_reports_recycler_view);
        recentReportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // empty list to hold the recent reports
        recentReportsList = new ArrayList<>();

        // instantiate the RecentReportsAdapter and set adapter for the RecyclerView
        recentReportsAdapter = new RecentReportsAdapter(recentReportsList);
        recentReportsRecyclerView.setAdapter(recentReportsAdapter);

        recentReportsAdapter.setOnItemClickListener(new RecentReportsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String reportId) {
                // open DetailedReportActivity when RecyclerView report is clicked (using report id)
                Intent intent = new Intent(HomeActivity.this, DetailedReportActivity.class);
                intent.putExtra("report_id", reportId);
                startActivity(intent);
            }
        });

        // load recent reports and populate RecyclerView
        loadRecentReports();
    }

    //TODO: the reports should show title, size, urgency, timestamp, status, image and (geocoded?) location

    private void loadRecentReports() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        Query recentReportsQuery = reportsRef.limitToLast(20); // fetch the 20 most recent reports

        recentReportsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear existing list of reports
                recentReportsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    // add report to the list
                    recentReportsList.add(report);
                }
                // notify adapter of data change
                recentReportsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: error handling?
                Toast.makeText(HomeActivity.this, "Failed to load recent reports.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNavigationDrawer(String userName, String userEmail) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name_textview);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email_textview);

        // TODO: show name or username?
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);

        Menu navMenu = navigationView.getMenu();

        // show or hide user-specific options based on login status
        if (userEmail != null) {
            // user is logged in
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
            navMenu.findItem(R.id.nav_noticeboard).setVisible(true);
            // hide "Register" & "Login" options
            navMenu.findItem(R.id.nav_register).setVisible(false);
            navMenu.findItem(R.id.nav_login).setVisible(false);
        }
        else {
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
            navMenu.findItem(R.id.nav_noticeboard).setVisible(false);
            // show "Register" & "Login" options
            navMenu.findItem(R.id.nav_register).setVisible(true);
            navMenu.findItem(R.id.nav_login).setVisible(true);
        }
    }

    // handle user choice in a switch statement
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // home
                showToast("Home");
                break;
            case R.id.nav_account:
                // account management (only logged-in users)
                startActivity(new Intent(HomeActivity.this, AccountManagementActivity.class));
                break;
            case R.id.nav_settings:
                // app settings TODO: need to be buttons, not imageview / icon
                showToast("Settings");
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_statistics:
                // statistics dashboard
                startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
                break;
            case R.id.nav_saved_reports:
                // saved reports (only logged-in users)
                showToast("Saved reports");
                break;
            case R.id.nav_add_report:
                // add new report
                startActivity(new Intent(HomeActivity.this, AddReportActivity.class));
                break;
            case R.id.nav_your_reports:
                // user-created reports (only logged-in users)
                startActivity(new Intent(HomeActivity.this, YourReportsActivity.class));
                break;
            case R.id.nav_education:
                // educational resources
                startActivity(new Intent(HomeActivity.this, EducationActivity.class));
                break;
            case R.id.nav_noticeboard:
                // noticeboard
                startActivity(new Intent(HomeActivity.this, NoticeboardActivity.class));
                break;
            case R.id.nav_sign_out:
                // sign out (only logged-in users)
                signOut();
                break;
            case R.id.nav_map:
                // map
                startActivity(new Intent(HomeActivity.this, ReportsMapActivity.class));
                break;
            case R.id.nav_register:
                // register (only anonymous users)
                startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
                break;
            case R.id.nav_login:
                // login (only anonymous users)
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        firebaseAuth.signOut();
        showToast("Signed out");
        // redirect to LoginActivity after sign out
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
