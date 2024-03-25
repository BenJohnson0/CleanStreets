package com.example.urban_management_app;

// necessary imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class YourReportsAdapter extends RecyclerView.Adapter<YourReportsAdapter.ViewHolder> {

    private List<Report> reportList;
    private OnReportDeleteListener deleteListener;

    public YourReportsAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    public void deleteReport(Report report) {
        int position = reportList.indexOf(report);
        if (position != -1) {
            reportList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnReportDeleteListener {
        void onReportDelete(Report report);
    }

    public void setOnReportDeleteListener(OnReportDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_your_reports, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the report at this position
                Report report = reportList.get(holder.getAdapterPosition());
                // Call the delete listener
                if (deleteListener != null) {
                    deleteListener.onReportDelete(report);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView reportTitleTextView;
        private TextView reportTimestampTextView;
        private TextView reportSizeTextView;
        private TextView reportUrgencyTextView;
        private TextView reportStatusTextView;
        private ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTextView = itemView.findViewById(R.id.report_title_textview);
            reportTimestampTextView = itemView.findViewById(R.id.report_timestamp_textview);
            reportSizeTextView = itemView.findViewById(R.id.report_size_textview);
            reportUrgencyTextView = itemView.findViewById(R.id.report_urgency_textview);
            reportStatusTextView = itemView.findViewById(R.id.report_status_textview);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(Report report) {
            reportTitleTextView.setText(report.getTitle());
            reportTimestampTextView.setText(report.getTimestamp());
            reportSizeTextView.setText(report.getSize());
            reportUrgencyTextView.setText(report.getUrgency());
            reportStatusTextView.setText(report.getStatus());
        }
    }
}
