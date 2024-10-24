package com.example.amity_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NetworkService apiService;
    private String name, address, phone, checkup_date, birthday, gender, status;
    private String blood_pressure, pulse_rate, resp_rate, weight, temperature, cc, pe, dx, meds, labs;
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
        genderSpinner = findViewById(R.id.ptntsGender);
        statusSpinner = findViewById(R.id.ptntsStatus);

        setupSpinners();
        setupButtons();
        setupDatePickers();
    }

    private void setupSpinners() {
        setupSpinner(genderSpinner, R.array.gender_options, selected -> gender = selected);
        setupSpinner(statusSpinner, R.array.status_options, selected -> status = selected);
    }

    private void setupButtons() {
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

    private void collectAndAddPatientData() {
        collectPatientData();
        if (!isPatientDataValid()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        addPatientToDatabase();
    }

    private void collectPatientData() {
        name = getTextFromEditText(R.id.ptntsNameTxt);
        address = getTextFromEditText(R.id.ptntsAddressTxt);
        phone = getTextFromEditText(R.id.ptntsPhoneTxt);
        birthday = dateBirth.getText().toString();
        checkup_date = dateCheckTxt.getText().toString();
        blood_pressure = getTextFromEditText(R.id.bloodPressureTxt);
        pulse_rate = getTextFromEditText(R.id.pulseRateTxt);
        resp_rate = getTextFromEditText(R.id.respRateTxt);
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
        apiService.addPatient(name, address, phone, gender, status, birthday, checkup_date,
                        blood_pressure, pulse_rate, resp_rate, weight, temperature, cc, pe, dx, meds, labs)
                .enqueue(new Callback<PatientResponseModel>() {
                    @Override
                    public void onResponse(Call<PatientResponseModel> call, Response<PatientResponseModel> response) {
                        Toast.makeText(MainActivity.this, response.isSuccessful() ? "Patient added successfully" : "Failed to add patient", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<PatientResponseModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Failed to add patient: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isPatientDataValid() {
        return !name.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !birthday.isEmpty() && !checkup_date.isEmpty();
    }

    private void showDatePickerDialog(TextView textView) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            textView.setText(String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        });
    }

    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    interface OnItemSelectedListener {
        void onItemSelected(String selected);
    }
}
