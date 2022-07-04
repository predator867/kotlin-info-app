package ar.tech.lab.status_downloader_for_whatsapp.status.manifests

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.dashboard.DashBoard
import ar.tech.lab.status_downloader_for_whatsapp.status.activity.MainActivity
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class SplashScreen : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseStorage
    private lateinit var activity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseStorage.getInstance()

        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as MainActivity
    }

    private fun moveFarmgents() {
        Handler(Looper.getMainLooper()).postDelayed({
            val fragmentTransaction: FragmentTransaction =
                activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, Login(), "AdminLogin")
            fragmentTransaction.commit()
        }, 3000)
    }

    override fun onStart() {
        super.onStart()
        if (auth.uid == null) {
            moveFarmgents()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({

                startActivity(Intent(context, DashBoard::class.java))

            }, 3000)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}