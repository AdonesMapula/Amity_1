package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class otpPhone extends AppCompatActivity {

    private EditText otpPhoneInput;
    private Button btnConfirmOtp;
    private ImageButton backButton;
    private Button emailButton;  // Email OTP button

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
        emailButton = findViewById(R.id.otpEmailBtn);  // Email OTP button

        // Set click listener for Send button
        btnConfirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = otpPhoneInput.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendOtpToEmail(email);  // Call method to send OTP
                } else {
                    Toast.makeText(otpPhone.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity and finish current activity
                Intent intent = new Intent(otpPhone.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listener for Email OTP button to redirect to OtpActivity
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start OtpActivity
                Intent intent = new Intent(otpPhone.this, OtpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendOtpToEmail(String email) {
        // Create Retrofit instance and call API
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.sendOtp(email);

        // Handle API response
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(otpPhone.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(otpPhone.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(otpPhone.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
