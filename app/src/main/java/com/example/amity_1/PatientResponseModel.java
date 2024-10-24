package com.example.amity_1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PatientResponseModel {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Patient> data;


    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Patient> getData() {
        return data;
    }

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
