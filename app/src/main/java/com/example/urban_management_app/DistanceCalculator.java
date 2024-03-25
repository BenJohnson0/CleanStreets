package com.example.urban_management_app;

// necessary imports
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class DistanceCalculator {

    // check if the origin OR list of reports is empty
    public static LatLng findNearest(LatLng origin, List<LatLng> points) {
        if (origin == null || points == null || points.isEmpty()) {
            return null;
        }

        // initialise the variables
        LatLng nearestPoint = null;
        double nearestDistance = Double.MAX_VALUE;

        // calculate the distance between the user and report, for each report
        // and store the result
        for (LatLng point : points) {
            double distance = calculateDistance(origin, point);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPoint = point;
            }
        }

        return nearestPoint;
    }

    private static double calculateDistance(LatLng point1, LatLng point2) {
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        // haversine formula
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // earth radius in km
        double radius = 6371.0;

        return radius * c;
    }
}
