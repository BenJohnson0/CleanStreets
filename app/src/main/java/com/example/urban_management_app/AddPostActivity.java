package com.example.urban_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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
        // initialise post parameters
        final String title = postTitle.getText().toString().trim();
        final String content = postContent.getText().toString().trim();
        final String postcode = spinnerPostcode.getSelectedItem().toString();
        final String tag = spinnerTag.getSelectedItem().toString();

        // check if the title and content values are not empty and the title length is within 60 characters
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || title.length() > 60) {
            if (title.length() > 40) {
                postTitle.setError("Title must not exceed 40 characters!");
            }
            Toast.makeText(this, "Please fill in all fields and keep the title length within 40 characters", Toast.LENGTH_LONG).show();
            return;
        }

        final String postId = databaseReference.push().getKey();

        final String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), null, postId,
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