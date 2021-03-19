package com.example.erailadmin.RestApi;

import com.example.erailadmin.Model.CommonModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("addCollage.php")
    Call<CommonModel> addCollage(@Field("name") String name, @Field("contact") String contact, @Field("password") String password, @Field("address") String address);

    @Multipart
    @POST("addShop.php")
    Call<CommonModel> addFoodShop(@Part("name") RequestBody name, @Part("contact") RequestBody contact,
                                  @Part("password") RequestBody password, @Part("address") RequestBody address,
                                  @Part("station") RequestBody station, @Part("photo\";filename=\"photo.jpg\"") RequestBody photo);

    @Multipart
    @POST("addCoolie.php")
    Call<CommonModel> addCoolie(@Part("name") RequestBody name, @Part("contact") RequestBody contact,
                                @Part("password") RequestBody password, @Part("station") RequestBody station,
                                @Part("photo\";filename=\"photo.jpg\"") RequestBody photo);

}