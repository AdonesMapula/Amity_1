package com.example.amity_1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientResponseModel {

    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    @SerializedName("patients") // Change this to match your JSON structure
    private List<Patient> data; // Change this to match your JSON structure

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Patient> getData() {
        return data;
    }
}
