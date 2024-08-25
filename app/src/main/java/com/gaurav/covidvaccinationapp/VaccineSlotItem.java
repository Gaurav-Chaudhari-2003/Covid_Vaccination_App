package com.gaurav.covidvaccinationapp;

public class VaccineSlotItem {
    private final String summary;
    private final String details;
    private final boolean showModifyButton;
    private final String slotId;
    private final String vaccineType;
    private boolean expanded;

    public VaccineSlotItem(String summary, String details, boolean showModifyButton, String slotId, String vaccineType) {
        this.summary = summary;
        this.details = details;
        this.showModifyButton = showModifyButton;
        this.slotId = slotId;
        this.vaccineType = vaccineType;
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

    public boolean isShowModifyButton() {
        return showModifyButton;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getVaccineType() {
        return vaccineType;
    }
}
