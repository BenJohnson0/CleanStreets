package com.example.urban_management_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;

public class DetailedReportActivity extends AppCompatActivity {

    private TextView titleTextView, locationTextView, sizeTextView, urgencyTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        titleTextView = findViewById(R.id.titleTextView);
        locationTextView = findViewById(R.id.locationTextView);
        sizeTextView = findViewById(R.id.sizeTextView);
        urgencyTextView = findViewById(R.id.urgencyTextView);
        imageView = findViewById(R.id.reportImageView);

        // Retrieve report_id from the intent
        String reportId = getIntent().getStringExtra("report_id");

        // Create a Firebase database reference to the specific report
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId);

        reportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);

                if (report != null) {
                    titleTextView.setText("Title: " + report.getTitle());

                    // Geocode coordinates to get an address
                    String address = geocodeLocation(DetailedReportActivity.this, report.getXCoordinates(), report.getYCoordinates());
                    locationTextView.setText("Location: " + address);

                    sizeTextView.setText("Size: " + report.getSize());
                    urgencyTextView.setText("Urgency: " + report.getUrgency());

                    Glide.with(DetailedReportActivity.this)
                            .load(report.getImageUrl())
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
            }
        });
    }

    // Geocode latitude and longitude coordinates into an address
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
}
