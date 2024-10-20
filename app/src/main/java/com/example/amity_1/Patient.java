package com.example.amity_1;

public class Patient {
    private String id; // Unique identifier for the patient
    private String name; // Patient's name
    private String phoneNumber; // Patient's phone number
    private String address; // Patient's address
    private String gender; // Patient's gender
    private String status; // Patient's status
    private String birthday; // Patient's date of birth
    private String checkupDate; // Patient's checkup date
    private String createdAt; // Record creation timestamp
    private String updatedAt; // Record update timestamp
    private VitalSigns vitals; // Patient's vital signs

    // Constructor for the Patient class
    public Patient(String id, String name, String phoneNumber, String address, String gender, String status,
                   String birthday, String checkupDate, String createdAt, String updatedAt, VitalSigns vitals) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.status = status;
        this.birthday = birthday;
        this.checkupDate = checkupDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.vitals = vitals; // Assigning the VitalSigns object
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

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public String getBirthday() {
        return birthday;
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

    public VitalSigns getVitals() {
        return vitals; // Return the VitalSigns object
    }
}
