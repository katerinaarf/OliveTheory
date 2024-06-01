package com.example.olivetheory;

public interface WeatherCallback {
    void onSuccess(Weather weather);
    void onFailure(String errorMessage);
}
