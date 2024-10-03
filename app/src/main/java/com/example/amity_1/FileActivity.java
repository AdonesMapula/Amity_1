package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        homeBtn = findViewById(R.id.homeBtn);
        fileBtn = findViewById(R.id.fileBtn);
        staffBtn = findViewById(R.id.staffBtn);

        // Initialize patient list with placeholder data
        patientList = new ArrayList<>();
        patientList.add(new Patient("John Doe", "123-456-7890", "file_1")); // Placeholder data
        patientList.add(new Patient("Jane Smith", "098-765-4321", "file_2")); // Placeholder data

        // Set up RecyclerView
        patientAdapter = new PatientAdapter(patientList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(patientAdapter);

        // Set up SearchView to filter patient list
        setupSearchView();

        // Set button click listeners
        homeBtn.setOnClickListener(v -> openHomeActivity());
        fileBtn.setOnClickListener(v -> Toast.makeText(this, "Already in File Activity", Toast.LENGTH_SHORT).show());
        staffBtn.setOnClickListener(v -> openStaffActivity());
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
