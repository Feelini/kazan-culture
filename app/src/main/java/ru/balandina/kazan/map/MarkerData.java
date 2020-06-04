package ru.balandina.kazan.map;

import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerData {
    private int id;
    private MarkerOptions markerOptions;

    public long getId() {
        return id;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public MarkerData(int id, MarkerOptions markerOptions) {
        this.id = id;
        this.markerOptions = markerOptions;
    }
}
