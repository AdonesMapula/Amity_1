package com.example.amity_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button buttonLogin, textCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        textCreateAccount = findViewById(R.id.lnkRegister);
        inputEmail = findViewById(R.id.txtloginEmail);
        inputPassword = findViewById(R.id.txtloginPwd);
        buttonLogin = findViewById(R.id.btnlogin);
    }

    private void setupListeners() {
        textCreateAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        buttonLogin.setOnClickListener(v -> {
            if (inputEmail.getText().toString().isEmpty()) {
                showToast("Please Enter Email");
            } else if (inputPassword.getText().toString().isEmpty()) {
                showToast("Please Enter Password");
            } else {
                login();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void login() {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        NetworkService networkService = NetworkClient.getClient().create(NetworkService.class);
        Call<LoginResponseModel> loginCall = networkService.login(inputEmail.getText().toString(), inputPassword.getText().toString());

        loginCall.enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseModel> call, @NonNull Response<LoginResponseModel> response) {
                handleLoginResponse(response);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseModel> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showToast("Login failed. Please try again.");
            }
        });
    }

    private void handleLoginResponse(Response<LoginResponseModel> response) {
        LoginResponseModel responseBody = response.body();
        if (responseBody != null) {
            if ("1".equals(responseBody.getSuccess())) {
                saveUserDetails(responseBody);
                showToast(responseBody.getMessage());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                showToast(responseBody.getMessage());
            }
        }
    }

    private void saveUserDetails(LoginResponseModel responseBody) {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.KEY_ISE_LOGGED_IN, true);
        editor.putString(Constants.KEY_USERNAME,
                responseBody.getUserDetailObject().getUserDetails().get(0).getFirstName() + " " +
                        responseBody.getUserDetailObject().getUserDetails().get(0).getLastName());
        editor.putString(Constants.KEY_EMAIL,
                responseBody.getUserDetailObject().getUserDetails().get(0).getEmail());
        editor.apply();
    }

    // Email validation
    public boolean emailValidator(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
