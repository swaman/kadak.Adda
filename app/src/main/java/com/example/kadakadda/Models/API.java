package com.example.kadakadda.Models;

import android.widget.Toolbar;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    @POST("initiateTransaction")
    Call<Token_Res> getAppToken(@Body TokenRequest req, @Query("mid") String mid, @Query("orderId") String orderId);
}
