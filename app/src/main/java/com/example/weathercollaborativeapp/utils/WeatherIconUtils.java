package com.example.weathercollaborativeapp.utils;

import com.example.weathercollaborativeapp.R;

public class WeatherIconUtils {
    public static int getIconResourceId(String icon){
        switch (icon){
            case "soleil_icon":
                return R.drawable.soleil;
            case "nuage_icon":
                return R.drawable.nuage;
            case "pluie_icon":
                return R.drawable.pluie;
            case "eclaircies_icon":
                return R.drawable.eclaircies;
            case "couvert_icon":
                return R.drawable.couvert;
            case "brouillard_icon":
                return R.drawable.brouillard;
            case "averses_icon":
                return R.drawable.averse;
            case "neige_icon":
                return R.drawable.neige;

            default:
                return R.drawable.soleil;
        }
    }
}
