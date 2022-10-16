package com.locateitteam.locateit.Model;

public class SettingModel {

    // variables
    String metric;
    String preferredMapType;
    String preferredTravelType;
    boolean activate;

    // constructor
    public SettingModel(String metric, String preferredLandmark, String preferredTravelType,boolean activate) {
        this.metric = metric;
        this.preferredMapType = preferredLandmark;
        this.preferredTravelType = preferredTravelType;
        this.activate = activate;
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

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }
}
