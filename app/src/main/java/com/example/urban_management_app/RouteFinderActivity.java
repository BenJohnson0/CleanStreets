package com.example.urban_management_app;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RouteFinderActivity extends FragmentActivity implements OnMapReadyCallback {

    // for getting user location
    private FusedLocationProviderClient fusedLocationClient;

    // for origin, destination
    private LatLng ORIGIN;

    // TODO: hide API key
    private String key = "AIzaSyC9LPKCQXaX0xMECbk9y-vEPjwgDjxeuUM";

    // google map implementation
    private GoogleMap mMap;
    private Polyline currentPolyline; // used for plotting the route on the map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finder);

        // TODO: Uncomment after testing
        //getUserLocation();
        getHardcodedLocation();

        getReportCoordinates();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // map setup
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        // set to Dublin
        LatLng startingCamera = new LatLng(53.36254986466739, -6.349820583825544);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingCamera, 12f));

        // apply custom map style
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.map_style);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer, "UTF-8");
            googleMap.setMapStyle(new MapStyleOptions(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: remove for deployment (hardcoded)
    private void getHardcodedLocation() {
        ORIGIN = new LatLng(53.35737638828084, -6.35031412259818);
    }

    // function for getting the current user location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Toast.makeText(RouteFinderActivity.this, "Current Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                            ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(RouteFinderActivity.this, "Location not available. Allow location services to proceed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // function to get the nearest report and plot the route
    private void getReportCoordinates() {
        Query reportsQuery = FirebaseDatabase.getInstance().getReference("reports");

        reportsQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // list to store report coordinates
                List<LatLng> coordinates = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);

                    if (report != null) {
                        // extract coordinates and add to LatLng List
                        LatLng reportLatLng = new LatLng(report.getXCoordinates(), report.getYCoordinates());
                        coordinates.add(reportLatLng);
                    }
                }

                List<LatLng> points = coordinates;

                LatLng nearestPoint = DistanceCalculator.findNearest(ORIGIN, points);
                System.out.println("Nearest Point: " + nearestPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: Handle this
            }
        });
    }

    // TODO: get Directions

    // TODO: make API call

    // TODO: plot on map

}


