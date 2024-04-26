package com.example.weathercollaborativeapp.fragment;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Long, Marker> markerMap = new HashMap<>();
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

          addButton = view.findViewById(R.id.add_marker_button);
        addButton.setOnClickListener(v -> fetchAndDisplayWeatherTypes());

        return view;
    }

    @Override
    public void onResume() {
        Log.d("tutu", "onResume");
        super.onResume();
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), newLocation -> {
            latitude = newLocation.latitude;
            longitude = newLocation.longitude;

            setMarkersOnMap(latitude, longitude, 100);
        });
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
        EditText editTextPseudo = popupView.findViewById(R.id.pseudoInput);

        NumberPicker numberPickerTemperature = popupView.findViewById(R.id.numberPickerTemperature);

        final int minValue = -50;
        final int maxValue = 50;

        numberPickerTemperature.setMinValue(0);
        numberPickerTemperature.setMaxValue(maxValue - minValue);
        numberPickerTemperature.setValue(20 - minValue);
        numberPickerTemperature.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int index) {
                return Integer.toString(index + minValue);
            }
        });


        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewWeatherType);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        WeatherTypeAdapter adapter = new WeatherTypeAdapter(weatherTypes, getContext());
        recyclerView.setAdapter(adapter);

        builder.setPositiveButton("Ajouter", null); // Set OnClickListener to null initially
        builder.setNegativeButton("Annuler", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setEnabled(false);
            addButton.setOnClickListener(v -> {
                WeatherType selectedType = adapter.getSelectedWeatherType();
                String pseudo = editTextPseudo.getText().toString();
                if(pseudo == null || pseudo.isEmpty()){
                    pseudo = "Anonyme";
                }

                int temperature = numberPickerTemperature.getValue() + minValue;
                Report report = new Report(latitude, longitude, temperature, pseudo, selectedType);
                postReportToAPI(report);
                addMarker(report);
                Log.d("tutu", "Ajout report :");
                setMarkersOnMap(latitude, longitude, 100);

                dialog.dismiss();
            });

            adapter.setWeatherTypeSelectionListener(isSelected -> {
                addButton.setEnabled(isSelected);
            });
        });

        dialog.show();
    }

    private void postReportToAPI(Report report) {
        ReportService service = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            service = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }
        Call<Report> call = service.postReport(report);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Report ajouté avec succès", Toast.LENGTH_SHORT).show();
                    reports.add(report);
                } else {
                    Log.e("tutu", "Failed with response code: " + response.code());
                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("tutu", "ERROR BODY: " + errorBody);
                    Toast.makeText(getContext(), "Échec de l'ajout du report: " + errorBody, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setMarkersOnMap(double latitude, double longitude, int radius) {
        reports.clear();
        clearMarkerMap();
        ReportService apiService = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            apiService = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }

        assert apiService != null;
        Call<List<Report>> call = apiService.getNearbyReports(latitude, longitude, radius);

        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {
                    reports = response.body();

                    for(Report report : reports){
                        Log.d("tutu", "Report : " + report.getUsername() + " WeatherType : " + report.getWeatherType() + " Température : " + report.getTemperature());
                    }

                    for(Marker report : markerMap.values()){
                        Log.d("tutu", "Markers : " + report.getSnippet() + " Id : " + report.getId());
                    }

                    if(isMapReady && reports != null){
                        addMarkers(reports);
                    }

                    for(Marker report : markerMap.values()){
                        Log.d("tutu", "V2Markers : " + report.getSnippet() + " Id : " + report.getId());
                    }

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

    private void clearMarkerMap() {
        for (Marker marker : markerMap.values()) {
            marker.remove();  // Supprime chaque marker de la carte
        }
        markerMap.clear();
    }

    private void addMarkers(List<Report> reports) {
        if(googleMap != null){
            for(Report report: reports){
                Marker existingMarker = markerMap.get(report.getId());
                if (existingMarker != null) {
                    existingMarker.setPosition(new LatLng(report.getLatitude(), report.getLongitude()));
                    existingMarker.setTitle(report.getWeatherType().getName());
                    existingMarker.setSnippet(report.getTemperature() + " °C posté par " + report.getUsername());
                    existingMarker.setIcon(resizeMapIcons(report.getWeatherType().getIcon(), 100, 100));
                } else {
                    Marker newMarker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(report.getLatitude(), report.getLongitude()))
                            .title(report.getWeatherType().getName())
                            .snippet(report.getTemperature() + " °C posté par " + report.getUsername())
                            .icon(resizeMapIcons(report.getWeatherType().getIcon(), 100, 100)));
                    markerMap.put(report.getId(), newMarker);
                }
            }
        }
    }

    private void addMarker(Report report) {

        LatLng newPosition = new LatLng(report.getLatitude(), report.getLongitude());
        if(googleMap != null && report.getWeatherType() != null){
            boolean isNearExistingMarker = false;

            // Vérifier d'abord s'il existe déjà un marker à proximité
            for(Report comparedReport : reports){
                LatLng existingPosition = new LatLng(comparedReport.getLatitude(), comparedReport.getLongitude());
                float[] results = new float[1];
                Location.distanceBetween(newPosition.latitude, newPosition.longitude, existingPosition.latitude, existingPosition.longitude, results);
                float distanceInMeters = results[0];

                if (distanceInMeters < 200 && comparedReport.getId() != report.getId()){
                    deleteReport(comparedReport);
                    Marker markerToRemove = markerMap.get(comparedReport.getId());
                    if (markerToRemove != null) {
                        markerToRemove.remove();
                        markerMap.remove(comparedReport.getId());
                        reports.remove(comparedReport);
                        isNearExistingMarker = true;
                        Log.d("tutu", "Delete marker from map and report list");
                    }
                    break;
                }
            }
                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(newPosition)
                        .title(report.getWeatherType().getName())
                        .snippet(report.getTemperature() + " °C posté par " + report.getUsername())
                        .icon(resizeMapIcons(report.getWeatherType().getIcon(), 100, 100)));
                markerMap.put(report.getId(), newMarker);
                reports.add(report);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("tutu", "OnMapReady");
        this.googleMap = googleMap;
        isMapReady = true;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.map_style)
            );

            if (!success) {
                Log.d("tutu", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.d("tutu", "Can't find style. Error: ", e);
        }

        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Clermont-Ferrand"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        if (reports != null) {
            addMarkers(reports);
        }
    }

    private void deleteReport(Report report) {
        ReportService service = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            service = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }
        Call<Void> call = service.deleteReport(report.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("tutu", "Report deleted successfully");
                } else {
                    Log.e("tutu", "Failed to delete report, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("tutu", "Network error while deleting report", t);
            }
        });
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
