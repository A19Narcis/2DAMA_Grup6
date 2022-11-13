package com.example.projecte_2dam_grup6;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/uploadUserImage")
    Call<ResponseBody> postImage (@Part MultipartBody.Part image, @Part("myFile")RequestBody name);

    @Multipart
    @POST("/uploadProductImage")
    Call<ResponseBody> postImageProd (@Part MultipartBody.Part image, @Part("myFile")RequestBody name);

    @GET("/dadesProductsJSON")
    Call<Producte> getProductList();

}
