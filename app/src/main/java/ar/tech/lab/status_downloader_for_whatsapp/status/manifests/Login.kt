package ar.tech.lab.status_downloader_for_whatsapp.status.manifests

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ar.tech.lab.status_downloader_for_whatsapp.Shared
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.dashboard.DashBoard
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    //////////// google sign auth
    private lateinit var googleSignInClient: GoogleSignInClient

    ///////// fb sign auth
    private lateinit var callbackManager: CallbackManager

    private lateinit var completeNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            if (!isEmpty()) {

                binding.ccp.registerCarrierNumberEditText(binding.edtNumber)
                completeNumber = binding.ccp.fullNumberWithPlus.replace("", "").trim { it <= ' ' }

                val bundle = Bundle()
                bundle.putString("number", completeNumber)

                val otp = OtpVerify()
                otp.arguments = bundle

                val fragmentTransaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.frameLayout,
                    otp,
                    "OtpVerify"
                )
                fragmentTransaction.commit()

            }
        }

        binding.imgBack.setOnClickListener {
            activity?.let {
                it.finishAffinity()
            }
        }

        ///////////////// google sign auth ////////////////////////

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("17966654610-3s92kjq74rjhresf4aiiiffdf87su3gh.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.imgGoogle.setOnClickListener {
            signIn()
        }

        ///////////////// login with fb /////////////////

        context?.let { FacebookSdk.sdkInitialize(it) };
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

//        buttonFacebookLogin.setReadPermissions("email", "public_profile")
//        buttonFacebookLogin.registerCallback(
//            callbackManager,
//            object : FacebookCallback<LoginResult> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    Log.d("TAG", "facebook:onSuccess:$loginResult")
//                    handleFacebookAccessToken(loginResult.accessToken)
//                }
//
//                override fun onCancel() {
//                    Log.d("TAG", "facebook:onCancel")
//                }
//
//                override fun onError(error: FacebookException) {
//                    Log.d("TAG", "facebook:onError", error)
//                }
//            })

        binding.imgFaceBook.setOnClickListener {

        }

        //////////////// login with email ///////////////

        binding.imgEmail.setOnClickListener {
            val fragmentTransaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameLayout,
                LoginWithEmail(),
                "OtpVerify"
            )
            fragmentTransaction.commit()
        }

        //////////////////////////////////////

        return view
    }

    /////////////////// fb sign code //////////////////////////

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

        val map = hashMapOf(
            "email" to user?.email.toString(),
            "name" to user?.displayName.toString(),
            "pic" to user?.photoUrl.toString(),
            "number" to user?.phoneNumber.toString()
        )

        auth.uid?.let {

            firestore.collection("Users")
                .document(it)
                .set(map)
                .addOnSuccessListener {

                    startActivity(Intent(context, DashBoard::class.java))
                    Toast.makeText(context, "Account create", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "" + exception.message, Toast.LENGTH_SHORT)
                        .show()

                }
        }

    }

    ///////////////////////////////////////////////////////////

    //////////////////// google sign code/////////////////////////
    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, Shared.REQUEST_CODE_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Shared.REQUEST_CODE_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "onActivityResult: " + account.id)
                fbAuthGoogle(account.idToken)
            } finally {

            }
        }
    }

    private fun fbAuthGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    Log.d("TAG", "fbAuthGoogle: success")
                    val user = auth.currentUser
                    updateUser(user)

                } else {

                    Log.d("TAG", "fbAuthGoogle: fail")
                    updateUser(null)

                }
            }
    }

    private fun updateUser(user: FirebaseUser?) {

        val map = hashMapOf(
            "email" to user?.email.toString(),
            "name" to user?.displayName.toString(),
            "pic" to user?.photoUrl.toString()
        )

        auth.uid?.let {

            firestore.collection("Users")
                .document(it)
                .set(map)
                .addOnSuccessListener {

                    startActivity(Intent(context, DashBoard::class.java))
                    Toast.makeText(context, "Account create", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "" + exception.message, Toast.LENGTH_SHORT)
                        .show()

                }

        }

    }
    //////////////////////////////////////////////////////////////

    private fun isEmpty(): Boolean {
        var empty = true
        when {
            binding.edtNumber.text.toString().isEmpty() -> binding.edtNumber.error = "Enter Number"
            else -> empty = false
        }
        return empty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}