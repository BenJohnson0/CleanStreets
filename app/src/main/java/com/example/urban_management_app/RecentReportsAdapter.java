package com.example.urban_management_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentReportsAdapter extends RecyclerView.Adapter<RecentReportsAdapter.ViewHolder> {

    private List<Report> reportList;
    private Context context;

    public RecentReportsAdapter(List<Report> reportList) {
        this.reportList = reportList;
        this.context = context;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitleTextView = itemView.findViewById(R.id.report_title_textview);
        }

        public void bind(Report report) {
            reportTitleTextView.setText(report.getTitle());
        }
    }
}

