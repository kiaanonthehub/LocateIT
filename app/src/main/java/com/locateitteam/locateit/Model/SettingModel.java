package com.locateitteam.locateit.Model;

public class SettingModel {

    // variables
    String metric;
    String preferredLandmark;

    // constructor
    public SettingModel(String metric, String preferredLandmark) {
        this.metric = metric;
        this.preferredLandmark = preferredLandmark;
    }

    // preferredLandmark constructor
    public SettingModel(String preferredLandmark) {
        this.preferredLandmark = preferredLandmark;
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

    public String getPreferredLandmark() {
        return preferredLandmark;
    }

    public void setPreferredLandmark(String preferredLandmark) {
        this.preferredLandmark = preferredLandmark;
    }
}
