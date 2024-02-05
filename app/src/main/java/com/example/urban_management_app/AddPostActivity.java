package com.example.urban_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private EditText postTitle, postContent;
    private Spinner spinnerTag;
    private Spinner spinnerPostcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // initialize Firebase database and storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        postTitle = findViewById(R.id.edit_text_post_title);
        postContent = findViewById(R.id.edit_text_post_content);

        spinnerTag = findViewById(R.id.spinner_tags);
        spinnerPostcode = findViewById(R.id.spinner_postcode);

        // handle submit
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String postId = databaseReference.push().getKey();
        final String title = postTitle.getText().toString().trim();
        final String content = postContent.getText().toString().trim();
        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        final String postcode = spinnerTag.getSelectedItem().toString();
        final String tag = spinnerPostcode.getSelectedItem().toString();

        // todo: retrieve other data

        if (validateData(title, content)) { // todo: validate all?
            Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), null, postId,  timeStamp, title, content, tag, postcode);
            databaseReference.push().setValue(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddPostActivity.this, "Post added successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity if desired
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPostActivity.this, "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // todo: display error message if validation fails
        }
    }

    // validation
    private boolean validateData(String title, String content) {
        // todo: check for empty fields, length restrictions, etc.
        return true;
    }
}