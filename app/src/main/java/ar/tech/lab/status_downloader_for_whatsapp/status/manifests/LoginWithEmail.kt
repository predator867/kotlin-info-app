package ar.tech.lab.status_downloader_for_whatsapp.status.manifests

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.dashboard.DashBoard
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentLoginWithEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginWithEmail : Fragment() {


    private var _binding: FragmentLoginWithEmailBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginWithEmailBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            if (!isEmpty()) {
                auth(binding.edtEmail.text, binding.edtPassword.text)
            }
        }

        binding.signUp.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, SignUp(), "AdminSingUp")
            fragmentTransaction.commit()
        }

        binding.imgBack.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, Login(), "AdminLogin")
            fragmentTransaction.commit()
        }

        return view
    }

    private fun auth(email: Editable?, password: Editable?) {
        auth.signInWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    startActivity(Intent(context, DashBoard::class.java))

                } else {
                    Toast.makeText(context, "Fail. Try Again later!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isEmpty(): Boolean {
        var empty = true
        when {
            binding.edtEmail.text.toString().isEmpty() -> binding.edtEmail.error = "Enter Email"
            binding.edtPassword.text.toString().isEmpty() -> binding.edtPassword.error =
                "Enter Password"
            else -> empty = false
        }
        return empty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}