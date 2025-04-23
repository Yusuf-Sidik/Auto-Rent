package com.example.rentalpetik.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("users/register")
    fun registUser(
        @Field("username") username : String,
        @Field("nama") nama : String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("role") role : String
    ) : Call<ResponseUser>

    @Multipart
    @POST("peminjaman/create")
    fun createPeminjaman(
//        @Part("user_id") userId: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("no_telepon") noTelepon: RequestBody,
        @Part("instansi") instansi: RequestBody,
        @Part("kendaraan") kendaraan: RequestBody,
        @Part("kondisi_kendaraan") kondisiKendaraan: RequestBody,
        @Part("bensin_awal") bensinAwal: RequestBody,
        @Part("sisa_etol") sisaEtol: RequestBody,
        @Part("tanggal_peminjaman") tanggalPeminjaman: RequestBody,
        @Part fotoKtp: MultipartBody.Part?,
        @Part fotoWajah: MultipartBody.Part?
    ): Call<List<PeminjamanItem>>
}