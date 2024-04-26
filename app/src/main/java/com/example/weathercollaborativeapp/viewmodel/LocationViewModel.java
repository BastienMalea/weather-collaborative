package com.example.weathercollaborativeapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class LocationViewModel extends ViewModel {

    private MutableLiveData<LatLng> userLocation = new MutableLiveData<>();

    public void setUserLocation(double latitude, double longitude){
        userLocation.setValue(new LatLng(latitude, longitude));
    }

    public LiveData<LatLng> getUserLocation(){
        return userLocation;
    }

}
