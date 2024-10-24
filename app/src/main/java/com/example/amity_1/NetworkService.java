package com.example.amity_1;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NetworkService {

    @FormUrlEncoded
    @POST("resend_otp.php")  // Adjust based on your actual endpoint
    Call<ResponseBody> resendOtp(
            @Field("email_or_phone") String emailOrPhone
    );
    @FormUrlEncoded
    @POST("reset_password.php") // Replace with your actual endpoint
    Call<ResponseBody> resetPassword(
            @Field("email") String email,
            @Field("new_password") String newPassword
    );
    @FormUrlEncoded
    @POST("sendOtp.php")
    Call<ResponseBody> sendOtp(
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("verifyOtp.php")
    Call<ResponseBody> verifyOtp(
            @Field("email") String email,
            @Field("otp") String otp
    );
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponseModel> login(
            @Field("name") String name,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("add_patient.php") // Make sure this matches your PHP file
    Call<PatientResponseModel> addPatient(
            @Field("name") String name,
            @Field("address") String address,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("status") String status,
            @Field("birthday") String birthday,
            @Field("checkup_date") String checkup_date,
            @Field("blood_pressure") String blood_pressure,
            @Field("pulse_rate") String pulse_rate,
            @Field("resp_rate") String resp_rate,
            @Field("weight") String weight,
            @Field("temperature") String temperature,
            @Field("cc") String cc,
            @Field("pe") String pe,
            @Field("dx") String dx,
            @Field("meds") String meds,
            @Field("labs") String labs
    );
    @Multipart
    @POST("upload_document.php")
    Call<UploadResponseModel> uploadDocument(
            @Part("patient_name") RequestBody patientName,
            @Part MultipartBody.Part document
    );
    @Multipart
    @POST("upload.php")
    Call<UploadResponseModel> uploadToHostingerGallery(@Part MultipartBody.Part file);

    @GET("get_patients.php")
    Call<PatientResponseModel> getPatients(); // Change the response model

    @GET("fetch_monthly_data.php")
    Call<GraphDataResponseModel> getMonthlyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getWeeklyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getDailyGraphData();
}
