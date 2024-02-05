package com.example.urban_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoticeboardActivity extends AppCompatActivity {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> recentPostsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard);

        Button buttonAddPost = findViewById(R.id.add_post_button);

        // database references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts");

        // initialize recyclerview and set layout
        postRecyclerView = findViewById(R.id.post_recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // hold recent posts
        recentPostsList = new ArrayList<>();

        // set adapter for recyclerview
        postAdapter = new PostAdapter(recentPostsList);
        postRecyclerView.setAdapter(postAdapter);


        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String postId) {
                // Open DetailedPostActivity when RecyclerView post is clicked (using post id)
                Intent intent = new Intent(NoticeboardActivity.this, DetailedPostActivity.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });

        // load recent posts and populate view
        loadRecentPosts();

        buttonAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeboardActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadRecentPosts() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        Query recentPostsQuery = postsRef.limitToLast(100);

        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear existing list of posts
                recentPostsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    // add post
                    recentPostsList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle error
                Toast.makeText(NoticeboardActivity.this, "Failed to load recent posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}