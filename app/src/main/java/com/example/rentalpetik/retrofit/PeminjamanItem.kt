package com.example.rentalpetik.retrofit

import com.google.gson.annotations.SerializedName

data class PeminjamanItem (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("nama")
    val nama: String? = null,

    @SerializedName("alamat")
    val alamat: String? = null,

    @SerializedName("no_telepon")
    val noTelepon: String? = null,

    @SerializedName("instansi")
    val instansi: String? = null,

    @SerializedName("kendaraan")
    val kendaraan: String? = null,

    @SerializedName("kondisi_kendaraan")
    val kondisiKendaraan: String? = null,

    @SerializedName("bensin_awal")
    val bensinAwal: Int? = null,

    @SerializedName("sisa_etol")
    val sisaEtol: Int? = null,

    @SerializedName("tanggal_peminjaman")
    val tanggalPeminjaman: String? = null,

    @SerializedName("foto_ktp")
    val fotoKtp: String? = null,

    @SerializedName("foto_wajah")
    val fotoWajah: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)
// Jika response adalah array langsung
typealias PeminjamanResponse = List<PeminjamanItem>