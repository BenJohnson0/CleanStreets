package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapSelectionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    static final int REQUEST_MAP_SELECTION = 3; //map selection request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // current location (optional, dublin as of now)
        LatLng currentLocation = new LatLng(53.350140, -6.266155);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f));

        googleMap.setOnMapClickListener(latLng -> {
            // Handle the selected location
            double selectedLatitude = latLng.latitude;
            double selectedLongitude = latLng.longitude;

            // Pass the selected coordinates back to the AddReportActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", selectedLatitude);
            resultIntent.putExtra("longitude", selectedLongitude);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
