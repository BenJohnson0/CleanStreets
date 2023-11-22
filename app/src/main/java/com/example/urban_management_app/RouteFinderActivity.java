package com.example.urban_management_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RouteFinderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient; // for getting user location
    private LatLng userLatLng;
    private List<Report> reportList = new ArrayList<>();
    private boolean isUserLocationAvailable = false;
    private boolean areReportsAvailable = false;
    private Button buttonFindNearest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finder);

        buttonFindNearest = findViewById(R.id.btnFindReport); // declare the button

        // create SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();

        // on click listener for the nearest report button
        buttonFindNearest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get user location x1,y1
                getUserLocation();

                // get report location x2,y2
                getReportCoordinates();

                // call Directions API for (x1,y1) and (x2,y2)

                // display on the map

            }
        });
    }

    // function for getting the current user location
    private void getUserLocation() {
        // check if user has allowed location check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) { //TODO: remove current location toast
                            Toast.makeText(RouteFinderActivity.this, "Current Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                            userLatLng = new LatLng(location.getLatitude(), location.getLongitude()); // store user coordinates
                        } else {
                            Toast.makeText(RouteFinderActivity.this, "Location not available. Allow location services in order to proceed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // currently cycles through all reports, only need the nearest
    private List<LatLng> getReportCoordinates() {
        List<LatLng> coordinates = new ArrayList<>();

        Query reportsQuery = FirebaseDatabase.getInstance().getReference("reports");

        reportsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coordinates.clear(); // assuming coordinates is a List<LatLng>

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);

                    if (report != null) { //TODO: check here for the nearest report to user
                        LatLng reportLatLng = new LatLng(report.getXCoordinates(), report.getYCoordinates());
                        Toast.makeText(RouteFinderActivity.this, "Report Location: " + reportLatLng.latitude + ", " + reportLatLng.longitude, Toast.LENGTH_SHORT).show();
                        coordinates.add(reportLatLng);
                    } else {
                        Toast.makeText(RouteFinderActivity.this, "No coordinates in report.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: Handle this
            }
        });

        return coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LatLng currentLocation = new LatLng(53.350140, -6.266155);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));
        mMap.setMyLocationEnabled(true); // uncomment to show user location on the map, san jose USA
    }
}


