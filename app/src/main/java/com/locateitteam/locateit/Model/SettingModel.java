package com.locateitteam.locateit.Model;

public class SettingModel {

    // variables
    String metric;
    String preferredMapType;
    String preferredTravelType;

    // constructor
    public SettingModel(String metric, String preferredLandmark, String preferredTravelType) {
        this.metric = metric;
        this.preferredMapType = preferredLandmark;
        this.preferredTravelType = preferredTravelType;
    }

    // preferredLandmark constructor
    public SettingModel(String preferredLandmark) {
        this.preferredMapType = preferredLandmark;
    }

    // blank constructor
    public SettingModel() {
    }

    // accessors and mutators
    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getPreferredMapType() {
        return preferredMapType;
    }

    public void setPreferredMapType(String preferredMapType) {
        this.preferredMapType = preferredMapType;
    }

    public String getPreferredTravelType() {
        return preferredTravelType;
    }

    public void setPreferredTravelType(String preferredTravelType) {
        this.preferredTravelType = preferredTravelType;
    }
}
