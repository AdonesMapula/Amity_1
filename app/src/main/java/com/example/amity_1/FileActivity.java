package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ImageButton homeBtn, fileBtn, staffBtn;
    private PatientAdapter patientAdapter;
    private List<Patient> patientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        // Initialize UI elements
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        homeBtn = findViewById(R.id.homeBtn);
        fileBtn = findViewById(R.id.fileBtn);
        staffBtn = findViewById(R.id.staffBtn);

        // Initialize the patient list
        patientList = new ArrayList<>();

        // Set up RecyclerView
        patientAdapter = new PatientAdapter(patientList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(patientAdapter);

        // Fetch patients from the database
        fetchPatientsFromDatabase();

        // Set up SearchView to filter patient list
        setupSearchView();

        // Set button click listeners
        homeBtn.setOnClickListener(v -> openHomeActivity());
        fileBtn.setOnClickListener(v -> Toast.makeText(this, "Already in File Activity", Toast.LENGTH_SHORT).show());
        staffBtn.setOnClickListener(v -> openStaffActivity());
    }

    private void fetchPatientsFromDatabase() {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<PatientResponseModel> call = apiService.getPatients(); // Use PatientResponseModel here

        call.enqueue(new Callback<PatientResponseModel>() {
            @Override
            public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Assuming the response model has a method to get the list of patients
                    List<Patient> patients = response.body().getData(); // Fetch patient data directly

                    if (patients.isEmpty()) {
                        Toast.makeText(FileActivity.this, "No patients found", Toast.LENGTH_SHORT).show();
                    } else {
                        patientList.clear();
                        patientList.addAll(patients);
                        patientAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(FileActivity.this, "Failed to fetch patients: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                Toast.makeText(FileActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                patientAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openStaffActivity() {
        Intent intent = new Intent(this, StaffActivity.class);
        startActivity(intent);
    }
}
