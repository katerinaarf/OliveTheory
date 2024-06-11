package com.example.olivetheory.models;

import java.util.List;

public class Disease {
    private String name;
    private String description;
    private List<String> keywords;
    private String treatment;
    private String image;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getImage() {
        return image;
    }
}