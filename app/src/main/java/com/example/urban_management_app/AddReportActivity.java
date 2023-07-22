package com.example.urban_management_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReportActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 2;
    private static final int REQUEST_MAP_SELECTION = 3;

    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    private EditText editTextTitle;
    private ImageView imageViewAttachment;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private Spinner spinnerSize;
    private Spinner spinnerUrgency;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        editTextTitle = findViewById(R.id.edit_text_title); // title of the report
        imageViewAttachment = findViewById(R.id.image_view_attachment); // [optional] image attached to the report
        Button buttonAttachImage = findViewById(R.id.button_attach_image); // [optional] button for uploading / taking an image
        Button buttonSubmit = findViewById(R.id.button_submit); // submit the report
        Button buttonSelectMap = findViewById(R.id.button_select_map); // for selecting the exact location of the report
        spinnerSize = findViewById(R.id.spinner_size); // for selecting the size of the issue
        spinnerUrgency = findViewById(R.id.spinner_urgency); // for selecting the urgency of the issue

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Report...");

        // Initialize Firebase database and storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");
        storageReference = FirebaseStorage.getInstance().getReference("report_images");

        buttonAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the uploadReport() method with the selected latitude and longitude
                uploadReport();
            }
        });

        buttonSelectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddReportActivity.this, MapSelectionActivity.class), REQUEST_MAP_SELECTION);
            }
        });

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
        } else {
            captureImage();
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // ... (your existing image capture handling)
        } else if (requestCode == REQUEST_MAP_SELECTION && resultCode == RESULT_OK) {
            if (data != null) {
                // Retrieve the selected coordinates from the MapSelectionActivity
                selectedLatitude = data.getDoubleExtra("latitude", 0);
                selectedLongitude = data.getDoubleExtra("longitude", 0);

                // You can display a message here to inform the user that the location is selected
                Toast.makeText(AddReportActivity.this, "Location selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void uploadReport() {
        final String title = editTextTitle.getText().toString().trim();
        final String size = spinnerSize.getSelectedItem().toString();
        final String urgency = spinnerUrgency.getSelectedItem().toString();

        // Check if the title, size, and urgency values are not empty
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(size) || TextUtils.isEmpty(urgency)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final String reportId = databaseReference.push().getKey();
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(reportId);
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Report report = new Report(reportId, timeStamp, String.valueOf(selectedLatitude), String.valueOf(selectedLongitude), size,
                                        urgency, uri.toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), title);
                                saveReportToDatabase(report);
                            }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddReportActivity.this,
                                "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If no image is attached, still create the report with empty image URL
            Report report = new Report(reportId, timeStamp, String.valueOf(selectedLatitude), String.valueOf(selectedLongitude),
                    size, urgency, "", FirebaseAuth.getInstance().getCurrentUser().getUid(), title);
            saveReportToDatabase(report);
        }
    }

    private void saveReportToDatabase(Report report) {
        databaseReference.child(report.getReportId()).setValue(report)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(AddReportActivity.this,
                                    "Report added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddReportActivity.this,
                                    "Failed to add report", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
