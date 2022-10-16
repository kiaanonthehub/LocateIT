package com.locateitteam.locateit.Model.ViewModel;

public class SavedPlacesViewModel {

    String name, address;
    Double rating, lat, lng;

    public SavedPlacesViewModel() {
    }

    // constructor to read from firebase
    public SavedPlacesViewModel(String address, Double lat, Double lng, String name, Double rating) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
