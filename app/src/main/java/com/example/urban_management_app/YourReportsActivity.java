package com.example.urban_management_app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YourReportsActivity extends AppCompatActivity {

    private RecyclerView yourReportsRecyclerView;
    private TextView emptyReportsTextView;

    YourReportsAdapter yourReportsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_reports);

        // set RecyclerView and TextView
        yourReportsRecyclerView = findViewById(R.id.your_reports_recycler_view);
        emptyReportsTextView = findViewById(R.id.empty_reports_text_view);

        yourReportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create adapter
        yourReportsAdapter = new YourReportsAdapter(new ArrayList<Report>());

        // set adapter on RecyclerView
        yourReportsRecyclerView.setAdapter(yourReportsAdapter);

        // retrieve reports from Firebase and update adapter
        retrieveAndShowYourReports();
    }

    private void retrieveAndShowYourReports() {
        // retrieve from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reportsRef = database.getReference("reports");

        // filter reports created by the logged-in user
        Query yourReportsQuery = reportsRef.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        yourReportsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // initialise the list of reports
                ArrayList<Report> yourReports = new ArrayList<>();

                // iterate and add each report to the list
                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    yourReports.add(report);
                }

                // update the adapter with the filtered report list
                yourReportsAdapter.setReportList(yourReports);

                // hide empty reports if required
                if (yourReports.size() > 0) {
                    emptyReportsTextView.setVisibility(View.GONE);
                } else {
                    emptyReportsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: Handle error
            }
        });
    }
}
