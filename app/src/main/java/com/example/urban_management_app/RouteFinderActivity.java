package com.example.urban_management_app;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.maps.model.EncodedPolyline;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RouteFinderActivity extends FragmentActivity implements OnMapReadyCallback {

    // for origin location
    private LatLng ORIGIN;

    // google map implementation
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finder);

        // request or access user location before calling functions
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // TODO: Uncomment after testing
        //getUserLocation();
        getHardcodedLocation(); //WALKING results will break unless its possible, so use this for now

        getReportCoordinates();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    private void getUserLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());
                            //TODO: remove toast when fixed
                            showToast(String.valueOf(ORIGIN));
                        } else {
                            Toast.makeText(this, "Location not available. Allow location services to proceed.", Toast.LENGTH_SHORT).show();
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

                // prepare list of coordinates for formula
                List<LatLng> points = coordinates;

                // execute formula and print result to console (debugging)
                LatLng nearestPoint = DistanceCalculator.findNearest(ORIGIN, points);

                System.out.println("Origin: " + extractCoordinates(String.valueOf(ORIGIN)));
                System.out.println("Nearest Point: " + extractCoordinates(String.valueOf(nearestPoint)));

                findRoute(ORIGIN, nearestPoint);
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

    //TODO: remove, just for debugging
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // for extracting just the coordinate values from the lat/lng object
    String extractCoordinates(String inputString) {
        String regex = "(lat\\/lng\\:\\s*)?\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);

        String coordinates = "";
        if (matcher.find()) {
            coordinates = matcher.group(2);
        }
        return coordinates;
    }

    private void findRoute(LatLng origin, LatLng destination) {

        //TODO: remove: https://maps.googleapis.com/maps/api/directions/json?origin=53.35737638828084,-6.35031412259818&destination=53.36254986466739,-6.349820583825544&mode=walking&key=AIzaSyC9LPKCQXaX0xMECbk9y-vEPjwgDjxeuUM THIS WORKS

        //TODO: hide
        String apiKey = "AIzaSyC9LPKCQXaX0xMECbk9y-vEPjwgDjxeuUM";

        // URL for Google Directions API
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+extractCoordinates(String.valueOf(origin))+"&destination="+
                extractCoordinates(String.valueOf(destination))+"&mode=walking&key="+apiKey;

        // testing url, works fine (once both points are in dublin)
        //System.out.println("url: " + url);

        // make a GET request to Google Directions API with OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // make the request and input url
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RouteFinderActivity", "Failed to connect to Google Directions API", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                // testing data output from http request, working fine
                //System.out.println("data: " + data);

                //TODO: some issue AFTER THIS POINT related to parsing the response

                /*
                Gson gson = new Gson();
                DirectionsRoute directionsResult = gson.fromJson(data, DirectionsRoute.class);

                for (DirectionsLeg leg : directionsResult.legs) {
                    for (DirectionsStep step : leg.steps) {
                        String polyline = step.polyline.encodedPolyline;
                        List<LatLng> polylinePoints = EncodedPolyline.decode(polyline);

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(polylinePoints);
                        polylineOptions.color(Color.BLUE);
                        polylineOptions.width(4);

                        Polyline polyline = mMap.addPolyline(polylineOptions);
                    }
                }
                 */
            }
        });
    }
}


