package com.example.urban_management_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReportActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 2;
    private static final int REQUEST_MAP_SELECTION = 3;

    private double selectedLatitude = 0;
    private double selectedLongitude = 0;
    private boolean isLocationSelected = false;
    
    private EditText editTextTitle;
    private ImageView imageViewAttachment;
    private Uri imageUri;
    private File imageFile;

    private ProgressDialog progressDialog;
    private Spinner spinnerSize;
    private Spinner spinnerUrgency;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    //TODO: ADD REPORT IMAGE USING UPLOAD FROM GALLERY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report); //set the layout

        editTextTitle = findViewById(R.id.edit_text_title); // title of the report
        imageViewAttachment = findViewById(R.id.image_view_attachment); // [optional] image attached to the report
        Button buttonAttachImage = findViewById(R.id.button_attach_image); // [optional] button for uploading / taking an image
        Button buttonSubmit = findViewById(R.id.button_submit); // submit the report
        Button buttonSelectMap = findViewById(R.id.button_select_map); // for selecting the exact location of the report
        spinnerSize = findViewById(R.id.spinner_size); // for selecting the size of the issue
        spinnerUrgency = findViewById(R.id.spinner_urgency); // for selecting the urgency of the issue

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Report...");

        // initialize Firebase database and storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");
        storageReference = FirebaseStorage.getInstance().getReference();

        buttonAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        // call uploadReport() method with the selected latitude and longitude
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadReport();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                // Create a temporary file to save the captured image
                imageFile = createImageFile();
                if (imageFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, "com.example.urban_management_app.fileprovider", imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (imageFile != null) {
                // save the URI of the captured image
                imageUri = Uri.fromFile(imageFile);

                // display image in ImageViewAttachment
                imageViewAttachment.setImageURI(imageUri);
            }
        }
        else if (requestCode == REQUEST_MAP_SELECTION && resultCode == RESULT_OK) {
            if (data != null) {
                // retrieve selected coordinates from MapSelectionActivity
                selectedLatitude = data.getDoubleExtra("latitude", 0);
                selectedLongitude = data.getDoubleExtra("longitude", 0);

                isLocationSelected = true;

                // display a message to inform the user that the location is selected
                Toast.makeText(AddReportActivity.this, "Location selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

    @SuppressLint("SimpleDateFormat")
    private void uploadReport() throws IOException {

        // check if the location data is added
        if (!isLocationSelected) {
            Toast.makeText(this, "Please select a location before submitting the report", Toast.LENGTH_SHORT).show();
            return;
        }

        // initialise report parameters
        final String title = editTextTitle.getText().toString().trim();
        final String size = spinnerSize.getSelectedItem().toString();
        final String urgency = spinnerUrgency.getSelectedItem().toString();
        final String status = "Active"; //TODO: fix with notifications etc.

        // check if the title, size, and urgency values are not empty and the title length is within 60 characters
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(size) || TextUtils.isEmpty(urgency) || title.length() > 60) {
            if (title.length() > 40) {
                editTextTitle.setError("Title must not exceed 40 characters!");
            }
            Toast.makeText(this, "Please fill in all fields and keep the title length within 40 characters", Toast.LENGTH_LONG).show();
            return;
        }

        // bad words check
        BadWordsFilter badWordsFilter = new BadWordsFilter(getResources().getAssets().open("bad-words.txt"));

        if (badWordsFilter.containsSwearWord(title)) {
            Toast.makeText(this, "Report title contains inappropriate language", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final String reportId = databaseReference.push().getKey();
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        if (imageUri != null) {
            // upload image to Firebase Storage
            StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // image uploaded successfully, get download URL
                            String imageUrl = uri.toString();
                            // create and save report to database with image URL
                            Report report = new Report(reportId, timeStamp, selectedLatitude, selectedLongitude, size,
                                    urgency, imageUrl, FirebaseAuth.getInstance().getCurrentUser().getUid(), title, status);
                            saveReportToDatabase(report);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddReportActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // no image attached, save report with empty image URL
            Report report = new Report(reportId, timeStamp, selectedLatitude, selectedLongitude, size,
                    urgency, "", FirebaseAuth.getInstance().getCurrentUser().getUid(), title, status);
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
}
