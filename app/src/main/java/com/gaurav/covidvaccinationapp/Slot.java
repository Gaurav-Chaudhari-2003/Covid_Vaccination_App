package com.gaurav.covidvaccinationapp;

import java.util.Objects;

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

    public Slot(String slotId, String date, String time, String location, String slotBooked,
                String slotBookedPersonID, String slotGeneratorPersonID, String vaccineType) {
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

    @Override
    public String toString() {
        return "Slot{" +
                "slotId='" + slotId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", slotBooked='" + slotBooked + '\'' +
                ", slotBookedPersonID='" + slotBookedPersonID + '\'' +
                ", slotGeneratorPersonID='" + slotGeneratorPersonID + '\'' +
                ", vaccineType='" + vaccineType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return Objects.equals(slotId, slot.slotId) &&
                Objects.equals(date, slot.date) &&
                Objects.equals(time, slot.time) &&
                Objects.equals(location, slot.location) &&
                Objects.equals(slotBooked, slot.slotBooked) &&
                Objects.equals(slotBookedPersonID, slot.slotBookedPersonID) &&
                Objects.equals(slotGeneratorPersonID, slot.slotGeneratorPersonID) &&
                Objects.equals(vaccineType, slot.vaccineType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId, date, time, location, slotBooked, slotBookedPersonID, slotGeneratorPersonID, vaccineType);
    }
}
