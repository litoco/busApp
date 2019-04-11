package com.example.testapp;

public class SingleRowItemHolder {

    private int imageId;
    private String name, details;

    public SingleRowItemHolder(int imageId, String name, String details) {
        this.imageId = imageId;
        this.name = name;
        this.details = details;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
