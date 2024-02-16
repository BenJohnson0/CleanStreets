package com.example.urban_management_app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private BarChart chart1;
    private BarChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);


        // load data from Firebase Database and update the charts
        loadSizeChartData();
        loadPostTypeChartData();
    }

    private void loadSizeChartData() {
        // initialize chart entries
        List<BarEntry> entries = new ArrayList<>();

        // initialize counters for each size category
        int[] sizeCounts = new int[5]; // Assuming 5 size categories

        // query Firebase reports
        FirebaseDatabase.getInstance().getReference("reports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Report report = snapshot.getValue(Report.class);
                            String size = report.getSize();

                            // increment counter for corresponding sizes
                            switch (size) {
                                case "Very Small":
                                    sizeCounts[0]++;
                                    break;
                                case "Small":
                                    sizeCounts[1]++;
                                    break;
                                case "Medium":
                                    sizeCounts[2]++;
                                    break;
                                case "Large":
                                    sizeCounts[3]++;
                                    break;
                                case "Very Large":
                                    sizeCounts[4]++;
                                    break;
                                default:
                                    break;
                            }
                        }

                        // populate chart entries
                        for (int i = 0; i < sizeCounts.length; i++) {
                            entries.add(new BarEntry(i, sizeCounts[i]));
                        }

                        // parse chart data and display
                        setBarChartData(chart1, entries, "Report Sizes Distribution");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // todo: handle cancelled database query
                    }
                });
    }

    private void setBarChartData(BarChart chart, List<BarEntry> entries, String label) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        chart.setData(barData);

        // customize chart appearance
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);

        // customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // refresh chart display
        chart.invalidate();
    }


    private void loadPostTypeChartData() {
        // Initialize chart entries
        List<BarEntry> entries = new ArrayList<>();

        // Initialize counters for each event type
        int[] eventCounts = new int[5]; // Assuming 5 event types

        // Query Firebase database for posts
        FirebaseDatabase.getInstance().getReference("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            String eventType = post.getPostTags();

                            // Increment the counter for the corresponding event type
                            switch (eventType) {
                                case "Fundraiser":
                                    eventCounts[0]++;
                                    break;
                                case "Cleanup":
                                    eventCounts[1]++;
                                    break;
                                case "Online meeting":
                                    eventCounts[2]++;
                                    break;
                                case "In-person meeting":
                                    eventCounts[3]++;
                                    break;
                                case "Help":
                                    eventCounts[4]++;
                                    break;
                                default:
                                    break;
                            }
                        }

                        // Populate chart entries
                        for (int i = 0; i < eventCounts.length; i++) {
                            entries.add(new BarEntry(i, eventCounts[i]));
                        }

                        // Set up chart data and display
                        setPostTagBarChartData(chart2, entries, "Post Tags Distribution");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle cancelled database query
                    }
                });
    }

    private void setPostTagBarChartData(BarChart chart, List<BarEntry> entries, String label) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        chart.setData(barData);

        // customize chart appearance
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);

        // customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // refresh chart display
        chart.invalidate();
    }

}

