package com.example.rentalpetik.ui.LoginRegist

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rentalpetik.MainActivity
import com.example.rentalpetik.R
import com.example.rentalpetik.databinding.FragmentRegisterBinding
import com.example.rentalpetik.retrofit.ApiConfig
import com.example.rentalpetik.retrofit.ResponseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHaveAccount.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containere, LoginFragment())
                .commit()
        }

        binding.btnSubmit.setOnClickListener {
            val username = binding.loginEdtUsername.text.toString()
            val name = binding.loginEdtName.text.toString()
            val email = binding.loginEdtEmail.text.toString()
            val password = binding.loginEdtPassword.text.toString()

            if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Request API Register
            val client = ApiConfig.getApiService().registUser(
                username = username,
                nama = name,
                email = email,
                password = password,
                role = "user"
            )

            client.enqueue(object : Callback<ResponseUser> {
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if (response.isSuccessful && response.body() != null) {
                        // Simpan status login
                        val sharedPreferences: SharedPreferences = requireActivity()
                            .getSharedPreferences("MYPREF", MODE_PRIVATE)

                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("USERNAME", username)
                        editor.putString("EMAIL", email)
                        editor.apply()

                        Toast.makeText(requireContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show()

                        // Pindah ke MainActivity
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Registrasi gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
