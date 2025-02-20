package com.hackfusion

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.addCallback
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.parseColor("#FFC100")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setupBottomNav()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost)
                val currentFragment = navHostFragment?.childFragmentManager?.fragments?.lastOrNull()

                if (currentFragment !is Home_Fragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navHost, Home_Fragment())
                        .addToBackStack(null)
                        .commit()

                    binding.bottomNavigation.post {
                        binding.bottomNavigation.selectedItemId = R.id.homeIcon
                    }
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        if (savedInstanceState == null) {
            switchFragment(Home_Fragment(), "Home")
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeIcon -> switchFragment(Home_Fragment(), "Home")
                R.id.electionIcon -> switchFragment(Election_Fragment(), "Election")
                R.id.facilityIcon -> switchFragment(Facility_Fragment(), "Facility")
                R.id.complaintIcon -> switchFragment(Complaint_Fragment(), "Complaint")
                R.id.budgetIcon -> switchFragment(Budget_Fragment(), "Budget")
                else -> false
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.navHost)

        if (currentFragment != null && currentFragment.tag == tag) {
            return
        }

        val transaction = fragmentManager.beginTransaction()
        fragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

        transaction.replace(R.id.navHost, fragment, tag)
        if (tag != "Home") {
            transaction.addToBackStack("Home")
        }

        transaction.commit()
    }
}
