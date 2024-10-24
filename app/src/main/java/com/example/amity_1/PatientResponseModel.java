package com.example.amity_1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientResponseModel {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Patient> data;

    // Old properties (if necessary for backward compatibility)
    @SerializedName("success")
    private String success; // Retain this only if needed

    @SerializedName("message")
    private String message; // Retain this only if needed

    public String getStatus() {
        return status;
    }

    public List<Patient> getData() {
        return data;
    }

    // Getters for old properties if needed
    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
