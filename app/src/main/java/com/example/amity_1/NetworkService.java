package com.example.amity_1;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Part;

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
    @POST("add_patient.php") // Adjust endpoint accordingly
    Call<PatientResponseModel> addPatient(
            @Field("name") String patientName,
            @Field("address") String patientAddress,
            @Field("phone") String patientPhone,
            @Field("checkup_date") String checkupDate // Ensure naming consistency
    );

    // Upload scanned document to the server
    @Multipart
    @POST("upload_document.php")
    Call<UploadResponseModel> uploadDocument(
            @Part("patient_name") RequestBody patientName,
            @Part MultipartBody.Part document
    );

    // Retrieve Patients Data from Hostinger
    @GET("get_patients.php")
    Call<ResponseBody> getPatients();

    // Retrieve graph data
    @POST("get_graph_data.php")
    Call<GraphDataResponseModel> getGraphData();
}
