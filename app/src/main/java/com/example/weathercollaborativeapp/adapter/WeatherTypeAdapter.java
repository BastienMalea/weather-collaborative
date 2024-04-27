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

    public interface WeatherTypeSelectionListener {
        void onWeatherTypeSelected(boolean isSelected);
    }

    private List<WeatherType> weatherTypeList;
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private WeatherTypeSelectionListener selectionListener;
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
        holder.itemView.setSelected(position == selectedPosition);
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            if(selectionListener != null){
                selectionListener.onWeatherTypeSelected(true);
            }
        });

        holder.itemView.setSelected(position == selectedPosition);
        holder.checkMarkIcon.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

    }
    @Override
    public int getItemCount() {
        return weatherTypeList.size();
    }

    public WeatherType getSelectedWeatherType(){
        if(selectedPosition != -1){
            return weatherTypeList.get(selectedPosition);
        }
        return null;
    }

    public void setWeatherTypeSelectionListener(WeatherTypeSelectionListener listener) {
        this.selectionListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherIcon;
        public TextView weatherName;

        public ImageView checkMarkIcon;

        public ViewHolder(View view) {
            super(view);
            weatherIcon = view.findViewById(R.id.imageViewWeatherIcon);
            weatherName = view.findViewById(R.id.textViewWeatherType);
            checkMarkIcon = view.findViewById(R.id.imageViewCheckmark);
        }
    }
}
