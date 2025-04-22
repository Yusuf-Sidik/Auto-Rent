package com.example.rentalpetik.ui.LoginRegist

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
import com.example.rentalpetik.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNotHaveAccount.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.containere, RegisterFragment()).commit()
        }


        binding.btnSubmit.setOnClickListener {
            val preferences : SharedPreferences = requireContext().getSharedPreferences("MYPREF",android.content.Context.MODE_PRIVATE)
            val username = binding.loginEdtUsername.text.toString()
            val pass = binding.loginEdtPassword.text.toString()
            val userDetail : String? = preferences.getString(username + pass + "data", null)
            val editor : SharedPreferences.Editor = preferences.edit()
            if (userDetail != null) {
                editor.putBoolean("isLoggedIn", true)
                editor.apply()
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            } else {
                if (username.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    Toast.makeText(requireContext(), "Username atau Password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}