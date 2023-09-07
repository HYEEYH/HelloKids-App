package com.bpdev.hellokids.api;

import com.bpdev.hellokids.model.FoodMenu;
import com.bpdev.hellokids.model.FoodMenuList;
import com.bpdev.hellokids.model.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoodMenuApi {
    @Multipart
    @POST("/menu/add")
    Call<Result> foodMenuAdd(@Header("Authorization") String token,
                             @Part("mealDate") RequestBody mealDate,
                             @Part MultipartBody.Part mealPhotoUrl,
                             @Part("mealContent") RequestBody mealContent,
                             @Part("mealType") RequestBody mealType); // mealDate, mealPhotoUrl, mealContent, mealType

    @GET("/menu/list")
    Call<FoodMenuList>foodMenuListAll(@Header("Authorization") String token,
                                   @Query("offset") Integer offset,
                                   @Query("limit") Integer limit,
                                   @Query("count") Integer count);

    @GET("/menu/{mealDate}")
    Call<FoodMenuList>foodMenuListDay(@Path("nurseryId") int nurseryId, @Path("mealDate") String mealDate); // 하루 메뉴 목록 /menu/2023-09-01

    @GET("/menu/{id}")
    Call<FoodMenu>foodMenuView(@Path("id") int id);; // 개별 메뉴 정보 보기

    @PUT("/menu/{id}")
    Call<Result> foodMenuAdd(@Header("Authorization") String token,
                             @Path("id") int id,
                             @Part("mealDate") RequestBody mealDate,
                             @Part MultipartBody.Part mealPhotoUrl,
                             @Part("mealContent") RequestBody mealContent,
                             @Part("mealType") RequestBody mealType); // mealDate, mealPhotoUrl, mealContent, mealType


    @DELETE("/menu/{id}")
    Call<FoodMenu>foodMenuDelete(@Header("Authorization") String token, @Path("id") int id); // 개별 메뉴 삭제

}
