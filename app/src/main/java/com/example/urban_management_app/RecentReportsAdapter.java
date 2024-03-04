package com.example.urban_management_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecentReportsAdapter extends RecyclerView.Adapter<RecentReportsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String reportId);
    }

    private List<Report> reportList;
    private OnItemClickListener listener;

    public RecentReportsAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_reports, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);

        /*
        String status = report.getStatus();
        if (status != null) {
            switch (status) {
                case "Active":
                    holder.reportStatusTextView.setBackgroundResource(R.drawable.rounded_bubbles);
                    break;
                case "In-progress":
                    holder.reportStatusTextView.setBackgroundResource(R.drawable.orange_rounded_bubbles);
                    break;
                case "Completed":
                    holder.reportStatusTextView.setBackgroundResource(R.drawable.purple_rounded_bubbles);
                    break;
                default:
                    // Default background color or do nothing
                    break;
            }
        }
        */
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView reportTitleTextView;
        private TextView reportTimestampTextView;
        private TextView reportSizeTextView;
        private TextView reportUrgencyTextView;
        private TextView reportStatusTextView;
        private TextView thumbsUpCounter;
        private ImageView thumbsUpIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTextView = itemView.findViewById(R.id.report_title_textview);
            reportTimestampTextView = itemView.findViewById(R.id.report_timestamp_textview);
            reportSizeTextView = itemView.findViewById(R.id.report_size_textview);
            reportUrgencyTextView = itemView.findViewById(R.id.report_urgency_textview);
            reportStatusTextView = itemView.findViewById(R.id.report_status_textview);
            thumbsUpCounter = itemView.findViewById(R.id.thumbs_up_counter);
            thumbsUpIcon = itemView.findViewById(R.id.thumbs_up_icon);

            thumbsUpIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the report at this position
                        Report clickedReport = reportList.get(position);
                        // Increment the thumbs up count for this report
                        clickedReport.incrementThumbsUpCount();
                        // Update the UI
                        thumbsUpCounter.setText(String.valueOf(clickedReport.getThumbsUpCount()));

                        // Update the thumbs up count in the database
                        FirebaseDatabase.getInstance().getReference("reports")
                                .child(clickedReport.getReportId())
                                .child("thumbsUpCount")
                                .setValue(clickedReport.getThumbsUpCount());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // get the report at this position
                            Report clickedReport = reportList.get(position);
                            // call the onItemClick method on the listener
                            listener.onItemClick(clickedReport.getReportId());
                        }
                    }
                }
            });
        }

        public void bind(Report report) {
            reportTitleTextView.setText(report.getTitle());
            reportTimestampTextView.setText("at " + report.getTimestamp());
            reportSizeTextView.setText(report.getSize());
            reportUrgencyTextView.setText(report.getUrgency());
            reportStatusTextView.setText(report.getStatus());
            thumbsUpCounter.setText(String.valueOf(report.getThumbsUpCount()));
        }
    }
}

