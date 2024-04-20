package com.example.weathercollaborativeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.model.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.temperatureTextView.setText(report.getTemperature() + "°C");
        Glide.with(holder.itemView.getContext()).load("URL_or_resource_of_icon/" + report.getWeatherType().getIcon()).into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void updateReports(List<Report> newReports) {
        reportList.clear();
        reportList.addAll(newReports);
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView temperatureTextView;
        ImageView iconImageView;

        public ReportViewHolder(@NonNull View itemView){
            super(itemView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            iconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }
}
