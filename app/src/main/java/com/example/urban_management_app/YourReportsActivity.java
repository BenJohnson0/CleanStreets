package com.example.urban_management_app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        yourReportsAdapter.setOnReportDeleteListener(new YourReportsAdapter.OnReportDeleteListener() {
            @Override
            public void onReportDelete(Report report) {
                YourReportsActivity.this.onReportDelete(report);
            }
        });

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

    public void onReportDelete(Report report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // reference report in the database
                        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(report.getReportId());

                        // delete the report data from the database
                        reportRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // report deleted successfully
                                yourReportsAdapter.deleteReport(report);
                                yourReportsAdapter.notifyDataSetChanged();

                                // check if the report has an image URL associated with it
                                if (report.getImageUrl() != null && !report.getImageUrl().isEmpty()) {
                                    // get the reference to the image
                                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(report.getImageUrl());

                                    // delete the image from storage
                                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // image deleted successfully
                                            Toast.makeText(YourReportsActivity.this, "Report and associated image deleted successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // failed to delete image
                                            Toast.makeText(YourReportsActivity.this, "Failed to delete report image.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // failed to delete report
                                Toast.makeText(YourReportsActivity.this, "Failed to delete report.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel deletion
                    }
                })
                .show();
    }


}
