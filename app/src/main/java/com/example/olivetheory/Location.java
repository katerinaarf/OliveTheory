package com.example.olivetheory;

import java.io.Serializable;

public class Location implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private int userId;  // To associate place with a user

    public Location(double latitude, double longitude, int userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){this.id = id;}
    public double getLatitude() {
        return latitude;
    }
    public  void setLatitude(double latitude){this.latitude = latitude;}
    public double getLongitude() {
        return longitude;
    }
    public  void setLongitude(double longitude){this.longitude = longitude;}
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId){this.userId = userId;}
}
