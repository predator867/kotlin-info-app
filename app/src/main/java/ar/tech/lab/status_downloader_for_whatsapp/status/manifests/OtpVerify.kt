package ar.tech.lab.status_downloader_for_whatsapp.status.manifests

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import ar.tech.lab.status_downloader_for_whatsapp.status.ConnectionDetector
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.dashboard.DashBoard
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentOtpVerifyBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class OtpVerify : Fragment() {

    private var _binding: FragmentOtpVerifyBinding? = null
    private val binding get() = _binding!!

    private var verficationCode: String? = null
    var forceResendingToken: ForceResendingToken? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    var time = 60
    private lateinit var connectionDetector: ConnectionDetector
    private lateinit var inputMethodManager: InputMethodManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOtpVerifyBinding.inflate(inflater, container, false)

        // ini db
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        binding.resendotp.isEnabled = false
        connectionDetector = ConnectionDetector(requireContext())

        binding.shownumber.text = arguments?.getString("number")


        ///////// check google play services available or not before otp send to user
        if (isGooglePlayServicesAvailable(this@OtpVerify)) {
            sendcodetouser(arguments?.getString("number")!!)
        }

        binding.btnVerify.setOnClickListener(View.OnClickListener {
            if (connectionDetector.isConnectingToInternet) {
                val entercodeotp: String = binding.opt1.text.toString() +
                        binding.opt2.text.toString() +
                        binding.opt3.text.toString() +
                        binding.opt4.text.toString() +
                        binding.opt5.text.toString() +
                        binding.opt6.text.toString()
                if ((binding.opt2.text.toString().trim { it <= ' ' }.isEmpty()
                            && binding.opt2.text.toString().trim { it <= ' ' }.isEmpty()
                            && binding.opt2.text.toString().trim { it <= ' ' }.isEmpty()
                            && binding.opt2.text.toString().trim { it <= ' ' }.isEmpty()
                            && binding.opt2.text.toString().trim { it <= ' ' }.isEmpty()
                            && binding.opt2.text.toString().trim { it <= ' ' }.isEmpty())
                    || entercodeotp.length < 6
                ) {
                    Toast.makeText(context, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                } else {
                    verifycode(entercodeotp)

                    binding.btnVerify.visibility = View.GONE
                }
            } else Toast.makeText(
                context,
                getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        })

        binding.resendotp.setOnClickListener(View.OnClickListener {
            time = 60
            binding.resendotp.isEnabled = false
            resendVerificationCode(arguments?.getString("number")!!, forceResendingToken!!)
        })

        binding.loginBack.setOnClickListener(View.OnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, Login(), "AdminLogin")
            fragmentTransaction.commit()
        })

        numbertopmove()

        return binding.root
    }

    private fun resendVerificationCode(phoneNumber: String, token: ForceResendingToken) {

        // progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            requireActivity(),  // Activity (for callback binding)
            mCallbacks,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

    private fun sendcodetouser(number: String) {
        Log.d("TAGCALLED", "sendcodetouser: CALLED")
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // OnVerificationStateChangedCallbacks
    }

    var mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                binding.resendotp.isEnabled = true
                Toast.makeText(
                    context,
                    "Failed to send OTP, Please Try Again!",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAGCALLED", "onVerificationFailed: FAILED $e")
                // progressBar.setVisibility(View.GONE);

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAGCALLED", "onVerificationFailed: SUCCESS")
                Toast.makeText(context, "OTP Code sent", Toast.LENGTH_SHORT).show()
                binding.btnVerify.visibility = View.VISIBLE
                //progressBar.setVisibility(View.GONE);
                verficationCode = verificationId
                forceResendingToken = token
                object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.countTimer.visibility = View.VISIBLE
                        binding.resendotp.visibility = View.GONE
                        binding.countTimer.text = "0:" + checkDigit(time)
                        time--
                    }

                    override fun onFinish() {
                        binding.countTimer.visibility = View.GONE
                        binding.resendotp.visibility = View.VISIBLE
                        binding.resendotp.isEnabled = true
                    }
                }.start()
            }
        }

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun verifycode(codeEnteredByUser: String) {

//        progressBar.setVisibility(View.VISIBLE);
        val credential = PhoneAuthProvider.getCredential(verficationCode!!, codeEnteredByUser)
        // now last function that will aloww to sign
        Log.d("OTPCRED", verficationCode + "verifycode: " + codeEnteredByUser)
        Log.d("OTPCRED", "verifycode: $credential")
        signIn(credential)
    }

    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserExit()
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show();
                }
            }
    }

    private fun checkUserExit() {


        mAuth.uid?.let {

            firestore.collection("Users")
                .document(it)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.exists()) {

                        val document: DocumentSnapshot = task.result
                        if (document.getString("number")!! == requireArguments().getString("number")
                        ) {
                            startActivity(Intent(context, DashBoard::class.java))
                            Toast.makeText(context, "Account create", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        createUser()
                    }
                }

        }

    }


    private fun createUser() {

        val map = hashMapOf(
            "number" to arguments?.getString("number")
        )

        mAuth.uid?.let {

            firestore.collection("Users")
                .document(it)
                .set(map)
                .addOnSuccessListener {

                    startActivity(Intent(context, DashBoard::class.java))
                    Toast.makeText(context, "Account create", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "${exception.message}", Toast.LENGTH_SHORT).show()

                }

        }

    }


    private fun numbertopmove() {

        binding.opt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt2.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        binding.opt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.opt3.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt1.requestFocus()
                }
            }
        })
        binding.opt3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.opt4.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt2.requestFocus()
                }
            }
        })
        binding.opt4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt5.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt3.requestFocus()
                }
            }
        })
        binding.opt5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt6.requestFocus()
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt4.requestFocus()
                }
            }
        })
        binding.opt6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.opt5.requestFocus()
                }
            }
        })
    }

    private fun isGooglePlayServicesAvailable(activity: OtpVerify): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(requireActivity())
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404)!!.show()
            }
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}