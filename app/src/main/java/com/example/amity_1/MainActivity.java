package com.example.amity_1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private static final String TAG = "MainActivity";

    private NetworkService apiService;
    private String patientName, patientAddress, patientPhone, checkupDate, patientDOB, patientGender, patientStatus;
    private TextView dateCheckTxt, dateBirth;
    private Spinner genderSpinner, statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeRetrofit();
    }

    private void initializeUI() {
        dateCheckTxt = findViewById(R.id.dateCheckTxt);
        dateBirth = findViewById(R.id.dateBirth);
        Button addFilesButton = findViewById(R.id.addFiles);
        Button addPatientButton = findViewById(R.id.addPatient);
        ImageButton fileButton = findViewById(R.id.fileBtn);
        ImageButton staffButton = findViewById(R.id.staffBtn);
        genderSpinner = findViewById(R.id.ptntsGender);
        statusSpinner = findViewById(R.id.ptntsStatus);

        setupSpinner(genderSpinner, R.array.gender_options, selectedGender -> {
            patientGender = selectedGender;
            Log.d(TAG, "Selected gender: " + patientGender);
        });

        setupSpinner(statusSpinner, R.array.status_options, selectedStatus -> {
            patientStatus = selectedStatus;
            Log.d(TAG, "Selected status: " + patientStatus);
        });

        addFilesButton.setOnClickListener(v -> launchCamera());
        addPatientButton.setOnClickListener(v -> addPatientToDatabase());
        fileButton.setOnClickListener(v -> openActivity(FileActivity.class));
        staffButton.setOnClickListener(v -> openActivity(StaffActivity.class));
        dateCheckTxt.setOnClickListener(v -> showDatePickerDialog());
        dateBirth.setOnClickListener(v -> showDatePickerDialog1());
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NetworkService.class);
    }

    private void setupSpinner(Spinner spinner, int arrayResource, OnItemSelectedListener listener) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                listener.onItemSelected(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listener.onNothingSelected();
            }
        });
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
                    Log.d(TAG, message);

                    if (response.isSuccessful()) {
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

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

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

        apiService.addPatient(patientName, patientAddress, patientPhone, patientDOB, checkupDate, patientGender, patientStatus)
                .enqueue(new Callback<PatientResponseModel>() {
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
        patientName = ((EditText) findViewById(R.id.ptntsNameTxt)).getText().toString().trim();
        patientAddress = ((EditText) findViewById(R.id.ptntsAddressTxt)).getText().toString().trim();
        patientPhone = ((EditText) findViewById(R.id.ptntsPhoneTxt)).getText().toString().trim();
        patientDOB = dateBirth.getText().toString();
        checkupDate = dateCheckTxt.getText().toString();
    }

    private boolean isPatientDataValid() {
        return !patientName.isEmpty() && !patientAddress.isEmpty() && !patientPhone.isEmpty() && !patientDOB.isEmpty() && !checkupDate.isEmpty();
    }

    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "patient_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to file", e);
            return null;
        }
    }

    private void showImageChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose an image option")
                .setItems(new String[]{"Upload Image", "Take Photo"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Implement upload image logic
                            break;
                        case 1:
                            launchCamera();
                            break;
                    }
                })
                .show();
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        showDatePickerDialog((listener, year, month, dayOfMonth) -> dateCheckTxt.setText(dayOfMonth + "/" + (month + 1) + "/" + year));
    }

    private void showDatePickerDialog1() {
        showDatePickerDialog((listener, year, month, dayOfMonth) -> dateBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year));
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    interface OnItemSelectedListener {
        void onItemSelected(String item);
        default void onNothingSelected() {}
    }
}
