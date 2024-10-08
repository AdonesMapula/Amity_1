package com.example.amity_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
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
        setContentView(R.layout.activity_staff);

        staffRecyclerView = findViewById(R.id.staffRecyclerView);
        staffList = new ArrayList<>();

        // Sample staff data (you can replace this with your actual data)
        staffList.add(new Staff("Dr. Cristian Jeff", "Available: 8AM-6PM | WED-SAT", R.drawable.emit));
        staffList.add(new Staff("Nurse Jane Smith", "Available: 8AM-6PM | WED-SAT", R.drawable.logo));
        staffList.add(new Staff("Dr. John Doe", "Available: 8AM-6PM | WED-SAT", R.drawable.logo));
        staffList.add(new Staff("Nurse Jane Smith", "Available: 8AM-6PM | WED-SAT", R.drawable.logo));
        staffList.add(new Staff("Dr. John Doe", "Available: 8AM-6PM | WED-SAT", R.drawable.logo));
        staffList.add(new Staff("Nurse Jane Smith", "Available: 8AM-6PM | WED-SAT", R.drawable.logo));

        staffAdapter = new StaffAdapter(staffList);
        staffRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
            // Optional: Toast to indicate the user is already in Staff Activity
        });
    }

    private void setupLogoutButton() {
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        // Clear user session data
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();

        // Navigate back to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class); // Replace with your login activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        startActivity(intent);

        // Close the app
        finishAffinity(); // Finish this activity and all parent activities
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openFileActivity() {
        Intent intent = new Intent(this, FileActivity.class);
        startActivity(intent);
    }
}
