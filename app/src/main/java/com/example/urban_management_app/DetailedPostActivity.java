package com.example.urban_management_app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailedPostActivity extends AppCompatActivity {

    private TextView titleTextView, postIDTextView, tagsTextView, contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        titleTextView = findViewById(R.id.titleTextView);
        postIDTextView = findViewById(R.id.postIDTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        contentTextView = findViewById(R.id.contentTextView);

        // retrieve post_id from the intent
        String postId = getIntent().getStringExtra("post_id");

        // create a Firebase database reference to the specific post
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        System.out.println(postRef);

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                if (post != null) {
                    titleTextView.setText(post.getPostTitle());
                    postIDTextView.setText("ID: " + post.getPostId());
                    tagsTextView.setText("Tag: " + post.getPostTags());
                    contentTextView.setText(post.getPostContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO: handle database read error
            }
        });
    }
}
