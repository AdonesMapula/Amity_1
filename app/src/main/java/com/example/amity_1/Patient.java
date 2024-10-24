package com.example.amity_1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Patient {
    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String gender;
    private String status;
    private String birthday;
    private String checkupDate;
    private String createdAt;
    private String updatedAt;
    private VitalSigns vitals;

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
        this.vitals = vitals;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getGender() { return gender; }
    public String getStatus() { return status; }
    public String getBirthday() { return birthday; }
    public String getCheckupDate() {
        return checkupDate != null ? checkupDate : "No Checkup Date";
    }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public VitalSigns getVitals() { return vitals; }

    public String getFormattedCheckupDate() {
        if (checkupDate == null) return "No Checkup Date";

        String formattedDate = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-mm-dd");

        try {
            Date date = inputFormat.parse(checkupDate);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }


    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", status='" + status + '\'' +
                ", birthday='" + birthday + '\'' +
                ", checkupDate='" + checkupDate + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", vitals=" + vitals +
                '}';
    }
}
