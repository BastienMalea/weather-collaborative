package com.example.weathercollaborativeapp.fragment;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.adapter.WeatherTypeAdapter;
import com.example.weathercollaborativeapp.model.Report;
import com.example.weathercollaborativeapp.model.WeatherType;
import com.example.weathercollaborativeapp.network.ReportService;
import com.example.weathercollaborativeapp.network.RetrofitClientInstance;
import com.example.weathercollaborativeapp.network.WeatherService;
import com.example.weathercollaborativeapp.utils.WeatherIconUtils;
import com.example.weathercollaborativeapp.viewmodel.LocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationViewModel locationViewModel;
    double latitude;
    double longitude;
    List<Report> reports = new ArrayList<>();
    private boolean isMapReady = false;
    private Button addButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_weather_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            latitude = newLocation.latitude;
            longitude = newLocation.longitude;
            Log.d("tutu", "Nouvelle localisation reçue: " + latitude + " longitude : " + longitude);
            setMarkersOnMap(latitude, longitude, 100);
        });

        addButton = view.findViewById(R.id.add_marker_button);
        addButton.setOnClickListener(v -> fetchAndDisplayWeatherTypes());

        return view;
    }

    private void fetchAndDisplayWeatherTypes() {
        WeatherService service = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            service = RetrofitClientInstance.getRetrofitInstance().create(WeatherService.class);
        }
        if(service == null) return;

        Call<List<WeatherType>> call = service.getWeatherTypes();
        call.enqueue(new Callback<List<WeatherType>>() {
            @Override
            public void onResponse(Call<List<WeatherType>> call, Response<List<WeatherType>> response) {
                if (response.isSuccessful()) {
                    List<WeatherType> weatherTypes = response.body();
                    showWeatherTypesPopup(weatherTypes);
                } else {
                    Log.e("tutu", "Error fetching weather types");
                }
            }

            @Override
            public void onFailure(Call<List<WeatherType>> call, Throwable t) {
                Log.e("tutu", "Network error while fetching weather types", t);
            }
        });
    }

    private void showWeatherTypesPopup(List<WeatherType> weatherTypes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.dialog_add_marker, null);
        builder.setView(popupView);

        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewWeatherType);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new WeatherTypeAdapter(weatherTypes, getContext()));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addMarkerAtUserLocation(String pseudo, String selectedWeatherIcon) {
        LatLng userPosition = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(userPosition)
                .title(pseudo)
                .icon(BitmapDescriptorFactory.fromResource(getIconResourceId(selectedWeatherIcon))));
    }

    private void setMarkersOnMap(double latitude, double longitude, int radius) {
        Log.d("tutu", "Passage setMarkersonMap : " + latitude + longitude);
        ReportService apiService = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            apiService = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }

        assert apiService != null;
        Log.d("tutu", "Call API");

        Call<List<Report>> call = apiService.getNearbyReports(latitude, longitude, radius);

        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {
                    Log.d("tutu", "Reponses Successfull" + response.body());

                    reports = response.body();

                    if(reports != null){
                        addMarkers(reports);
                    }

                } else {
                    Log.d("tutu", "Fail");

                    Toast.makeText(getContext(), "Error fetching reports", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable throwable) {
                Log.d("tutu", "Network error", throwable);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.googleMap = googleMap;
        isMapReady = true;

        try{
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.map_style)
            );

            if (!success) {
                Log.d("tutu", "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            Log.d("tutu", "Can't find style. Error: ", e);
        }

        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Clermont-Ferrand"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        if (reports != null){
            addMarkers(reports);
        }


    }

    private void addMarkers(List<Report> reports) {
        if(googleMap != null && isMapReady){
            for(Report report: reports){
                LatLng position = new LatLng(report.getLatitude(), report.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(report.getWeatherType().getName())
                        .icon(resizeMapIcons(report.getWeatherType().getIcon(), 100, 100)));
            }
        }else{
            Log.d("tutu", "Map is not ready yet");
        }
    }

    private int getIconResourceId(String icon) {
        return WeatherIconUtils.getIconResourceId(icon);
    }

    private BitmapDescriptor resizeMapIcons(String iconName, int width, int height){
        int iconResourceId = getIconResourceId(iconName);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), iconResourceId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }
}
