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
        Log.d("tutu", "Click sur le boutond ajout ");
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
                    Log.d("tutu", "Recupere la liste des weatherType");

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
                int temperature = numberPickerTemperature.getValue() + minValue;
                Report report = new Report(latitude, longitude, temperature, pseudo, selectedType);
                addMarker(report);
                postReportToAPI(report);
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

                    if(isMapReady && reports != null){
                        for(Report report: reports){
                            addMarker(report);
                        }
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        if (reports != null){
            for(Report report: reports){
                addMarker(report);
            }
        }
    }

    private Map<Marker, Report> markers = new HashMap<>();

    private void addMarker(Report report) {
        if (googleMap != null && report.getWeatherType() != null) {
            LatLng newPosition = new LatLng(report.getLatitude(), report.getLongitude());
            Marker markerToRemove = null;

            for(Map.Entry<Marker, Report> entry : markers.entrySet()){
                Marker existingMarker = entry.getKey();
                Report existingReport = entry.getValue();
                LatLng existingPosition = new LatLng(existingReport.getLatitude(), existingReport.getLongitude());

                float[] results = new float[1];
                Location.distanceBetween(newPosition.latitude, newPosition.longitude, existingPosition.latitude, existingPosition.longitude, results);
                float distanceInMeters = results[0];

                if (distanceInMeters < 500) {
                    markerToRemove = existingMarker;
                    deleteReport(existingReport);
                    break;
                }
            }

            if (markerToRemove != null) {
                markerToRemove.remove();
                markers.remove(markerToRemove);
            }


            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(newPosition)
                    .title(report.getWeatherType().getName())
                    .snippet(report.getTemperature() + " °C posté par " + report.getUsername())
                    .icon(resizeMapIcons(report.getWeatherType().getIcon(), 100, 100)));
            markers.put(newMarker, report);
        }
    }

    private void deleteReport(Report report) {
        ReportService service = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            service = RetrofitClientInstance.getRetrofitInstance().create(ReportService.class);
        }
        Log.d("tutu", "Id du report : " + report.getId());
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
