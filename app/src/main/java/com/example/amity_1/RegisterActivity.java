package com.example.amity_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword;
    private Button buttonRegister;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        inputName = findViewById(R.id.txtName);
        inputEmail = findViewById(R.id.txtEmail);
        inputPassword = findViewById(R.id.txtPwd);
        linkLogin = findViewById(R.id.lnkLogin);
        buttonRegister = findViewById(R.id.btnregister);
    }

    private void setupListeners() {
        linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        buttonRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", inputName.getText().toString());
                params.put("email", inputEmail.getText().toString());
                params.put("password", inputPassword.getText().toString());
                register(params);
            }
        });
    }

    private boolean validateInputs() {
        if (inputName.getText().toString().isEmpty()) {
            showToast("Enter first name");
            return false;
        } else if (inputEmail.getText().toString().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (inputPassword.getText().toString().isEmpty()) {
            showToast("Enter password");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void register(HashMap<String, String> params) {
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<RegistrationResponseModel> registerCall = networkService.register(params);

        registerCall.enqueue(new Callback<RegistrationResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<RegistrationResponseModel> call, @NonNull Response<RegistrationResponseModel> response) {
                handleRegisterResponse(response);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showToast("Registration failed. Please try again.");
            }
        });
    }

    private void handleRegisterResponse(Response<RegistrationResponseModel> response) {
        RegistrationResponseModel responseBody = response.body();
        if (responseBody != null) {
            showToast(responseBody.getMessage());
            if ("1".equals(responseBody.getSuccess())) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        }
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
