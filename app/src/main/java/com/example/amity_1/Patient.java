package com.example.amity_1;

public class Patient {
    private String name;
    private String phone;
    private String fileId;

    public Patient(String name, String phone, String fileId) {
        this.name = name;
        this.phone = phone;
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getFileId() {
        return fileId;
    }
}
