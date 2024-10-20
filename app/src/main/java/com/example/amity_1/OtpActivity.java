package com.example.amity_1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private EditText otpPhoneInput;
    private Button btnConfirmOtp;
    private ImageButton backButton;
    private Button emailButton;  // Email OTP button
    private Button phoneButton;  // Phone OTP button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_phone);

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views by ID
        otpPhoneInput = findViewById(R.id.otpInput);
        btnConfirmOtp = findViewById(R.id.btnConfirmOtp);
        backButton = findViewById(R.id.backButton);
        emailButton = findViewById(R.id.otpEmailBtn);
        phoneButton = findViewById(R.id.otpPhoneBtn);

        // Set default button states
        setButtonInactive(emailButton);  // Email button is inactive by default
        setButtonActive(phoneButton);    // Phone button is active by default

        // Set click listener for Send OTP button
        btnConfirmOtp.setOnClickListener(v -> {
            String input = otpPhoneInput.getText().toString().trim();
            if (!input.isEmpty()) {
                if (phoneButton.isEnabled()) {
                    sendOtpToPhone(input);  // Send OTP to phone
                } else {
                    sendOtpToEmail(input);  // Send OTP to email
                }
            } else {
                Toast.makeText(OtpActivity.this, "Please enter a valid email or phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listener for Email OTP button to switch to email
        emailButton.setOnClickListener(v -> {
            otpPhoneInput.setHint("Enter Email");
            otpPhoneInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);  // Set input for email

            // Set button states
            setButtonActive(emailButton);
            setButtonInactive(phoneButton);

            // Enable phone button, disable email button
            phoneButton.setEnabled(true);
            emailButton.setEnabled(false);
        });

        // Set click listener for Phone OTP button to switch to phone
        phoneButton.setOnClickListener(v -> {
            otpPhoneInput.setHint("Enter Phone Number");
            otpPhoneInput.setInputType(InputType.TYPE_CLASS_PHONE);  // Set input for phone

            // Set button states
            setButtonActive(phoneButton);
            setButtonInactive(emailButton);

            // Enable email button, disable phone button
            emailButton.setEnabled(true);
            phoneButton.setEnabled(false);
        });
    }

    // Method to set a button as active (blue background and white text)
    private void setButtonActive(Button button) {
        button.setBackgroundColor(Color.parseColor("#0064F6"));  // Set background color to blue
        button.setTextColor(Color.WHITE);  // Set text color to white
    }

    // Method to set a button as inactive (transparent background and gray text)
    private void setButtonInactive(Button button) {
        button.setBackgroundColor(Color.TRANSPARENT);  // Set background color to transparent
        button.setTextColor(Color.GRAY);  // Set text color to gray
    }

    // Method to send OTP to email
    private void sendOtpToEmail(String email) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.sendOtp(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(OtpActivity.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                            // Redirect to OtpVerificationActivity
                            Intent intent = new Intent(OtpActivity.this, otpVerificationActivity.class);
                            intent.putExtra("email", email); // Pass email to the next activity
                            startActivity(intent);
                        } else {
                            Toast.makeText(OtpActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to send OTP to phone
    private void sendOtpToPhone(String phoneNumber) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.sendOtpToPhone(phoneNumber);  // Assuming you have a method for phone OTP

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(OtpActivity.this, "OTP sent to your phone", Toast.LENGTH_SHORT).show();

                            // Redirect to OtpVerificationActivity
                            Intent intent = new Intent(OtpActivity.this, otpVerificationActivity.class);
                            intent.putExtra("phone", phoneNumber); // Pass phone number to the next activity
                            startActivity(intent);
                        } else {
                            Toast.makeText(OtpActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
