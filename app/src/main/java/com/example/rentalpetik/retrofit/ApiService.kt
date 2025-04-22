package com.example.rentalpetik.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("user")
    fun getUser() : Call<ResponseUser>

    @FormUrlEncoded
    @POST("users/register")
    fun registUser(
        @Field("username") username : String,
        @Field("nama") nama : String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("role") role : String
    ) : Call<ResponseUser>
}