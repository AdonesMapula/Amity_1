package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
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

        initializeUI();
        setupRecyclerView();
        fetchPatientsFromDatabase();
        setupSearchView();
        setButtonListeners();
    }

    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        homeBtn = findViewById(R.id.homeBtn);
        fileBtn = findViewById(R.id.fileBtn);
        staffBtn = findViewById(R.id.staffBtn);
    }

    private void setupRecyclerView() {
        patientList = new ArrayList<>();
        patientAdapter = new PatientAdapter(patientList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(patientAdapter);
    }

    private void fetchPatientsFromDatabase() {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<PatientResponseModel> call = apiService.getPatients();

        call.enqueue(new Callback<PatientResponseModel>() {
            @Override
            public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Patient> patients = response.body().getData();
                    if (patients.isEmpty()) {
                        showError("No patients found");
                    } else {
                        updatePatientList(patients);
                    }
                } else {
                    showError("Failed to fetch patients: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void updatePatientList(List<Patient> patients) {
        patientList.clear();
        patientList.addAll(patients);
        patientAdapter.notifyDataSetChanged();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchSearchedPatients(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchSearchedPatients(newText);
                return true;
            }
        });

        searchView.setOnClickListener(v -> searchView.setIconified(false));
    }

    private void fetchSearchedPatients(String query) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<PatientResponseModel> call = apiService.searchPatients(query);

        call.enqueue(new Callback<PatientResponseModel>() {
            @Override
            public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatePatientList(response.body().getData());
                } else {
                    showError("Search failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void setButtonListeners() {
        homeBtn.setOnClickListener(v -> openActivity(MainActivity.class));
        fileBtn.setOnClickListener(v -> Toast.makeText(this, "Already in File Activity", Toast.LENGTH_SHORT).show());
        staffBtn.setOnClickListener(v -> openActivity(StaffActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
