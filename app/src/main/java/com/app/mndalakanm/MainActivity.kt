package com.app.mndalakanm

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ActivityMainBinding
import com.vilborgtower.user.utils.Constant
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPref: SharedPref
 //   lateinit var utils: Utils
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController:  NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
      //  utils = Utils(get)
     //   utils?.getFirebaseRegisterId()
      appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.splash_nav, R.id.login_type_nav
            )
        )
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment? ?: return

        // Set up Action Bar
        sharedPref= SharedPref(this@MainActivity)
         navController = host.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupNavigationMenu(navController)
        if (sharedPref.getStringValue(Constant.LANGUAGE).equals("")) {
            sharedPref.setStringValue(Constant.LANGUAGE, "en")
        }
        setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
        //checkForPermission()
    }


    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.navigation)
        sideNavView?.setupWithNavController(navController)
    }

    fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1000
            )
        } else {
            // already permission granted
           // handlerMethod()
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) { // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED
            ) {
                handlerMethod()
            } else {
                utils?.showToast(getString(R.string.permission_denied))
            }
        }
    }*/

   /* fun handlerMethod() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (PrefManager.getBoolean(Constant.IS_LOGIN)) {
                *//* val intent = Intent(applicationContext, HomeActivity::class.java)
                 startActivity(intent)
                 finish()*//*
            } else {
                navController.navigate(R.id.action_splash_to_login_type)

                *//* val intent = Intent(applicationContext, LoginActivity::class.java)
                 startActivity(intent)
                 finish()*//*
            }
        }, 3000)
    }*/

    private fun setLocale(lang: String?) {
        // Create a new Locale object
        val locale = Locale(lang)
        Locale.setDefault(locale)
        // Create a new configuration object
        val config = Configuration()
        // Set the locale of the new configuration
        config.setLocale(locale)
        // Update the configuration of the Accplication context
        resources.updateConfiguration(
            config,
            resources.displayMetrics
        )
        if (lang != null) {
            sharedPref.setStringValue(Constant.LANGUAGE, lang)
        }
    }
}
