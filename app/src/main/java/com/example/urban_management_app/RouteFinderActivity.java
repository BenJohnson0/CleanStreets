package com.example.urban_management_app;

// necessary imports
import static java.nio.charset.StandardCharsets.UTF_8;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
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

        // initial check for location services
        checkLocationServiceEnabled();

        // request or access user location before calling functions
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getUserLocation();
        //getHardcodedLocation(); //WALKING results will break unless its possible, so use this for now

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
            String json = new String(buffer, UTF_8);
            googleMap.setMapStyle(new MapStyleOptions(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHardcodedLocation() {
        ORIGIN = new LatLng(53.34687384010104, -6.265105683680536);
    }

    // function for getting the current user location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());
                            getReportCoordinates(); // Call getReportCoordinates() here
                        } else {
                            Log.e("RouteFinderActivity", "User location is null");
                            Toast.makeText(this, "Location not available. Allow location services to proceed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.e("RouteFinderActivity", "Location permission not granted");
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
        // parse the JSON response
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        // check if there are routes in the response
        if (jsonObject.has("routes") && jsonObject.getAsJsonArray("routes").size() > 0) {

            // extract the polyline string from the "points" property of "overview_polyline"
            JsonObject route = jsonObject.getAsJsonArray("routes").get(0).getAsJsonObject();
            if (route.has("overview_polyline")) {
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");
                if (overviewPolyline.has("points")) {
                    return overviewPolyline.get("points").getAsString();
                }
            }
        }

        // return an empty string if the polyline is not found
        return "";
    }

    private void findRoute(LatLng origin, LatLng destination) {
        if (origin == null || destination == null) {
            Log.e("RouteFinderActivity", "Origin or destination is null");
            return;
        }

        // <removed personal API key>
        String apiKey = "<use API key here>";

        // URL for Google Directions API
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+extractCoordinates(String.valueOf(origin))+"&destination="+
                extractCoordinates(String.valueOf(destination))+"&mode=walking&key="+apiKey;

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

                // polyline can cause issues on main thread
                // so, start the process in a separate thread
                new Thread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(polylinePoints);
                    polylineOptions.color(Color.parseColor("#22668D"));
                    polylineOptions.width(15);

                    // markers to show user & report location
                    MarkerOptions startMarkerOptions = new MarkerOptions();
                    startMarkerOptions.position(origin);
                    startMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    MarkerOptions endMarkerOptions = new MarkerOptions();
                    endMarkerOptions.position(destination);
                    endMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkLocationServiceEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // prompt the user to enable location services
            new AlertDialog.Builder(this)
                    .setTitle("Location Services Not Enabled")
                    .setMessage("Please enable location services to proceed.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}


