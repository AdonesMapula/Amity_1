package com.example.amity_1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

    private NetworkService apiService; // Retrofit API service instance
    private String patientName; // Patient name for upload
    private String patientAddress; // Patient's address
    private String patientPhone; // Patient's phone number
    private String checkupDate; // Selected date

    private TextView dateCheckTxt; // To display the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initializeUI();

        // Initialize Retrofit service
        initializeRetrofit();
    }

    // Method to initialize UI components
    private void initializeUI() {
        Button addFilesButton = findViewById(R.id.addFiles);
        Button addPatientButton = findViewById(R.id.addPatient);
        ImageButton fileButton = findViewById(R.id.fileBtn);
        ImageButton staffButton = findViewById(R.id.staffBtn);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        dateCheckTxt = findViewById(R.id.dateCheckTxt); // Reference to the date TextView

        // Set up ViewPager with an adapter for graphs
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Set OnClickListeners for buttons
        addFilesButton.setOnClickListener(v -> launchCamera());
        addPatientButton.setOnClickListener(v -> addPatientToDatabase());
        fileButton.setOnClickListener(v -> openFileActivity());
        staffButton.setOnClickListener(v -> openStaffPage());

        // Set OnClickListener to show DatePickerDialog
        dateCheckTxt.setOnClickListener(v -> showDatePickerDialog());
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
            uploadDocument(imageBitmap); // Upload the scanned document
        }
    }

    // Method to upload a document (image) to the database
    private void uploadDocument(Bitmap imageBitmap) {
        if (!patientName.isEmpty()) {
            File file = convertBitmapToFile(imageBitmap);
            if (file != null) {
                RequestBody patientNamePart = RequestBody.create(MediaType.parse("text/plain"), patientName);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

                // Make the API call to upload the document
                apiService.uploadDocument(patientNamePart, filePart).enqueue(new Callback<UploadResponseModel>() {
                    @Override
                    public void onResponse(Call<UploadResponseModel> call, Response<UploadResponseModel> response) {
                        String message = response.isSuccessful() ? "Document uploaded successfully" : "Failed to upload document: " + response.message();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<UploadResponseModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Image conversion failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a patient's name first", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add a patient record to the database
    private void addPatientToDatabase() {
        patientName = ((EditText) findViewById(R.id.ptntsNameTxt)).getText().toString().trim();
        patientAddress = ((EditText) findViewById(R.id.ptntsAddressTxt)).getText().toString().trim();
        patientPhone = ((EditText) findViewById(R.id.ptntsPhoneTxt)).getText().toString().trim();
        checkupDate = dateCheckTxt.getText().toString().trim();

        if (!patientName.isEmpty() && !patientAddress.isEmpty() && !patientPhone.isEmpty() && !checkupDate.isEmpty()) {
            apiService.addPatient(patientName, patientAddress, patientPhone, checkupDate).enqueue(new Callback<PatientResponseModel>() {
                @Override
                public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                    String message = response.isSuccessful() ? "Patient added successfully" : "Failed to add patient: " + response.message();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (response.isSuccessful()) {
                        // Show options to open the camera, gallery, or exit
                        showImageChoiceDialog();
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


    // Method to show dialog for image selection choice
    private void showImageChoiceDialog() {
        String[] options = {"Open Camera", "Select from Gallery", "Exit"};

        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Open Camera
                            launchCamera();
                            break;
                        case 1: // Select from Gallery
                            openGallery();
                            break;
                        case 2: // Exit
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    // Method to open the gallery for selecting images
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE);
    }

    // Method to show a DatePickerDialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    checkupDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateCheckTxt.setText(checkupDate); // Display the selected date in the TextView
                }, year, month, day);

        datePickerDialog.show();
    }

    // Helper method to convert bitmap to file
    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "image.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if the file conversion fails
        }
    }

    // Method to open the file activity
    private void openFileActivity() {
        startActivity(new Intent(this, FileActivity.class));
    }

    // Method to open the staff page
    private void openStaffPage() {
        startActivity(new Intent(this, StaffActivity.class));
    }
}
