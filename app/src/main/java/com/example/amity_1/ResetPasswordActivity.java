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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private Button btnResetPassword;
    private TextView txtEmail;
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
        btnToggleNewPwd = findViewById(R.id.btnToggleNewPwd);
        btnToggleConfirmPwd = findViewById(R.id.btnToggleConfirmPwd);

        String email = getIntent().getStringExtra("email");
        txtEmail.setText("Email: " + email);

        btnResetPassword.setOnClickListener(v -> {
            String newPassword = txtNewPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();
            if (newPassword.equals(confirmPassword) && !newPassword.isEmpty()) {
                resetPassword(email, newPassword);
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match or are empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnToggleNewPwd.setOnClickListener(v -> {
            if (isNewPasswordVisible) {
                txtNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnToggleNewPwd.setImageResource(R.drawable.ic_visibility_off);
            } else {
                txtNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnToggleNewPwd.setImageResource(R.drawable.ic_visibility_on);
            }
            txtNewPassword.setSelection(txtNewPassword.length());
            isNewPasswordVisible = !isNewPasswordVisible;
        });

        btnToggleConfirmPwd.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                txtConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnToggleConfirmPwd.setImageResource(R.drawable.ic_visibility_off);
            } else {
                txtConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnToggleConfirmPwd.setImageResource(R.drawable.ic_visibility_on);
            }
            txtConfirmPassword.setSelection(txtConfirmPassword.length());
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });
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
