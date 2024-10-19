package com.example.amity_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private EditText otpEmailInput;
    private Button btnConfirmOtp;
    private Button btnResendOtp;
    private Button btnPhoneOtp;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Set up window insets for padding (adjusting padding for the back button based on system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        otpEmailInput = findViewById(R.id.otpEmailInput);    // Email input field
        btnConfirmOtp = findViewById(R.id.btnConfirmOtp);    // Confirm OTP button
        btnResendOtp = findViewById(R.id.otpEmailBtn);       // Resend OTP (Email) button
        btnPhoneOtp = findViewById(R.id.otpPhoneBtn);        // Phone OTP button (currently disabled)
        backButton = findViewById(R.id.backButton);          // Back button

        // Set up listeners for the buttons
        setupListeners();
    }

    private void setupListeners() {
        // Confirm OTP button listener
        btnConfirmOtp.setOnClickListener(v -> {
            String otp = otpEmailInput.getText().toString();
            if (!otp.isEmpty()) {
                submitOtp(otp);
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend OTP button listener (Email)
        btnResendOtp.setOnClickListener(v -> {
            String email = otpEmailInput.getText().toString();
            if (email != null && !email.isEmpty()) {
                resendOtp(email);
            } else {
                Toast.makeText(this, "Email not found. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button listener
        backButton.setOnClickListener(v -> finish()); // Finish activity and go back
    }

    private void submitOtp(String otp) {
        String email = otpEmailInput.getText().toString(); // Get email from input

        if (email != null && !email.isEmpty()) {
            NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
            Call<ResponseBody> otpCall = networkService.verifyOtp(email, otp); // Pass email and OTP

            otpCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Handle successful OTP verification
                        Toast.makeText(OtpActivity.this, "OTP Verified! Please reset your password.", Toast.LENGTH_SHORT).show();
                        // You can add intent here to move to the reset password activity
                    } else {
                        Toast.makeText(OtpActivity.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(OtpActivity.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Email not found. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resendOtp(String email) {
        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> resendCall = networkService.sendOtp(email); // Adjust based on your API

        resendCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OtpActivity.this, "OTP Resent successfully. Please check your email.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpActivity.this, "Failed to resend OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Failed to resend OTP. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
