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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

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

        //load custom map style
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.map_style);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String json = new String(buffer, "UTF-8");

            // Set the map style
            googleMap.setMapStyle(new MapStyleOptions(json));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: Make map open at location of user (right now its just Dublin hardcoded)
        LatLng currentLocation = new LatLng(53.350140, -6.266155);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

        googleMap.setOnMapClickListener(latLng -> {
            double selectedLatitude = latLng.latitude;
            double selectedLongitude = latLng.longitude;

            //pass the selected coordinates back to the AddReportActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", selectedLatitude);
            resultIntent.putExtra("longitude", selectedLongitude);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
