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
    @POST("send_otp.php")
    Call<ResponseBody> sendOtp(
            @Field("email") String email
    );
    @POST("send_otp_phone.php")
    Call<ResponseBody> sendOtpToPhone(@Body String phone);

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<ResponseBody> verifyOtp(
            @Field("email") String email,
            @Field("otp") String otp
    );

    // Login user
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponseModel> login(
            @Field("name") String name,
            @Field("password") String password
    );

    // Add patient to the database
    @POST("add_patient.php")
    @FormUrlEncoded
    Call<PatientResponseModel> addPatient(
            @Field("name") String name,
            @Field("address") String address,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("status") String status,
            @Field("birthday") String birthday,
            @Field("checkup_date") String checkupDate,
            @Field("blood_pressure") String bloodPressure,
            @Field("pulse_rate") String pulseRate,
            @Field("resp_rate") String respRate,
            @Field("weight") String weight,
            @Field("temperature") String temperature,
            @Field("cc") String cc,
            @Field("pe") String pe,
            @Field("dx") String dx,
            @Field("meds") String meds,
            @Field("labs") String labs
    );


    // Upload scanned document to the server
    @Multipart
    @POST("upload_document.php")
    Call<UploadResponseModel> uploadDocument(
            @Part("patient_name") RequestBody patientName,
            @Part MultipartBody.Part document
    );
    @Multipart
    @POST("upload.php") // Adjust this to your actual upload endpoint on Hostinger
    Call<UploadResponseModel> uploadToHostingerGallery(@Part MultipartBody.Part file);


    // Retrieve all patients data from Hostinger
    @GET("get_patients.php")
    Call<PatientResponseModel> getPatients(); // Change the response model

    // Retrieve monthly patients data
    @GET("fetch_monthly_data.php")
    Call<GraphDataResponseModel> getMonthlyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getWeeklyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getDailyGraphData();
}
