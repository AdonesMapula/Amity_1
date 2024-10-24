package com.example.amity_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private Button btnResetPassword;
    private TextView txtEmail;
    private TextView txtName; // TextView to display the name
    private ImageView btnToggleNewPwd;
    private ImageView btnToggleConfirmPwd;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtNewPassword = findViewById(R.id.newPasswordInput);
        txtConfirmPassword = findViewById(R.id.confirmPasswordInput);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtUsername); // Initialize the name TextView
        btnToggleNewPwd = findViewById(R.id.btnToggleNewPwd);
        btnToggleConfirmPwd = findViewById(R.id.btnToggleConfirmPwd);

        String email = getIntent().getStringExtra("email");
        txtEmail.setText("Email: " + email);

        fetchUserName(email);

        btnResetPassword.setOnClickListener(v -> {
            String newPassword = txtNewPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();
            if (newPassword.equals(confirmPassword) && !newPassword.isEmpty()) {
                resetPassword(email, newPassword);
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match or are empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnToggleNewPwd.setOnClickListener(v -> togglePasswordVisibility(txtNewPassword, btnToggleNewPwd, isNewPasswordVisible));

        btnToggleConfirmPwd.setOnClickListener(v -> togglePasswordVisibility(txtConfirmPassword, btnToggleConfirmPwd, isConfirmPasswordVisible));
    }

    private void fetchUserName(String email) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.getUserName(email); // Add this API call

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String name = response.body().string(); // Handle IOException
                        txtName.setText("Username: " + name);
                    } catch (IOException e) {
                        e.printStackTrace();
                        txtName.setText("Error reading name");
                    }
                } else {
                    txtName.setText("Name: Not found");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Failed to fetch user name: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.ic_visibility_off);
        } else {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.ic_visibility_on);
        }
        editText.setSelection(editText.length());
        isVisible = !isVisible;
    }

    private void resetPassword(String email, String newPassword) {
        NetworkService apiService = NetworkClient.getClient().create(NetworkService.class);
        Call<ResponseBody> call = apiService.resetPassword(email, newPassword);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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
