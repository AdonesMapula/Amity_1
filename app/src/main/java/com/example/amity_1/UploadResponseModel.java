package com.example.amity_1;

import com.google.gson.annotations.SerializedName;

public class UploadResponseModel {

    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    public String getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
