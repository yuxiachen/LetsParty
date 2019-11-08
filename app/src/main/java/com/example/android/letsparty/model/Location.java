package com.example.android.letsparty.model;

public class Location {
    String addressLine;
    String city;
    String country;
    String state;
    String zipCode;

    public Location() {}

    public Location(String addressLine, String city, String country, String state, String zipCode) {
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}