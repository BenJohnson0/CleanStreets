package com.example.urban_management_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posts, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.postTitleTextView.setText(post.getPostTitle());
        holder.postIdTextView.setText("Post ID: " + post.getPostId());
        holder.timestampTextView.setText(post.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView postIdTextView;
        public TextView postTitleTextView;
        public TextView timestampTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            postIdTextView = itemView.findViewById(R.id.post_id_text_view);
            postTitleTextView = itemView.findViewById(R.id.post_title_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
        }
    }
}
