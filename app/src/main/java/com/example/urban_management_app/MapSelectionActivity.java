package com.example.urban_management_app;

import static com.example.urban_management_app.AddReportActivity.REQUEST_IMAGE_CAPTURE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker at the current location (optional)
        LatLng currentLocation = new LatLng(53.3607, 6.2511);
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));

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
