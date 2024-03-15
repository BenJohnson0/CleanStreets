package com.example.urban_management_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MapSelectionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    static final int REQUEST_MAP_SELECTION = 3; //map selection request code
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

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

        // check if location permission is granted
        getLocationPermission();

        // enable location layer if permission is granted
        if (locationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

        // center the map on the user's location if permission is granted
        if (locationPermissionGranted) {
            getDeviceLocation();
        }

        googleMap.setOnMapClickListener(latLng -> {
            double selectedLatitude = latLng.latitude;
            double selectedLongitude = latLng.longitude;

            // Check if the selected coordinates are inside Dublin city
            if (isInsideDublin(selectedLatitude, selectedLongitude)) {
                // Get the geocoded address for the selected location
                String address = geocodeLocation(selectedLatitude, selectedLongitude);

                // Show a popup asking the user to confirm the geocoded location
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Location");
                builder.setMessage("Selected location:\n" + address + "\n\nDo you want to use this location?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // Pass the selected coordinates and address back to the AddReportActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latitude", selectedLatitude);
                    resultIntent.putExtra("longitude", selectedLongitude);
                    resultIntent.putExtra("address", address);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                });
                builder.show();
            } else {
                // show message popup indicating that the user needs to select a location within Dublin city
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Outside Dublin");
                builder.setMessage("Please select a location within Dublin city.");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }
        });
    }

    // function to geocode latitude and longitude coordinates into an address
    private String geocodeLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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

    // function to check if the selected coordinates are inside Dublin city
    private boolean isInsideDublin(double latitude, double longitude) {
        // define boundaries of Dublin city
        double minLatitude = 53.3064;
        double maxLatitude = 53.4239;
        double minLongitude = -6.4010;
        double maxLongitude = -6.1025;

        // check if the selected coordinates are within the boundaries
        return (latitude >= minLatitude && latitude <= maxLatitude &&
                longitude >= minLongitude && longitude <= maxLongitude);
    }


    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    getDeviceLocation();
                }
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                googleMap.setOnMyLocationChangeListener(location -> {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));
                    googleMap.setOnMyLocationChangeListener(null); // remove listener to prevent multiple updates
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
