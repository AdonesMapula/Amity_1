package com.example.amity_1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputName, inputPassword;
    private Button buttonLogin, buttonForgotPassword;
    private SharedPreferences preferences;
    private ImageView eyeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        if (preferences.getBoolean(Constants.KEY_ISE_LOGGED_IN, false)) {
            navigateToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        buttonForgotPassword = findViewById(R.id.lnkReset); // The "Reset" button
        inputName = findViewById(R.id.txtloginName);
        inputPassword = findViewById(R.id.txtloginPwd);
        buttonLogin = findViewById(R.id.btnlogin);
        eyeIcon = findViewById(R.id.btnTogglePwd);
    }

    private void setupListeners() {
        buttonForgotPassword.setOnClickListener(v -> {
            // Directly proceed to the OTP Activity regardless of email input
            String email = inputName.getText().toString();
            Intent intent = new Intent(getApplicationContext(), OtpActivity.class); // Redirect to OTP input activity
            intent.putExtra("email", email); // Send the email input, even if it's empty
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(v -> {
            if (isInputValid()) {
                login();
            }
        });

        eyeIcon.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (inputPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
            // Show password
            inputPassword.setTransformationMethod(null);
            eyeIcon.setImageResource(R.drawable.ic_visibility_on); // Update to an open eye icon
        } else {
            // Hide password
            inputPassword.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
            eyeIcon.setImageResource(R.drawable.ic_visibility_off); // Update to a closed eye icon
        }
        inputPassword.setSelection(inputPassword.getText().length()); // Keep the cursor at the end
    }

    private boolean isInputValid() {
        if (inputName.getText().toString().isEmpty()) {
            showToast("Please enter your username.");
            return false;
        } else if (inputPassword.getText().toString().isEmpty()) {
            showToast("Please enter your password.");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void login() {
        ProgressDialog progressDialog = showLoadingDialog();

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<LoginResponseModel> loginCall = networkService.login(
                inputName.getText().toString().trim(), // username
                inputPassword.getText().toString().trim()  // password
        );

        loginCall.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull Response<LoginResponseModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    handleLoginResponse(response);
                } else {
                    Log.e("LoginActivity", "Response error: " + response.errorBody());
                    showToast("Login failed. Please check your credentials.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e("LoginActivity", "Network failure: " + t.getMessage());
                showToast("Login failed. Please try again.");
            }
        });
    }

    private ProgressDialog showLoadingDialog() {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    private void handleLoginResponse(Response<LoginResponseModel> response) {
        LoginResponseModel responseBody = response.body();
        if (responseBody != null) {
            if ("1".equals(responseBody.getSuccess())) {
                // Debugging logs to inspect response content
                Log.d("LoginActivity", "User Detail Object: " + responseBody.getUserDetailObject());

                if (responseBody.getUserDetailObject() != null) {
                    // Adjusted to the new structure of getUserDetails()
                    if (responseBody.getUserDetailObject().getName() != null &&
                            responseBody.getUserDetailObject().getPassword() != null) {

                        saveUserDetails(responseBody);
                        showToast(responseBody.getMessage());
                        navigateToMainActivity(); // Navigate to MainActivity upon successful login
                    } else {
                        showToast("User details not found.");
                        Log.e("LoginActivity", "User details not found in response: " + responseBody.getUserDetailObject());
                    }
                } else {
                    showToast("User details not found.");
                    Log.e("LoginActivity", "User detail object is null.");
                }
            } else {
                showToast(responseBody.getMessage());
                Log.e("LoginActivity", "Login failed: " + responseBody.getMessage());
            }
        } else {
            showToast("Response body is null.");
            Log.e("LoginActivity", "Response body is null.");
        }
    }

    private void saveUserDetails(LoginResponseModel responseBody) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.KEY_ISE_LOGGED_IN, true);

        // Adjusted to match the structure of the response body
        if (responseBody.getUserDetailObject() != null) {
            editor.putString(Constants.KEY_USERNAME,
                    responseBody.getUserDetailObject().getName()); // Save the user's name
            editor.putString(Constants.KEY_EMAIL,
                    responseBody.getUserDetailObject().getPassword()); // Save the user's password
        } else {
            Log.e("LoginActivity", "User details are missing in the response.");
        }
        editor.apply();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Close LoginActivity
    }

    // Email validation method
    public boolean emailValidator(String email) {
        String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    // Adjusts the layout when the keyboard opens
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSoftKeyboard(this);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
