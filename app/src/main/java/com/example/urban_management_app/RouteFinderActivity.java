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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        ORIGIN = new LatLng(53.34687384010104, -6.265105683680536);
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

    private static String extractPolyline(String jsonResponse) {
        // Parse the JSON response
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        // Check if there are routes in the response
        if (jsonObject.has("routes") && jsonObject.getAsJsonArray("routes").size() > 0) {
            // Extract the polyline string from the "points" property of "overview_polyline"
            JsonObject route = jsonObject.getAsJsonArray("routes").get(0).getAsJsonObject();
            if (route.has("overview_polyline")) {
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");
                if (overviewPolyline.has("points")) {
                    return overviewPolyline.get("points").getAsString();
                }
            }
        }

        // Return an empty string if the polyline is not found
        return "";
    }

    private void findRoute(LatLng origin, LatLng destination) {

        //TODO: hide
        String apiKey = "AIzaSyC9LPKCQXaX0xMECbk9y-vEPjwgDjxeuUM";

        // URL for Google Directions API
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+extractCoordinates(String.valueOf(origin))+"&destination="+
                extractCoordinates(String.valueOf(destination))+"&mode=walking&key="+apiKey;

        //TODO: remove after testing:
        // https://maps.googleapis.com/maps/api/directions/json?origin=53.3455708378654,%20-6.27221675017766&destination=53.34585903227803,%20-6.4062932793566585&mode=walking&key=AIzaSyC9LPKCQXaX0xMECbk9y-vEPjwgDjxeuUM

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

                // extract polyline string
                String polyline = extractPolyline(data);

                List<LatLng> polylinePoints = PolyUtil.decode(polyline);

                // Polyline can cause issues on main thread
                // so, start the process in a separate thread
                new Thread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(polylinePoints);
                    polylineOptions.color(Color.parseColor("#B48D26")); //accent colour
                    polylineOptions.width(15);

                    // markers to show user & report location
                    MarkerOptions startMarkerOptions = new MarkerOptions();
                    startMarkerOptions.position(origin);
                    startMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    MarkerOptions endMarkerOptions = new MarkerOptions();
                    endMarkerOptions.position(destination);
                    endMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    runOnUiThread(() -> {
                        // set camera to Report x,y & add polylines and markers
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f));
                        mMap.addPolyline(polylineOptions);
                        mMap.addMarker(startMarkerOptions);
                        mMap.addMarker(endMarkerOptions);
                    });
                }).start();
            }
        });
    }
}


