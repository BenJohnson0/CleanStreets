package com.example.urban_management_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTextView = itemView.findViewById(R.id.report_title_textview);
            reportTimestampTextView = itemView.findViewById(R.id.report_timestamp_textview);
            reportSizeTextView = itemView.findViewById(R.id.report_size_textview);
            reportUrgencyTextView = itemView.findViewById(R.id.report_urgency_textview);
            reportStatusTextView = itemView.findViewById(R.id.report_status_textview);

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
            reportTimestampTextView.setText(report.getTimestamp());
            reportSizeTextView.setText("Size: " + report.getSize());
            reportUrgencyTextView.setText("Urgency: " + report.getUrgency());
            reportStatusTextView.setText("Status: " + report.getStatus());
        }
    }
}

