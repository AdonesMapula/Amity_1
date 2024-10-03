package com.example.amity_1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PatientFileActivity extends AppCompatActivity {

    private TextView fileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_file); // Ensure this layout exists

        fileTextView = findViewById(R.id.fileTextView); // Ensure this ID exists

        // Get the file ID from the intent
        String fileId = getIntent().getStringExtra("FILE_ID");
        if (fileId != null) {
            fileTextView.setText("Displaying file for ID: " + fileId); // Placeholder logic
        } else {
            fileTextView.setText("No file ID provided");
        }
    }
}
