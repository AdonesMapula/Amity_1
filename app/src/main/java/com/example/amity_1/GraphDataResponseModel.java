package com.example.amity_1;

import com.google.gson.annotations.SerializedName;

public class GraphDataResponseModel {

    @SerializedName("success")
    private String success;

    @SerializedName("data")
    private GraphDataModel data;

    public String getSuccess() {
        return success;
    }

    public GraphDataModel getData() {
        return data;
    }

    public class GraphDataModel {
        @SerializedName("daily")
        private int daily;

        @SerializedName("weekly")
        private int weekly;

        @SerializedName("monthly")
        private int monthly;

        public int getDaily() {
            return daily;
        }

        public int getWeekly() {
            return weekly;
        }

        public int getMonthly() {
            return monthly;
        }
    }
}
