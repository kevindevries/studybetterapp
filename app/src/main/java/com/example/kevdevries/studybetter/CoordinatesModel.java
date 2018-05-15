package com.example.kevdevries.studybetter;
/**
 * Created by KevdeVries on 09/02/2018.
 *
 * Model for Coordinates Data
 */

public class CoordinatesModel {
    private double latitude;
    private double longitude;
    private String firstName;
    private String year;
    private String subject1;
    private String subject2;
    private String subject3;

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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubject1() {
        return subject1;
    }

    public void setSubject1(String subject1) {
        this.subject1 = subject1;
    }

    public String getSubject2() {
        return subject2;
    }

    public void setSubject2(String subject2) {
        this.subject2 = subject2;
    }

    public String getSubject3() {
        return subject3;
    }

    public void setSubject3(String subject3) {
        this.subject3 = subject3;
    }
}