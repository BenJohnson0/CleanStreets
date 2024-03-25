package com.example.urban_management_app;

// necessary imports
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateStatusActivity extends AppCompatActivity {

    private Spinner statusSpinner;
    private Button confirmButton;
    private DatabaseReference reportRef;
    private String reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);

        reportRef = FirebaseDatabase.getInstance().getReference("reports");

        reportId = getIntent().getStringExtra("report_id");

        statusSpinner = findViewById(R.id.status_spinner);
        confirmButton = findViewById(R.id.confirm_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReportStatus();
            }
        });
    }

    private void updateReportStatus() {
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        reportRef.child(reportId).child("status").setValue(selectedStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateStatusActivity.this, "Report status updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateStatusActivity.this, "Failed to update report status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
