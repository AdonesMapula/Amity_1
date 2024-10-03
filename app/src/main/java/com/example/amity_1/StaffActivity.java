package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import Button class
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity {
    private RecyclerView staffRecyclerView;
    private StaffAdapter staffAdapter;
    private List<Staff> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff); // Change to your layout file name

        staffRecyclerView = findViewById(R.id.staffRecyclerView);
        staffList = new ArrayList<>();

        // Sample data (you should replace this with data from your database)
        staffList.add(new Staff("Dr. John Doe", "Doctor", R.drawable.logo));
        staffList.add(new Staff("Nurse Jane Smith", "Nurse", R.drawable.logo));
        staffList.add(new Staff("Dr. John Doe", "Doctor", R.drawable.logo));
        staffList.add(new Staff("Nurse Jane Smith", "Nurse", R.drawable.logo));
        staffList.add(new Staff("Dr. John Doe", "Doctor", R.drawable.logo));
        staffList.add(new Staff("Nurse Jane Smith", "Nurse", R.drawable.logo));

        staffAdapter = new StaffAdapter(staffList);
        staffRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Change number for columns as needed
        staffRecyclerView.setAdapter(staffAdapter);

        // Set up image buttons
        setupImageButtons();

        // Setup logout button
        setupLogoutButton();
    }

    private void setupImageButtons() {
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        ImageButton fileBtn = findViewById(R.id.fileBtn);
        ImageButton staffBtn = findViewById(R.id.staffBtn);

        homeBtn.setOnClickListener(v -> openHomeActivity());
        fileBtn.setOnClickListener(v -> openFileActivity());
        staffBtn.setOnClickListener(v -> {
            // Toast to indicate the user is already in Staff Activity
        });
    }

    private void setupLogoutButton() {
        Button logoutBtn = findViewById(R.id.addFiles2);
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear user session data if you have any
        // For example, you might want to clear shared preferences
        // SharedPreferences sharedPreferences = getSharedPreferences("YourPrefs", MODE_PRIVATE);
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.clear();
        // editor.apply();

        // Navigate back to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class); // Replace with your login activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        startActivity(intent);
        finish(); // Optional: Call finish() if you want to close the current activity
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class); // Replace with your home activity
        startActivity(intent);
    }

    private void openFileActivity() {
        Intent intent = new Intent(this, FileActivity.class); // Replace with your file activity
        startActivity(intent);
    }
}
