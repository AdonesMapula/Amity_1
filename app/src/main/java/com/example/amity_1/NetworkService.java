package com.example.amity_1;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface NetworkService {

    // Register user
    @FormUrlEncoded
    @POST("register.php")
    Call<RegistrationResponseModel> register(@FieldMap HashMap<String, String> params);

    // Login user
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponseModel> login(@Field("email") String email, @Field("password") String password);

    // Add patient to the database
    @FormUrlEncoded
    @POST("add_patient.php")
    Call<PatientResponseModel> addPatient(
            @Field("patient_name") String patientName,
            @Field("checkup_date") String checkupDate
    );

    // Upload scanned document to the server
    @Multipart
    @POST("upload_document.php")
    Call<UploadResponseModel> uploadDocument(
            @Part("patient_name") RequestBody patientName, // Patient name in the database
            @Part MultipartBody.Part document // The scanned document (file)
    );

    // Retrieve graph data (e.g., daily, weekly, monthly patient records)
    @POST("get_graph_data.php")
    Call<GraphDataResponseModel> getGraphData();
}
