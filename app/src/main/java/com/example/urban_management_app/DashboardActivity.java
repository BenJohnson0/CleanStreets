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
    // TODO: define other chart views here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chart1 = findViewById(R.id.chart1);
        // TODO: initialize other chart views

        // load data from Firebase Database and update the charts
        loadChartData();
    }

    private void loadChartData() {
        // database reference "reports"
        FirebaseDatabase.getInstance().getReference("reports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Entry> entries1 = new ArrayList<>();
                        // TODO: initialize other lists / other charts here

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //TODO: firebase data
                            // Parse  data from dataSnapshot and add to the corresponding lists
                        }

                        // set up chart data and display
                        setChartData(chart1, entries1, "Chart 1 Label");
                        // TODO: set up other charts here
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // TODO: handle error if data retrieval is canceled
                    }
                });
    }

    private void setChartData(LineChart chart, List<Entry> entries, String label) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        // customize chart appearance
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.colorAccent));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);

        // customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // refresh chart display
        chart.invalidate();
    }
}

