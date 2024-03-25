package com.example.urban_management_app;

// necessary imports
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private BarChart chart1, chart2;
    private PieChart chart3, chart4;
    private LineChart chart5;

    // for pie chart 1
    int reportsWithImages = 0;
    int reportsWithoutImages = 0;

    // for pie chart 2
    int activeReports = 0;
    int inProgressReports = 0;
    int completedReports = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);
        chart4 = findViewById(R.id.chart4);
        chart5 = findViewById(R.id.chart5);

        // load data from Firebase Database and update the charts
        loadSizeChartData();
        loadPostTypeChartData();
        loadReportImageData();
        loadReportStatusData();
        loadReportsData();
    }

    private void loadReportsData() {
        // initialize HashMap to store the count of reports made each day
        HashMap<String, Integer> reportsPerDay = new HashMap<>();

        // query Firebase to count reports made each day
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null) {
                        // extract the timestamp and format to get the date
                        long timestamp = parseTimestamp(report.getTimestamp());
                        String date = getFormattedDate(timestamp);

                        // increment the count of reports made on that day
                        if (reportsPerDay.containsKey(date)) {
                            reportsPerDay.put(date, reportsPerDay.get(date) + 1);
                        } else {
                            reportsPerDay.put(date, 1);
                        }
                    }
                }

                // create entries for the line chart
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : reportsPerDay.entrySet()) {
                    String date = entry.getKey();
                    int count = entry.getValue();
                    entries.add(new Entry(labels.size(), count));
                    labels.add(date);
                }

                // display data on the line chart
                displayLineChart(entries, labels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle cancelled
            }
        });
    }

    private long parseTimestamp(String timestamp) {
        try {
            // define the date format of your timestamp string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // parse the timestamp string into a Date object
            Date date = sdf.parse(timestamp);
            // return the milliseconds since the Unix epoch
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            // return 0 (error)
            return 0;
        }
    }


    private String getFormattedDate(long timestamp) {
        // convert timestamp to Date object
        Date date = new Date(timestamp);
        // format date as "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    private void displayLineChart(List<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, "Reports per Day");
        LineData lineData = new LineData(dataSet);

        chart5.setData(lineData);
        chart5.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart5.invalidate();
    }

    private void loadReportStatusData() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null) {
                        String status = report.getStatus();
                        switch (status) {
                            case "Active":
                                activeReports++;
                                break;
                            case "In-progress":
                                inProgressReports++;
                                break;
                            case "Completed":
                                completedReports++;
                                break;
                        }
                    }
                }

                displayPieChart(chart4, activeReports, inProgressReports, completedReports);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle cancelled
            }
        });
    }

    private void displayPieChart(PieChart pieChart, int... counts) {
        List<PieEntry> entries = new ArrayList<>();
        String[] labels = {"Active", "In-progress", "Completed"};

        for (int i = 0; i < counts.length; i++) {
            entries.add(new PieEntry(counts[i], labels[i]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.invalidate();
    }

    private void loadReportImageData() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null && !report.getImageUrl().isEmpty()) {
                        reportsWithImages++;
                    } else {
                        reportsWithoutImages++;
                    }
                }

                displayPieChart(reportsWithImages, reportsWithoutImages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle cancelled
            }
        });
    }

    private void displayPieChart(int reportsWithImages, int reportsWithoutImages) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(reportsWithImages, "Reports with Images"));
        entries.add(new PieEntry(reportsWithoutImages, "Reports without Images"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        chart3.setData(data);
        chart3.setEntryLabelColor(Color.BLACK);
        chart3.invalidate();
    }

    private void loadSizeChartData() {
        // initialize chart entries
        List<BarEntry> entries = new ArrayList<>();

        // initialize counters for each size category
        int[] sizeCounts = new int[3]; // 3 size categories

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
                                case "Small":
                                    sizeCounts[0]++;
                                    break;
                                case "Medium":
                                    sizeCounts[1]++;
                                    break;
                                case "Large":
                                    sizeCounts[2]++;
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
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);

        // customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // refresh chart display
        chart.invalidate();
    }


    private void loadPostTypeChartData() {
        // initialize charts
        List<BarEntry> entries = new ArrayList<>();

        // initialize counters for each event
        int[] eventCounts = new int[5]; // todo: how many event types

        // query firebase for posts
        FirebaseDatabase.getInstance().getReference("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            String eventType = post.getPostTags();

                            // increment the counter for the corresponding event type
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

                        // populate chart
                        for (int i = 0; i < eventCounts.length; i++) {
                            entries.add(new BarEntry(i, eventCounts[i]));
                        }

                        // set up chart data and display
                        setPostTagBarChartData(chart2, entries, "Post Tags Distribution");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // todo: handle cancelled database query
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
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);

        // customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size(), true);

        // refresh chart display
        chart.invalidate();
    }

}

