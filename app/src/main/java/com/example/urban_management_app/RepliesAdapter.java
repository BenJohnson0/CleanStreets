package com.example.urban_management_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {

    private List<Reply> repliesList;

    public RepliesAdapter(List<Reply> repliesList) {
        this.repliesList = repliesList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = repliesList.get(position);
        holder.usernameTextView.setText(reply.getUsername());
        holder.messageTextView.setText(reply.getMessage());
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, messageTextView;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}

