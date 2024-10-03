package com.example.amity_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private NetworkService apiService; // Retrofit API service instance
    private String patientName; // Keep track of the patient name for upload

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize buttons and fields
        Button addFilesButton = findViewById(R.id.addFiles);
        Button addPatientButton = findViewById(R.id.addPatient);
        ImageButton fileButton = findViewById(R.id.fileBtn);
        ImageButton staffButton = findViewById(R.id.staffBtn);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Initialize Retrofit service
        initializeRetrofit();

        // Set up the ViewPager with an adapter for graphs
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Set up OnClickListeners for buttons
        addFilesButton.setOnClickListener(v -> launchCamera());
        addPatientButton.setOnClickListener(v -> addPatientToDatabase());

        // File button listener
        fileButton.setOnClickListener(v -> openFileActivity());

        // Staff button listener
        staffButton.setOnClickListener(v -> openStaffPage());
    }

    // Method to initialize the Retrofit service
    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL) // Replace with your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NetworkService.class); // Initialize Retrofit service
    }

    // Method to launch the camera for scanning documents
    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Handle the result after the user takes a photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // Upload the scanned document with the patient name
            uploadDocument(imageBitmap, patientName);
        }
    }

    // Method to upload a document (image) to the database
    private void uploadDocument(Bitmap imageBitmap, String patientName) {
        if (!patientName.isEmpty()) {
            RequestBody patientNamePart = RequestBody.create(MediaType.parse("text/plain"), patientName);
            File file = convertBitmapToFile(imageBitmap);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            apiService.uploadDocument(patientNamePart, filePart).enqueue(new Callback<UploadResponseModel>() {
                @Override
                public void onResponse(Call<UploadResponseModel> call, Response<UploadResponseModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Document uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to upload document: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponseModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter a patient's name first", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add a patient record to the database and then upload a document
    private void addPatientToDatabase() {
        patientName = ((EditText) findViewById(R.id.ptntsNameTxt)).getText().toString();
        String checkupDate = ((EditText) findViewById(R.id.dateCheckTxt)).getText().toString();

        if (!patientName.isEmpty() && !checkupDate.isEmpty()) {
            apiService.addPatient(patientName, checkupDate).enqueue(new Callback<PatientResponseModel>() {
                @Override
                public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Patient added successfully", Toast.LENGTH_SHORT).show();

                        // After adding the patient, launch the camera to capture and upload a document
                        launchCamera();

                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add patient: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to add patient", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert bitmap to file
    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Method to open the file activity
    private void openFileActivity() {
        Intent intent = new Intent(this, FileActivity.class);
        startActivity(intent);
    }

    // Method to open the staff page
    private void openStaffPage() {
        Intent intent = new Intent(this, StaffActivity.class);
        startActivity(intent);
    }
}
