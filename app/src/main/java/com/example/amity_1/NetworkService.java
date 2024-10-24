package com.example.amity_1;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NetworkService {
    @GET("patients.php")
    Call<PatientResponseModel> getPatients();

    @GET("patients.php")
    Call<PatientResponseModel> searchPatients(@Query("query") String query);

    @FormUrlEncoded
    @POST("resend_otp.php")
    Call<ResponseBody> resendOtp(
            @Field("email_or_phone") String emailOrPhone
    );
    @FormUrlEncoded
    @POST("reset_password.php")
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
    @POST("add_patient.php")
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

    @GET("fetch_monthly_data.php")
    Call<GraphDataResponseModel> getMonthlyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getWeeklyGraphData();

    @GET("fetch_daily_data.php")
    Call<GraphDataResponseModel> getDailyGraphData();
}
