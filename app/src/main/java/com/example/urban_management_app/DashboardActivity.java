package com.example.urban_management_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private LineChart chart1;
    // Define other chart views here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chart1 = findViewById(R.id.chart1);
        // Initialize other chart views here

        // Load data from Firebase Database and update the charts
        loadChartData();
    }

    private void loadChartData() {
        // Replace "dataRef" with your actual Firebase Database reference
        FirebaseDatabase.getInstance().getReference("dataRef")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Entry> entries1 = new ArrayList<>();
                        // Initialize other lists for other charts here

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //TODO: firebase data
                            // Parse your data from dataSnapshot and add to the corresponding lists
                            // Example: For a field "value1", you can do:
                            // float value = snapshot.child("value1").getValue(Float.class);
                            // entries1.add(new Entry(xValue, value));
                        }

                        // Set up chart data and display
                        setChartData(chart1, entries1, "Chart 1 Label");
                        // Set up other charts here
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error if data retrieval is canceled
                    }
                });
    }

    private void setChartData(LineChart chart, List<Entry> entries, String label) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        // Customize chart appearance
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorAccent));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);

        // Customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // Refresh chart display
        chart.invalidate();
    }
}

