package ar.tech.lab.status_downloader_for_whatsapp.status.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ar.tech.lab.status_downloader_for_whatsapp.status.R
import ar.tech.lab.status_downloader_for_whatsapp.status.activity.MainActivity
import ar.tech.lab.status_downloader_for_whatsapp.status.databinding.ActivityDrawerBinding
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.makeramen.roundedimageview.RoundedImageView

class DashBoard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDrawerBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default fragment load
        if (savedInstanceState == null) {
            loadFragment(Home())
        }

        auth = FirebaseAuth.getInstance()

        drawerToggle = ActionBarDrawerToggle(
            parent,
            binding.drawerLayout,
            binding.appBar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerToggle!!.drawerArrowDrawable.color = resources.getColor(R.color.teal_200)
        binding.drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()

        val navigationView: NavigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null
        navigationView.setItemIconSize(80)

        binding.appBar.imgLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }


        ///////// header

        ///////// header
        val headerView: View = binding.navView.getHeaderView(0)
        val appName = headerView.findViewById<TextView>(R.id.appname)
        val appVersion = headerView.findViewById<TextView>(R.id.appversion)
        val img = headerView.findViewById<RoundedImageView>(R.id.imgUser)

        auth.let {

            appName.text = it.currentUser!!.displayName
            appVersion.text = it.currentUser!!.email
            Glide.with(applicationContext).load(it.currentUser!!.photoUrl).into(img)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> loadFragment(Home())
            R.id.menu_billing -> loadFragment(AppBilling())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragmentName: Fragment) {
        val fragmentTransaction: FragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragmentName)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}