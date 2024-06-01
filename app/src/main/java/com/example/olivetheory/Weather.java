package com.example.olivetheory;

public class Weather {
    private String name;
    private double temperature;
    private double humidity;

    public Weather(){
    }
    public Weather(String name, double temperature, double humidity){
        this.humidity = humidity;
        this.name = name;
        this.temperature = temperature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}

