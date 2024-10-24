package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private EditText otpInput;
    private Button btnConfirmOtp;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Find views by ID
        otpInput = findViewById(R.id.otpInput);
        btnConfirmOtp = findViewById(R.id.btnConfirmOtp);
        backButton = findViewById(R.id.backButton);

        // Set click listener for Send OTP button
        btnConfirmOtp.setOnClickListener(v -> {
            String email = otpInput.getText().toString().trim();
            if (!email.isEmpty() && isValidEmail(email)) {
                sendOtpToEmail(email);  // Send OTP to email
            } else {
                Toast.makeText(OtpActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for Back button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Helper method to validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to send OTP to email
    private void sendOtpToEmail(String email) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.sendOtp(email);  // Call the PHP email OTP endpoint

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(OtpActivity.this, "OTP sent to your email, please check", Toast.LENGTH_SHORT).show();

                            // Redirect to OtpVerificationActivity
                            Intent intent = new Intent(OtpActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("email", email);  // Pass email to the next activity
                            startActivity(intent);
                        } else {
                            Toast.makeText(OtpActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(OtpActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
