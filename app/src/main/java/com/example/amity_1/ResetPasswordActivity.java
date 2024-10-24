package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private Button btnResetPassword;
    private TextView txtEmail; // To display the email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // Ensure you have the correct layout

        // Find views by ID
        txtNewPassword = findViewById(R.id.newPasswordInput);
        txtConfirmPassword = findViewById(R.id.confirmPasswordInput);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        txtEmail = findViewById(R.id.txtEmail); // Initialize the email TextView

        // Get the email passed from OtpVerificationActivity
        String email = getIntent().getStringExtra("email");
        txtEmail.setText("Email: " + email); // Display the email

        // Set click listener for the Reset Password button
        btnResetPassword.setOnClickListener(v -> {
            String newPassword = txtNewPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();
            if (newPassword.equals(confirmPassword) && !newPassword.isEmpty()) {
                resetPassword(email, newPassword); // Call the resetPassword method
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match or are empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to reset the password
    private void resetPassword(String email, String newPassword) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.resetPassword(email, newPassword); // Your reset password API call

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
