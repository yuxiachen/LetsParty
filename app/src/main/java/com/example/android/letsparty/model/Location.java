package com.example.android.letsparty.model;

import androidx.annotation.NonNull;

public class Location {
    String addressLine;
    String city;
    String country;
    String state;
    String zipCode;

    public Location() {}

    public Location(String city, String state) {
        this.city = city;
        this.state = state;
    }


    public Location(String city, String country, String state, String zipCode) {
        this.city = city;
        this.country = country;
        this.state = state;
        this.zipCode = zipCode;
    }

    public Location(String addressLine, String city, String state, String country, String zipCode) {
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.country = country;
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

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (city != null) sb.append(city);
        if (!sb.toString().isEmpty()) sb.append(", ");
        if (state != null) sb.append(state);
        return sb.toString();
    }
}
