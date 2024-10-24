package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button btnConfirmOtp;
    private Button btnBackOtp; // Declare the resend button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification); // Ensure the correct layout file is set

        // Find views by ID
        otpInput = findViewById(R.id.otpInputField);
        btnConfirmOtp = findViewById(R.id.btnVerifyOtp);
        btnBackOtp = findViewById(R.id.btnBackOtp); // Initialize the resend button
        TextView emailTextView = findViewById(R.id.emailTextView); // Initialize the email TextView

        // Get the email passed from the previous activity
        String email = getIntent().getStringExtra("email");
        emailTextView.setText(email); // Set the email to the TextView

        // Set click listener for Confirm OTP button
        btnConfirmOtp.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (!otp.isEmpty()) {
                verifyOtp(email, otp);
            } else {
                Toast.makeText(OtpVerificationActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Resend OTP button
        btnBackOtp.setOnClickListener(v -> {
            Intent intent = new Intent(OtpVerificationActivity.this, OtpActivity.class);
            startActivity(intent);
            finish();
        });
    }


    // Method to verify the OTP
    private void verifyOtp(String email, String otp) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.verifyOtp(email, otp); // Your verification API call

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(OtpVerificationActivity.this, "OTP verified successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to ResetPasswordActivity
                            Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                            startActivity(intent);
                            finish(); // Optionally finish the current activity
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}