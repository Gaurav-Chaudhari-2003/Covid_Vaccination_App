package com.gaurav.covidvaccinationapp;

public class VaccineSlotItem {
    private final String summary;
    private final String details;
    private boolean expanded;

    public VaccineSlotItem(String summary, String details) {
        this.summary = summary;
        this.details = details;
        this.expanded = false;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetails() {
        return details;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
