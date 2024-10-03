package com.example.amity_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyGraphFragment extends Fragment {

    private NetworkService apiService; // Declare the API service

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_graph, container, false);
        initializeRetrofit(); // Initialize Retrofit
        fetchPatients(); // Fetch patient data
        return view;
    }

    private void initializeRetrofit() {
        apiService = NetworkClient.getClient().create(NetworkService.class); // Replace with your RetrofitClient instance
    }

    private void fetchPatients() {
        apiService.getPatients().enqueue(new Callback<PatientResponseModel>() {
            @Override
            public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String success = response.body().getSuccess();
                    String message = response.body().getMessage();

                    // Check if the success flag indicates a successful retrieval
                    if ("true".equals(success)) {
                        // Use success and message as needed
                        // For example, show a toast or update the UI
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle case when success is not true
                        Toast.makeText(getContext(), "Data retrieval failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when the response is not successful
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                // Handle failure
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
