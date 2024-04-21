package com.example.weathercollaborativeapp.adapter;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.model.Report;
import com.example.weathercollaborativeapp.utils.TimeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private boolean isReversed = false;

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
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.bind(report);
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateReports(List<Report> newReports) {
        Log.d("tutu", "isReversed : " + isReversed);
        if(isReversed){
            Collections.reverse(newReports);
        }
        reportList.clear();
        reportList.addAll(newReports);
        notifyDataSetChanged();
    }

    public void reverseData() {
        Collections.reverse(reportList);
        isReversed = !isReversed;
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewIcon;
        private TextView textViewTemperature;
        private TextView textViewWeatherType;
        private TextView textViewRelativeTime;

        private TextView textViewDistance;

        ReportViewHolder(View itemView){
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewTemperature = itemView.findViewById(R.id.textViewTemperature);
            textViewWeatherType = itemView.findViewById(R.id.textViewWeatherType);
            textViewRelativeTime = itemView.findViewById(R.id.textViewRelativeTime);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        void bind(Report report){
            textViewTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", report.getTemperature()));
            textViewWeatherType.setText(report.getWeatherType().getName());
            imageViewIcon.setImageResource(report.getWeatherType().getIconResourceId());
            textViewRelativeTime.setText(TimeUtils.getRelativeTime(report.getCreatedAt()));

            String distanceText = String.format(Locale.getDefault(), "%.2f km", report.getDistanceFromUser());
            textViewDistance.setText(distanceText);
        }
    }
}
