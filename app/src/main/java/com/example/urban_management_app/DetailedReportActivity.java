package com.example.urban_management_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;
import android.widget.Toast;

public class DetailedReportActivity extends AppCompatActivity {

    private TextView titleTextView, locationTextView, sizeTextView, urgencyTextView;
    private ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        titleTextView = findViewById(R.id.titleTextView);
        locationTextView = findViewById(R.id.locationTextView);
        sizeTextView = findViewById(R.id.sizeTextView);
        urgencyTextView = findViewById(R.id.urgencyTextView);
        imageView = findViewById(R.id.reportImageView);

        Button amendButton = findViewById(R.id.amendButton);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        // retrieve report_id from the intent
        String reportId = getIntent().getStringExtra("report_id");

        // create a Firebase database reference to the specific report
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId);

        reportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);

                if (report != null) {
                    titleTextView.setText(report.getTitle());

                    // geocode coordinates to get an address
                    String address = geocodeLocation(DetailedReportActivity.this, report.getXCoordinates(), report.getYCoordinates());
                    locationTextView.setText("Near " + address);
                    sizeTextView.setText("The reported size is '" + report.getSize()+"'");
                    urgencyTextView.setText("The reported urgency is '" + report.getUrgency()+"'");

                    Glide.with(DetailedReportActivity.this)
                            .load(report.getImageUrl())
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: handle database read error
            }
        });

        amendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmendOrDeleteDialog(reportId); // pass the reportId to the dialog
            }
        });

        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check if the current user isAdmin
                if (dataSnapshot.exists() && dataSnapshot.child("isAdmin").getValue(String.class).equals("Yes")) {
                    // show amend button
                    amendButton.setVisibility(View.VISIBLE);
                } else {
                    // hide amend button
                    amendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(DetailedReportActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // geocode latitude and longitude coordinates into an address
    private String geocodeLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressText = "Unknown Location";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressText;
    }

    private void showAmendOrDeleteDialog(final String reportId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Report");
        builder.setMessage("Are you sure you want to change, delete or update the status of this report?");
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // start the AmendReportActivity
                Intent intent = new Intent(DetailedReportActivity.this, AmendReportActivity.class);
                intent.putExtra("report_id", reportId);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteReport(reportId);
            }
        });

        builder.setNeutralButton("Change Status", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // start the UpdateStatusActivity
                Intent intent = new Intent(DetailedReportActivity.this, UpdateStatusActivity.class);
                intent.putExtra("report_id", reportId);
                startActivity(intent);
            }
        });

        builder.show();
    }

    // function for deleting the report
    private void deleteReport(String reportId) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId);
        reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get report object
                Report report = dataSnapshot.getValue(Report.class);
                if (report != null && report.getImageUrl() != null && !report.getImageUrl().isEmpty()) {
                    // get the reference to the image
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(report.getImageUrl());

                    // delete the image from storage
                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // image deleted
                            deleteReportFromDatabase(reportId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // failed to delete image
                            Toast.makeText(DetailedReportActivity.this, "Failed to delete report image.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // if no image URL or report object found, just delete the report from the database
                    deleteReportFromDatabase(reportId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle error
                Toast.makeText(DetailedReportActivity.this, "Failed to delete report, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReportFromDatabase(String reportId) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId);
        reportRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetailedReportActivity.this, "Report deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DetailedReportActivity.this, HomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailedReportActivity.this, "Error deleting report, please try again later.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DetailedReportActivity.this, HomeActivity.class));
            }
        });
    }
}
