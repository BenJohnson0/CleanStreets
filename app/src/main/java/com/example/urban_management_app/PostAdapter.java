package com.example.urban_management_app;

// necessary imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// adapter class used for each Post
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static List<Post> posts;
    private static OnItemClickListener listener;

    public PostAdapter(List<Post> posts) { this.posts = posts; }

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener = listener; }

    public interface OnItemClickListener {
        void onItemClick(String postId);
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
        holder.postcodeTextView.setText(post.getPostCode());
        holder.tagTextView.setText(post.getPostTags());
        holder.timestampTextView.setText(post.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView postTitleTextView;
        public TextView timestampTextView;
        public TextView postcodeTextView;
        public TextView tagTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            postTitleTextView = itemView.findViewById(R.id.post_title_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
            postcodeTextView = itemView.findViewById(R.id.postcode_text_view);
            tagTextView = itemView.findViewById(R.id.tag_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // get the report at this position
                            Post clickedPost = posts.get(position);
                            // call the onItemClick method on the listener
                            listener.onItemClick(clickedPost.getPostId());
                        }
                    }
                }
            });
        }
    }
}
