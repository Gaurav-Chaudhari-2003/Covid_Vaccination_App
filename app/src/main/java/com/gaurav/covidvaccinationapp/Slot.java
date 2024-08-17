package com.gaurav.covidvaccinationapp;

public class Slot {

    private String slotId;
    private String date;
    private String time;
    private String location;
    private String slotBooked;
    private String slotBookedPersonID;
    private String slotGeneratorPersonID;
    private String vaccineType;

    public Slot() {
        // Required empty public constructor
    }

    public Slot(String slotId, String date, String time, String location, String slotBooked, String slotBookedPersonID, String slotGeneratorPersonID, String vaccineType) {
        this.slotId = slotId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.slotBooked = slotBooked;
        this.slotBookedPersonID = slotBookedPersonID;
        this.slotGeneratorPersonID = slotGeneratorPersonID;
        this.vaccineType = vaccineType;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSlotBooked() {
        return slotBooked;
    }

    public void setSlotBooked(String slotBooked) {
        this.slotBooked = slotBooked;
    }

    public String getSlotBookedPersonID() {
        return slotBookedPersonID;
    }

    public void setSlotBookedPersonID(String slotBookedPersonID) {
        this.slotBookedPersonID = slotBookedPersonID;
    }

    public String getSlotGeneratorPersonID() {
        return slotGeneratorPersonID;
    }

    public void setSlotGeneratorPersonID(String slotGeneratorPersonID) {
        this.slotGeneratorPersonID = slotGeneratorPersonID;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }
}
