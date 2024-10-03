package com.example.amity_1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyGraphFragment extends Fragment {

    private GraphView graph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daily_graph, container, false);
        graph = rootView.findViewById(R.id.graph);

        // Fetch and display data
        fetchGraphData();

        return rootView;
    }

    private void fetchGraphData() {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<GraphDataResponseModel> call = apiService.getDailyGraphData(); // Use your API endpoint here

        call.enqueue(new Callback<GraphDataResponseModel>() {
            @Override
            public void onResponse(Call<GraphDataResponseModel> call, Response<GraphDataResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Assuming response.body().getData() returns a list of your data points
                    List<GraphDataResponseModel.GraphData> graphDataList = response.body().getData();
                    displayGraphData(graphDataList);
//                } else {
//                    Toast.makeText(getContext(), "Failed to fetch graph data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GraphDataResponseModel> call, Throwable t) {
//                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayGraphData(List<GraphDataResponseModel.GraphData> graphDataList) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        for (GraphDataResponseModel.GraphData data : graphDataList) {
            // Assuming GraphData has methods to get x and y values
            series.appendData(new DataPoint(data.getXValue(), data.getYValue()), true, graphDataList.size());
        }

        // Set series color and other attributes
        series.setColor(Color.GREEN); // Change to desired color
        series.setThickness(8); // Set line thickness
        series.setDrawDataPoints(true); // Enable drawing data points
        series.setDataPointsRadius(10); // Set radius for data points
        series.setAnimated(true); // Enable animation

        // Optionally set background color
        graph.setBackgroundColor(Color.WHITE); // Set background color

        // Add the series to the graph
        graph.addSeries(series);

        // Optional: Customize grid colors
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);

        // Customize the viewport
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(12); // Adjust this according to your data
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(24); // Adjust this according to your data
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
    }
}
