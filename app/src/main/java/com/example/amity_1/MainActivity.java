package com.example.amity_1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import androidx.annotation.Nullable;
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
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final String TAG = "MainActivity";

    private NetworkService apiService;
    private String patientName, patientAddress, patientPhone, checkupDate, patientDOB, patientGender, patientStatus;
    private String bloodPressure, pulseRate, respRate, weight, temperature, cc, pe, dx, meds, labs;
    private TextView dateCheckTxt, dateBirth;
    private Spinner genderSpinner, statusSpinner;
    private Uri imageUri;

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
        genderSpinner = findViewById(R.id.ptntsGender);
        statusSpinner = findViewById(R.id.ptntsStatus);

        setupSpinners();
        setupButtons();
        setupDatePickers();
    }

    private void setupSpinners() {
        setupSpinner(genderSpinner, R.array.gender_options, selected -> patientGender = selected);
        setupSpinner(statusSpinner, R.array.status_options, selected -> patientStatus = selected);
    }

    private void setupButtons() {
        findViewById(R.id.addFiles).setOnClickListener(v -> showImageChoiceDialog());
        findViewById(R.id.addPatient).setOnClickListener(v -> collectAndAddPatientData());
        findViewById(R.id.fileBtn).setOnClickListener(v -> openActivity(FileActivity.class));
        findViewById(R.id.staffBtn).setOnClickListener(v -> openActivity(StaffActivity.class));
    }

    private void setupDatePickers() {
        dateCheckTxt.setOnClickListener(v -> showDatePickerDialog(dateCheckTxt));
        dateBirth.setOnClickListener(v -> showDatePickerDialog(dateBirth));
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
                listener.onItemSelected(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showImageChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose an image option")
                .setItems(new String[]{"Upload Image", "Take Photo"}, (dialog, which) -> {
                    if (which == 0) openGallery();
                    else if (which == 1) launchCamera();
                })
                .show();
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                uploadDocument((Bitmap) data.getExtras().get("data"));
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                uploadImageFromGallery(data.getData());
            }
        }
    }

    private void uploadImageFromGallery(Uri uri) {
        uploadFile(new File(uri.getPath()));
    }

    private void uploadDocument(Bitmap imageBitmap) {
        if (!isPatientDataValid()) return;
        uploadFile(convertBitmapToFile(imageBitmap));
    }

    private void uploadFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        apiService.uploadToHostingerGallery(body).enqueue(new Callback<UploadResponseModel>() {
            @Override
            public void onResponse(Call<UploadResponseModel> call, Response<UploadResponseModel> response) {
                Toast.makeText(MainActivity.this, response.isSuccessful() ? "Image uploaded successfully" : "Failed to upload image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UploadResponseModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Image upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collectAndAddPatientData() {
        collectPatientData();
        if (!isPatientDataValid()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        addPatientToDatabase();
    }

    private void collectPatientData() {
        patientName = getTextFromEditText(R.id.ptntsNameTxt);
        patientAddress = getTextFromEditText(R.id.ptntsAddressTxt);
        patientPhone = getTextFromEditText(R.id.ptntsPhoneTxt);
        patientDOB = dateBirth.getText().toString();
        checkupDate = dateCheckTxt.getText().toString();
        bloodPressure = getTextFromEditText(R.id.bloodPressureTxt);
        pulseRate = getTextFromEditText(R.id.pulseRateTxt);
        respRate = getTextFromEditText(R.id.respRateTxt);
        weight = getTextFromEditText(R.id.weightTxt);
        temperature = getTextFromEditText(R.id.temperatureTxt);
        cc = getTextFromEditText(R.id.ccTxt);
        pe = getTextFromEditText(R.id.peTxt);
        dx = getTextFromEditText(R.id.dxTxt);
        meds = getTextFromEditText(R.id.medsTxt);
        labs = getTextFromEditText(R.id.labsTxt);
    }

    private String getTextFromEditText(int id) {
        return ((EditText) findViewById(id)).getText().toString().trim();
    }

    private void addPatientToDatabase() {
        apiService.addPatient(patientName, patientAddress, patientPhone, patientGender, patientStatus,patientDOB,
                        bloodPressure, pulseRate, respRate, weight, temperature, cc, pe, dx, meds, labs, checkupDate)
                .enqueue(new Callback<PatientResponseModel>() {
                    @Override
                    public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                        Toast.makeText(MainActivity.this, response.isSuccessful() ? "Patient added successfully" : "Failed to add patient", Toast.LENGTH_SHORT).show();
                        if (response.isSuccessful()) showImageChoiceDialog();
                    }

                    @Override
                    public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Failed to add patient: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            Log.e(TAG, "Error converting bitmap to file", e);
            return null;
        }
    }

    private void showDatePickerDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            textView.setText(formattedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void openActivity(Class<?> cls) {
        startActivity(new Intent(MainActivity.this, cls));
    }

    private interface OnItemSelectedListener {
        void onItemSelected(String selected);
    }
}
