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
        Call<ResponseBody> call = apiService.getPatients();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Patient> patients = parsePatientsFromResponse(response.body());
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FileActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private List<Patient> parsePatientsFromResponse(ResponseBody responseBody) {
        try {
            String json = responseBody.string(); // Convert the response body to a string
            // Assuming your JSON response structure includes a "patients" array
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("patients");

            List<Patient> patients = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject patientObject = jsonArray.getJSONObject(i);
                Patient patient = new Patient(
                        patientObject.getString("id"),
                        patientObject.getString("name"),
                        patientObject.getString("phone_number"),
                        patientObject.getString("address"),
                        patientObject.getString("checkup_date"), // Include the checkup date
                        patientObject.getString("created_at"), // Include created_at
                        patientObject.getString("updated_at") // Include updated_at
                );
                patients.add(patient);
            }
            return patients;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
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
