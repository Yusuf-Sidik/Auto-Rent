package com.example.rentalpetik.ui.pinjam

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rentalpetik.R
import com.example.rentalpetik.databinding.ActivityPinjamBinding
import com.example.rentalpetik.retrofit.ApiConfig
import com.example.rentalpetik.retrofit.PeminjamanItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PinjamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinjamBinding
    private var selectedKendaraan: String? = null
    private val calendar = Calendar.getInstance()
    private var ktpFile: File? = null
    private var wajahFile: File? = null
    private val STORAGE_PERMISSION_CODE = 100

    private val pickKtpLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File(cacheDir, "ktp_${System.currentTimeMillis()}.png")
            contentResolver.openInputStream(it)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            ktpFile = file
            binding.btnUploadKtp.text = "KTP: ${file.name}"
        }
    }

    private val pickWajahLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File(cacheDir, "wajah_${System.currentTimeMillis()}.png")
            contentResolver.openInputStream(it)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            wajahFile = file
            binding.btnUploadWajah.text = "Wajah: ${file.name}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinjamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Atur adapter untuk Spinner
        ArrayAdapter.createFromResource(
            this, R.array.kendaraan_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerKendaraan.adapter = adapter
        }

        //Tangani pilihan Spinner
        binding.spinnerKendaraan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedKendaraan = parent.getItemAtPosition(position).toString()
                //Non aktifkan tombol jika placeholder dipilih
                binding.btnPinjam.isEnabled = selectedKendaraan != "Pilih Kendaraan"
                println("Kendaraan yang dipilih: $selectedKendaraan") //untuk debugging
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnPinjam.isEnabled = false
            }
        }

        // Tangani klik EditText Tanggal Peminjaman
        binding.edtTanggalPeminjaman.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format tanggal ke ISO 8601
                calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val formattedDate = sdf.format(calendar.time)
                binding.edtTanggalPeminjaman.setText(formattedDate)
            }, year, month, day).show()
        }

        // Tangani klik tombol Upload KTP
        binding.btnUploadKtp.setOnClickListener {
            if (checkPermission()) {
                pickKtpLauncher.launch("image/*")
            } else {
                requestPermission()
            }
        }

        // Tangani klik tombol Upload Wajah
        binding.btnUploadWajah.setOnClickListener {
            if (checkPermission()) {
                pickWajahLauncher.launch("image/*")
            } else {
                requestPermission()
            }
        }

        // Tangani klik tombol Pinjam
        binding.btnPinjam.setOnClickListener {
            if (validateInputs()) {
                // Tambahkan log di sini sebelum memanggil createPeminjaman
                Log.d("Peminjaman", "Nama: ${binding.edtNama.text}, Alamat: ${binding.edtAlamat.text}, No Telepon: ${binding.edtNotelp.text}, Instansi: ${binding.edtInstansi.text}, Kondisi Kendaraan: ${binding.edtKondisiKendaraan.text}")
                Log.d("Peminjaman", "Bensin Awal: ${binding.edtBensinAwal.text}, Sisa Etol: ${binding.edtSisaEtol.text}, Tanggal Peminjaman: ${binding.edtTanggalPeminjaman.text}")
                Log.d("Peminjaman", "Kendaraan: $selectedKendaraan")
                Log.d("Peminjaman", "KTP: ${ktpFile?.name}, Wajah: ${wajahFile?.name}")
                createPeminjaman()
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Peminjaman"
    }

    private fun createPeminjaman() {
        binding.btnPinjam.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        val nama = binding.edtNama.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val alamat = binding.edtAlamat.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val noTelepon = binding.edtNotelp.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val instansi = binding.edtInstansi.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val kendaraan = selectedKendaraan!!.lowercase(Locale.ROOT).toRequestBody("text/plain".toMediaTypeOrNull())
        val kondisiKendaraan = binding.edtKondisiKendaraan.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val bensinAwal = binding.edtBensinAwal.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val sisaEtol = binding.edtSisaEtol.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tanggalPeminjaman = binding.edtTanggalPeminjaman.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val ktpPart = ktpFile?.let {
            val mimeType = getMimeType(it)
            MultipartBody.Part.createFormData("foto_ktp", it.name, it.asRequestBody(mimeType.toMediaTypeOrNull()))
        }
        val wajahPart = wajahFile?.let {
            val mimeType = getMimeType(it)
            MultipartBody.Part.createFormData("foto_wajah", it.name, it.asRequestBody(mimeType.toMediaTypeOrNull()))
        }

        val call = ApiConfig.getApiService().createPeminjaman(
            nama, alamat, noTelepon, instansi, kendaraan, kondisiKendaraan,
            bensinAwal, sisaEtol, tanggalPeminjaman, ktpPart, wajahPart
        )

        call.enqueue(object : Callback<List<PeminjamanItem>> {
            override fun onResponse(call: Call<List<PeminjamanItem>>, response: Response<List<PeminjamanItem>>) {
                binding.btnPinjam.isEnabled = true
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    Log.d("API Response", "Success: ${response.body()}")
                    response.body()?.firstOrNull()?.let {
                        Toast.makeText(this@PinjamActivity, "Peminjaman berhasil dibuat", Toast.LENGTH_SHORT).show()
                        finish()
                    } ?: run {
                        Toast.makeText(this@PinjamActivity, "Respons kosong dari server", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("API Response", "Failed: ${response.code()} ${response.message()}")
                    Log.d("API Response", "Error body: ${response.errorBody()?.string()}")
                    println("Code: ${response.code()}, Message: ${response.message()}, ErrorBody: ${response.errorBody()?.string()}")
                    Toast.makeText(this@PinjamActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PeminjamanItem>>, t: Throwable) {
                Log.e("API Error", "Request failed: ${t.localizedMessage}")
                binding.btnPinjam.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@PinjamActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "image/*"
        }
    }


    private fun validateInputs(): Boolean {
        if (selectedKendaraan == "Pilih Kendaraan") {
            Toast.makeText(this, "Pilih Kendaraan Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtNama.text.isEmpty()) {
            binding.edtNama.error = "Nama tidak boleh kosong"
            return false
        }
        if (binding.edtAlamat.text.isEmpty()) {
            binding.edtAlamat.error = "Alamat tidak boleh kosong"
            return false
        }
        if (binding.edtNotelp.text.isEmpty()) {
            binding.edtNotelp.error = "Nomor Telepon tidak boleh kosong"
            return false
        }
        if (binding.edtInstansi.text.isEmpty()) {
            binding.edtInstansi.error = "Instansi tidak boleh kosong"
            return false
        }
        if (binding.edtKondisiKendaraan.text.isEmpty()) {
            binding.edtKondisiKendaraan.error = "Kondisi Kendaraan tidak boleh kosong"
            return false
        }
        if (binding.edtBensinAwal.text.isEmpty()) {
            binding.edtBensinAwal.error = "Bensin Awal tidak boleh kosong"
            return false
        }
        if (binding.edtSisaEtol.text.isEmpty()) {
            binding.edtSisaEtol.error = "Sisa E-toll tidak boleh kosong"
            return false
        }
        if (binding.edtTanggalPeminjaman.text.isEmpty()) {
            binding.edtTanggalPeminjaman.error = "Tanggal Peminjaman tidak boleh kosong"
            return false
        }
        if (ktpFile == null) {
            Toast.makeText(this, "Upload KTP terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        if (wajahFile == null) {
            Toast.makeText(this, "Upload wajah terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    private fun checkPermission() : Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin diberikan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Izin diperlukan untuk upload file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> {super.onOptionsItemSelected(item)}
        }
    }
}