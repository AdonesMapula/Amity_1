package com.example.amity_1;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDetailModel {

    @SerializedName("name")
    private String name;

    @SerializedName("password")
    private String password;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
