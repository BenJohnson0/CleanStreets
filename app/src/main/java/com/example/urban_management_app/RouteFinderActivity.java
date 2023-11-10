package com.example.urban_management_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.urban_management_app.R;
import com.example.urban_management_app.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RouteFinderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnFindReport;
    private TextView textView;
    private FusedLocationProviderClient fusedLocationClient;

    private DatabaseReference reportsRef;
    private List<Report> reportList = new ArrayList<>();

    //TODO: focus map on dublin city at launch
    // route finder to nearest report for now
    // scope for nearest 3-5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finder);

        btnFindReport = findViewById(R.id.btnFindReport);
        textView = findViewById(R.id.textView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Request location permission and initialize the fused location client
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Firebase references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reportsRef = database.getReference("reports");

        btnFindReport.setOnClickListener(view -> findNearestReportLocation());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void findNearestReportLocation() {
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
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double userLatitude = location.getLatitude();
                double userLongitude = location.getLongitude();

                // Calculate the nearest report location
                LatLng nearestReportLocation = getNearestReportLocation(userLatitude, userLongitude);

                // Display the nearest report location on the map
                mMap.clear(); // Clear any previous markers
                mMap.moveCamera(CameraUpdateFactory.newLatLng(nearestReportLocation));
                mMap.addMarker(new MarkerOptions().position(nearestReportLocation).title("Nearest Report Location"));
            }
        });
    }

    private LatLng getNearestReportLocation(double userLatitude, double userLongitude) {
        // Implement the logic to find the nearest report location from your reportList
        // You can calculate the distances using the Haversine formula
        // For simplicity, we'll assume the first report is the nearest
        double nearestLat = reportList.get(0).getXCoordinates();
        double nearestLng = reportList.get(0).getYCoordinates();
        LatLng nearestLocation = new LatLng(nearestLat, nearestLng);

        // You should iterate through your reportList and calculate the distances
        // to find the actual nearest report location
        // Here, we just assume the first report is the nearest

        return nearestLocation;
    }
}
