package com.example.urban_management_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private List<Report> reportList;
    private Button buttonNearest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        reportList = new ArrayList<>();
        loadReportsFromDatabase();

        buttonNearest = findViewById(R.id.buttonNearest);

        buttonNearest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch RegistrationActivity
                startActivity(new Intent(ReportsMapActivity.this, RouteFinderActivity.class));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // load custom map style
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.map_style);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer, "UTF-8");

            // set the map style
            googleMap.setMapStyle(new MapStyleOptions(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReportsFromDatabase() {
        Query reportsQuery = FirebaseDatabase.getInstance().getReference("reports");

        reportsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null) {
                        reportList.add(report);
                    }
                }

                plotReportsOnMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO: handle this
            }
        });
    }

    private Report getReportFromMarker(Marker marker) {
        LatLng markerPosition = marker.getPosition();
        for (Report report : reportList) {
            double latitude = report.getXCoordinates();
            double longitude = report.getYCoordinates();
            LatLng reportPosition = new LatLng(latitude, longitude);
            if (reportPosition.equals(markerPosition)) {
                return report;
            }
        }
        return null; // return null if no matching report is found
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void plotReportsOnMap() {
        if (googleMap != null) {
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null; // return null to use the default info window background
                }

                @SuppressLint("SetTextI18n")
                @Override
                public View getInfoContents(Marker marker) {
                    View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                    TextView titleTextView = infoView.findViewById(R.id.info_window_title);
                    TextView sizeTextView = infoView.findViewById(R.id.info_window_size);
                    TextView timestampTextView = infoView.findViewById(R.id.info_window_timestamp);
                    TextView urgencyTextView = infoView.findViewById(R.id.info_window_urgency);
                    TextView statusTextView = infoView.findViewById(R.id.info_window_status);
                    TextView addressTextView = infoView.findViewById(R.id.info_window_address);

                    titleTextView.setText(marker.getTitle());

                    // get the Report associated with the marker and set data to TextViews
                    Report report = getReportFromMarker(marker);
                    if (report != null) {
                        sizeTextView.setText("Size: " + report.getSize());
                        timestampTextView.setText("Timestamp: " + report.getTimestamp());
                        urgencyTextView.setText("Urgency: " + report.getUrgency());
                        statusTextView.setText("Status: " + report.getStatus());
                        addressTextView.setText("Location: " + geocodeToAddress(report.getXCoordinates(), report.getYCoordinates()));
                    }

                    return infoView;
                }
            });

            for (Report report : reportList) {
                double latitude = report.getXCoordinates();
                double longitude = report.getYCoordinates();

                LatLng location = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title(report.getTitle())
                        .snippet("Size: " + report.getSize() +
                                "\nUrgency: " + report.getUrgency() +
                                "\nTimestamp: " + report.getTimestamp() +
                                "\nStatus: " + report.getStatus() +
                                "\nLocation: " + geocodeToAddress(latitude, longitude));

                googleMap.addMarker(markerOptions);
            }

            if (!reportList.isEmpty()) {
                //TODO: Make map open at location of user (right now its just Dublin hardcoded)
                LatLng lastLocation = new LatLng(53.350140, -6.266155);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 12f));
            }
        }
    }

    private String geocodeToAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // return the first line of the address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // return an empty string if geocoding fails
    }

}

