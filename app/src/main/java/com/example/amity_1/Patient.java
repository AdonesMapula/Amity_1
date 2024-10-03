package com.example.amity_1;

public class Patient {
    private String id; // Unique identifier for the patient
    private String name; // Patient's name
    private String phoneNumber; // Patient's phone number
    private String address; // Patient's address
    private String checkupDate; // Patient's checkup date
    private String createdAt; // Record creation timestamp
    private String updatedAt; // Record update timestamp

    // Constructor for the Patient class
    public Patient(String id, String name, String phoneNumber, String address, String checkupDate, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.checkupDate = checkupDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCheckupDate() {
        return checkupDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
