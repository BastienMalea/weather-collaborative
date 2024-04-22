package com.example.weathercollaborativeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.model.Report;
import com.example.weathercollaborativeapp.model.WeatherType;
import com.example.weathercollaborativeapp.utils.WeatherIconUtils;

import java.util.List;

public class WeatherTypeAdapter extends RecyclerView.Adapter<WeatherTypeAdapter.ViewHolder> {

    private List<WeatherType> weatherTypeList;
    private LayoutInflater inflater;

    public WeatherTypeAdapter(List<WeatherType> weatherTypes, Context context){
        this.weatherTypeList = weatherTypes;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherType weatherType = weatherTypeList.get(position);
        holder.weatherName.setText(weatherType.getName());
        holder.weatherIcon.setImageResource(WeatherIconUtils.getIconResourceId(weatherType.getIcon()));
    }

    @Override
    public int getItemCount() {
        return weatherTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherIcon;
        public TextView weatherName;

        public ViewHolder(View view) {
            super(view);
            weatherIcon = view.findViewById(R.id.imageViewWeatherIcon);
            weatherName = view.findViewById(R.id.textViewWeatherType);
        }
    }
}