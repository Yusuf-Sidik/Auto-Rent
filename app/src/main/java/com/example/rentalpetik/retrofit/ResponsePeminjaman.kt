package com.example.rentalpetik.retrofit

import com.google.gson.annotations.SerializedName

data class ResponsePeminjaman(

	@field:SerializedName("ResponsePeminjaman")
	val responsePeminjaman: List<ResponsePeminjamanItem?>? = null
)

data class ResponsePeminjamanItem(

	@field:SerializedName("kondisi_kendaraan")
	val kondisiKendaraan: String? = null,

	@field:SerializedName("kendaraan")
	val kendaraan: String? = null,

	@field:SerializedName("foto_ktp")
	val fotoKtp: String? = null,

	@field:SerializedName("foto_wajah")
	val fotoWajah: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("sisa_etol")
	val sisaEtol: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("bensin_awal")
	val bensinAwal: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("no_telepon")
	val noTelepon: String? = null,

	@field:SerializedName("instansi")
	val instansi: String? = null,

	@field:SerializedName("tanggal_peminjaman")
	val tanggalPeminjaman: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
