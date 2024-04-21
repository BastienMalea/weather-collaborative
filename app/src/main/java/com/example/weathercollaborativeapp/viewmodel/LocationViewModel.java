package com.example.weathercollaborativeapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class LocationViewModel extends ViewModel {

    private MutableLiveData<LatLng> userLocation = new MutableLiveData<>();

    public void setUserLocation(double latitude, double longitude){
        Log.d("Location", "LocationUser " + latitude + " / " + longitude);
        userLocation.setValue(new LatLng(latitude, longitude));
    }

    public LiveData<LatLng> getUserLocation(){
        Log.d("Location", "getUserLocation " + userLocation);
        return userLocation;
    }

}
