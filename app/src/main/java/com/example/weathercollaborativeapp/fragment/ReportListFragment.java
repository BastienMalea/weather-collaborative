package com.example.weathercollaborativeapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.adapter.ReportAdapter;
import com.example.weathercollaborativeapp.model.Report;
import com.example.weathercollaborativeapp.network.ReportService;
import com.example.weathercollaborativeapp.network.RetrofitClientInstance;
import com.example.weathercollaborativeapp.viewmodel.LocationViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportListFragment extends Fragment {

    double latitude;
    double longitude;
    private SeekBar seekBarRadius;
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private TextView textViewRadius;
    private Button buttonSort;
    private LocationViewModel locationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewReports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportAdapter = new ReportAdapter(new ArrayList<>());
        recyclerView.setAdapter(reportAdapter);
        seekBarRadius = view.findViewById(R.id.seekBarRadius);
        textViewRadius = view.findViewById(R.id.textViewRadius);
        buttonSort = view.findViewById(R.id.buttonSort);

        setupButtonSort();
        setupSeekBar();

        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            latitude = newLocation.latitude;
            longitude = newLocation.longitude;
            fetchReports(latitude, longitude, seekBarRadius.getProgress() + 1);
        });


        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        fetchReports(latitude, longitude, seekBarRadius.getProgress() + 1);
    }

    private void setupButtonSort() {
        buttonSort.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(reportAdapter != null){
                    reportAdapter.reverseData();
                }
            }
        });
    }

    private void setupSeekBar() {
        seekBarRadius.setMax(99);
        seekBarRadius.setProgress(50);
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    textViewRadius.setText("Rayon:" + progress + " km" );
                    fetchReports(latitude, longitude, progress + 1);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void fetchReports(double latitude, double longitude, int radius) {

        ReportService apiService = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            apiService = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }
        Call<List<Report>> call = apiService.getNearbyReports(latitude, longitude, radius);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {

                    List<Report> reports = response.body();
                    for (Report report : reports) {
                        report.updateDistanceFrom(latitude, longitude);
                    }

                    reportAdapter.updateReports(response.body());
                } else {
                    Toast.makeText(getContext(), "Error fetching reports", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable throwable) {
                Log.d("tutu", "Network error", throwable);
            }

        });
    }

}
