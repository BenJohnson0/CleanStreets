package com.example.urban_management_app;

// necessary imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private EditText postTitle, postContent;
    private Spinner spinnerTag, spinnerPostcode, spinnerRefReport;
    private List<String> reportTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // for populating the ref report spinner
        fetchReportTitles();

        // initialize Firebase database and storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        postTitle = findViewById(R.id.edit_text_post_title);
        postContent = findViewById(R.id.edit_text_post_content);
        spinnerTag = findViewById(R.id.spinner_tags);
        spinnerPostcode = findViewById(R.id.spinner_postcode);
        spinnerRefReport = findViewById(R.id.spinner_refReport);

        // handle submit
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submitPost();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void fetchReportTitles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportTitles = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (report != null) {
                        reportTitles.add(report.getTitle());
                    }
                }

                // populate spinner with report titles
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPostActivity.this,
                        android.R.layout.simple_spinner_item, reportTitles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRefReport.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle database error
            }
        });
    }

    private void submitPost() throws IOException {
        // initialise post parameters
        final String title = postTitle.getText().toString().trim();
        final String content = postContent.getText().toString().trim();
        final String postcode = spinnerPostcode.getSelectedItem().toString();
        final String tag = spinnerTag.getSelectedItem().toString();
        final String selectedReportTitle = spinnerRefReport.getSelectedItem().toString();

        // check if the title and content values are not empty and the title length is within 60 characters
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || title.length() > 60) {
            if (title.length() > 60) {
                postTitle.setError("Title must not exceed 60 characters!");
            }
            Toast.makeText(this, "Please fill in all fields and keep the title length within 60 characters", Toast.LENGTH_LONG).show();
            return;
        }

        // bad words check
        BadWordsFilter badWordsFilter = new BadWordsFilter(getResources().getAssets().open("bad-words.txt"));

        if (badWordsFilter.containsSwearWord(title)) {
            Toast.makeText(this, "Do not use inappropriate language, try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (badWordsFilter.containsSwearWord(content)) {
            Toast.makeText(this, "Do not use inappropriate language, try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String postId = databaseReference.push().getKey();

        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), selectedReportTitle, postId,
                timeStamp, title, content, tag, postcode);

        databaseReference.child(postId).setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddPostActivity.this, "Post added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPostActivity.this, "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}