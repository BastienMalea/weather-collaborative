package com.example.weathercollaborativeapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weathercollaborativeapp.R;
import com.example.weathercollaborativeapp.viewmodel.LocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class WeatherMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationViewModel locationViewModel;

    double latitude;
    double longitude;

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

        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), this::updateLocation);

        return view;
    }

    private void updateLocation(LatLng location) {
        if (location != null) {
            latitude = location.latitude;
            longitude = location.longitude;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("Clermont-Ferrand"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
    }
}
