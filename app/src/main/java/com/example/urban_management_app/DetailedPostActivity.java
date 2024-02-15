package com.example.urban_management_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailedPostActivity extends AppCompatActivity {

    private TextView titleTextView, postIDTextView, tagsTextView, contentTextView;
    private RecyclerView repliesRecyclerView;
    private List<Reply> repliesList;
    private RepliesAdapter repliesAdapter;
    private Button replyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        titleTextView = findViewById(R.id.titleTextView);
        postIDTextView = findViewById(R.id.postIDTextView);
        tagsTextView = findViewById(R.id.tagsTextView);
        contentTextView = findViewById(R.id.contentTextView);
        replyButton = findViewById(R.id.replyButton);

        repliesRecyclerView = findViewById(R.id.repliesRecyclerView);
        repliesList = new ArrayList<>();
        repliesAdapter = new RepliesAdapter(repliesList);
        repliesRecyclerView.setAdapter(repliesAdapter);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // retrieve post_id from the intent
        String postId = getIntent().getStringExtra("post_id");

        // fetch replies from Firebase
        fetchRepliesFromFirebase();

        // create a Firebase database reference to the specific post
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        findViewById(R.id.replyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedPostActivity.this, ReplyToPost.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });

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

    private void fetchRepliesFromFirebase() {
        // todo: implement Firebase database query to fetch replies
        // todo: populate repliesList with fetched replies
    }
}
