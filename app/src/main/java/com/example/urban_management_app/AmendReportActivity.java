package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AmendReportActivity extends AppCompatActivity {

    private EditText titleEditText;
    private Spinner sizeSpinner, urgencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend_report);

        titleEditText = findViewById(R.id.titleEditText);
        sizeSpinner = findViewById(R.id.sizeSpinner);
        urgencySpinner = findViewById(R.id.urgencySpinner);
        Button amendButton = findViewById(R.id.amendButton);

        // Retrieve report_id from the intent
        String reportId = getIntent().getStringExtra("report_id");

        // Set up Size Spinner
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.size_options, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        // Set up Urgency Spinner
        ArrayAdapter<CharSequence> urgencyAdapter = ArrayAdapter.createFromResource(
                this, R.array.urgency_options, android.R.layout.simple_spinner_item);
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urgencySpinner.setAdapter(urgencyAdapter);

        amendButton.setOnClickListener(v -> {
            // Get the updated details
            String updatedTitle = titleEditText.getText().toString();
            String updatedSize = sizeSpinner.getSelectedItem().toString();
            String updatedUrgency = urgencySpinner.getSelectedItem().toString();

            // Update the report in the database
            updateReport(reportId, updatedTitle, updatedSize, updatedUrgency);
        });
    }

    private void updateReport(String reportId, String updatedTitle, String updatedSize, String updatedUrgency) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(reportId);

        // Update the specific fields
        reportRef.child("title").setValue(updatedTitle);
        reportRef.child("size").setValue(updatedSize);
        reportRef.child("urgency").setValue(updatedUrgency);

        // Update timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());
        reportRef.child("timestamp").setValue(timestamp);

        startActivity(new Intent(AmendReportActivity.this, HomeActivity.class));
    }
}
