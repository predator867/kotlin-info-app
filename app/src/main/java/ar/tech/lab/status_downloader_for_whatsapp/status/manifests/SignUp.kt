package ar.tech.lab.status_downloader_for_whatsapp.status.manifests

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            if (!isEmpty()) {
                auth(binding.edtEmail.text, binding.edtPassword.text)
            }
        }

        binding.imgBack.setOnClickListener {

            val fragmentTransaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameLayout,
                LoginWithEmail(),
                "AdminLoginEmail"
            )
            fragmentTransaction.commit()
        }

        return view
    }

    private fun auth(email: Editable?, password: Editable?) {

        auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = hashMapOf(
                        "email" to email.toString(),
                        "password" to password.toString(),
                        "userID" to auth.uid
                    )

                    auth.uid?.let {
                        firestore.collection("Users")
                            .document(it)
                            .set(user)
                            .addOnSuccessListener {

                                val fragmentTransaction: FragmentTransaction =
                                    requireActivity().supportFragmentManager.beginTransaction()
                                fragmentTransaction.replace(
                                    R.id.frameLayout,
                                    Login(),
                                    "AdminLogin"
                                )
                                fragmentTransaction.commit()

                                Toast.makeText(context, "Account create", Toast.LENGTH_SHORT).show()


                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "" + exception.message, Toast.LENGTH_SHORT)
                                    .show()

                            }

                    }

                } else {
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun isEmpty(): Boolean {
        var empty = true
        when {
            binding.edtEmail.text.toString().isEmpty() -> binding.edtEmail.error =
                "Enter Email"
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