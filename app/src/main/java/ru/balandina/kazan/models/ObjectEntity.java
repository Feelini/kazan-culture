package ru.balandina.kazan.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ObjectEntity {
    private long id;
    private String address;
    private String description;
    private ArrayList<Double> location;
    private String nearbyMetroStation;
    private String routeFromTheCityCenter;
    private String openingHours;
    private String price;
    private String title;

    public ObjectEntity(){}

    public ObjectEntity(long id, String address, String description, ArrayList<Double> location,
                        String nearbyMetroStation, String routeFromTheCityCenter,
                        String openingHours, String price, String title) {
        this.id = id;
        this.address = address;
        this.description = description;
        this.location = location;
        this.nearbyMetroStation = nearbyMetroStation;
        this.routeFromTheCityCenter = routeFromTheCityCenter;
        this.openingHours = openingHours;
        this.price = price;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Double> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Double> location) {
        this.location = location;
    }

    public String getNearbyMetroStation() {
        return nearbyMetroStation;
    }

    public void setNearbyMetroStation(String nearbyMetroStation) {
        this.nearbyMetroStation = nearbyMetroStation;
    }

    public String getRouteFromTheCityCenter() {
        return routeFromTheCityCenter;
    }

    public void setRouteFromTheCityCenter(String routeFromTheCityCenter) {
        this.routeFromTheCityCenter = routeFromTheCityCenter;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
