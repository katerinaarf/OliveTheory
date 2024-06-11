package com.example.olivetheory.models;

public class SavedLocation {
    private String name;
    private double latitude, longitude;

    public SavedLocation() {
        // Default constructor required for Firestore
    }

    public SavedLocation( double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name + "\n" + "Lat: " + latitude + ", Lng: " + longitude;
    }
}
