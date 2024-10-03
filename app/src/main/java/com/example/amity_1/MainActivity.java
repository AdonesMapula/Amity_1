package com.example.amity_1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

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
    private static final String EMPTY_FIELD_MESSAGE = "Please fill in all fields";
    private static final String TAG = "MainActivity"; // Define a tag for logging

    private NetworkService apiService;
    private String patientName, patientAddress, patientPhone, checkupDate;
    private TextView dateCheckTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI and Retrofit
        initializeUI();
        initializeRetrofit();
    }

    private void initializeUI() {
        dateCheckTxt = findViewById(R.id.dateCheckTxt);
        Button addFilesButton = findViewById(R.id.addFiles);
        Button addPatientButton = findViewById(R.id.addPatient);
        ImageButton fileButton = findViewById(R.id.fileBtn);
        ImageButton staffButton = findViewById(R.id.staffBtn);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Set up ViewPager adapter for graphs
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Button click listeners
        addFilesButton.setOnClickListener(v -> launchCamera());
        addPatientButton.setOnClickListener(v -> addPatientToDatabase());
        fileButton.setOnClickListener(v -> openActivity(FileActivity.class));
        staffButton.setOnClickListener(v -> openActivity(StaffActivity.class));
        dateCheckTxt.setOnClickListener(v -> showDatePickerDialog());
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NetworkService.class);
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            uploadDocument(imageBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadDocument(Bitmap imageBitmap) {
        if (!isPatientDataValid()) return;

        File file = convertBitmapToFile(imageBitmap);
        if (file != null) {
            RequestBody patientNamePart = RequestBody.create(MediaType.parse("text/plain"), patientName);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            apiService.uploadDocument(patientNamePart, filePart).enqueue(new Callback<UploadResponseModel>() {
                @Override
                public void onResponse(Call<UploadResponseModel> call, Response<UploadResponseModel> response) {
                    String message = response.isSuccessful() ? "Document uploaded successfully" : "Failed to upload document: " + response.message();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, message); // Log the response message

                    if (response.isSuccessful()) {
                        // After successful upload, upload the image to the Hostinger gallery
                        uploadToHostingerGallery(file);
                    }
                }

                @Override
                public void onFailure(Call<UploadResponseModel> call, Throwable t) {
                    Log.e(TAG, "Document upload failed", t);
                    Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Image conversion failed");
            Toast.makeText(this, "Image conversion failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToHostingerGallery(File file) {
        // Define the URL to your Hostinger gallery
        String uploadUrl = "https://cornflowerblue-quetzal-932463.hostingersite.com/uploads/";

        // Create a new RequestBody for the file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Create a Retrofit interface method for uploading to Hostinger
        apiService.uploadToHostingerGallery(body).enqueue(new Callback<UploadResponseModel>() {
            @Override
            public void onResponse(Call<UploadResponseModel> call, Response<UploadResponseModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Image uploaded to Hostinger gallery successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Image uploaded to Hostinger gallery successfully");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to upload image to Hostinger gallery: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed to upload image to Hostinger gallery: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UploadResponseModel> call, Throwable t) {
                Log.e(TAG, "Image upload to Hostinger gallery failed", t);
                Toast.makeText(MainActivity.this, "Image upload to Hostinger gallery failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addPatientToDatabase() {
        collectPatientData();

        if (!isPatientDataValid()) {
            Toast.makeText(this, EMPTY_FIELD_MESSAGE, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Validation failed: " + EMPTY_FIELD_MESSAGE);
            return;
        }

        apiService.addPatient(patientName, patientAddress, patientPhone, checkupDate).enqueue(new Callback<PatientResponseModel>() {
            @Override
            public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Patient added successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Patient added successfully: " + response.body());
                    showImageChoiceDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add patient: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed to add patient: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                Log.e(TAG, "Failed to add patient", t);
                Toast.makeText(MainActivity.this, "Failed to add patient: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void collectPatientData() {
        patientName = getTextFromEditText(R.id.ptntsNameTxt);
        patientAddress = getTextFromEditText(R.id.ptntsAddressTxt);
        patientPhone = getTextFromEditText(R.id.ptntsPhoneTxt);
        checkupDate = dateCheckTxt.getText().toString().trim();

        Log.d(TAG, "Collected data: Name = " + patientName + ", Address = " + patientAddress + ", Phone = " + patientPhone + ", Checkup Date = " + checkupDate);
    }

    private boolean isPatientDataValid() {
        return !patientName.isEmpty() && !patientAddress.isEmpty() && !patientPhone.isEmpty() && !checkupDate.isEmpty();
    }

    private String getTextFromEditText(int editTextId) {
        return ((EditText) findViewById(editTextId)).getText().toString().trim();
    }

    private void showImageChoiceDialog() {
        String[] options = {"Open Camera", "Select from Gallery", "Exit"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> handleImageChoice(which))
                .show();
    }

    private void handleImageChoice(int which) {
        switch (which) {
            case 0:
                launchCamera();
                break;
            case 1:
                openGallery();
                break;
            case 2:
                // Exit
                break;
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the DatePickerDialog with custom theme
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerTheme, // Apply your custom theme here
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    checkupDate = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay); // Format date as YYYY-MM-DD
                    dateCheckTxt.setText(checkupDate);
                    Log.d(TAG, "Selected date: " + checkupDate); // Log the selected date
                },
                year, month, day
        );

        datePickerDialog.show(); // Show the dialog
    }


    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d(TAG, "Bitmap converted to file: " + file.getAbsolutePath()); // Log the file path
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Failed to convert bitmap to file", e);
            return null;
        }
    }

    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}
