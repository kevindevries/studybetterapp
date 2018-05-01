package com.example.kevdevries.studybetter;


public class CoordinatesModel {
    private double latitude;
    private double longitude;
    private String firstName;

    public CoordinatesModel(){

    }

    public CoordinatesModel(double latitude, double longitude, String firstName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.firstName = firstName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}