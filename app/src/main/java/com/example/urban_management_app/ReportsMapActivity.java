package com.example.urban_management_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private List<Report> reportList;

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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
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
                // Handle the error if needed
            }
        });
    }

    private void plotReportsOnMap() {
        if (googleMap != null) {
            for (Report report : reportList) {
                double latitude = Double.parseDouble(report.getXCoordinates());
                double longitude = Double.parseDouble(report.getYCoordinates());

                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location).title(report.getTitle()));
            }

            if (!reportList.isEmpty()) {
                //dublin coords
                LatLng lastLocation = new LatLng(53.350140, -6.266155);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10f));
            }
        }
    }
}

