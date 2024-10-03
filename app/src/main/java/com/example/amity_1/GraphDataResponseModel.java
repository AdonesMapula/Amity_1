package com.example.amity_1;

import java.util.List;

public class GraphDataResponseModel {
    private List<GraphData> data;

    public List<GraphData> getData() {
        return data;
    }
    public class GraphData {
        private double xValue;
        private double yValue;

        public double getXValue() {
            return xValue;
        }

        public double getYValue() {
            return yValue;
        }
    }
}

