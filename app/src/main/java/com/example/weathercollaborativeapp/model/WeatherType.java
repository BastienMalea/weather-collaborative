package com.example.weathercollaborativeapp.model;

import com.example.weathercollaborativeapp.R;

public class WeatherType {
    private long id;
    private String name;
    private String icon;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconResourceId(){
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
