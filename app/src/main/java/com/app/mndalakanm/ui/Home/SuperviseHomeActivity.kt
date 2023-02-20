package com.app.mndalakanm.ui.Home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.*
import com.app.mndalakanm.MainActivity
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.service.ScreenshotWork
import com.app.mndalakanm.utils.SharedPref
import com.techno.mndalakanm.R
import com.techno.mndalakanm.databinding.ActivitySuperviseHomeBinding
import com.vilborgtower.user.utils.Constant


class SuperviseHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivitySuperviseHomeBinding
    lateinit var navController: NavController
    lateinit var sharedPref: SharedPref
    lateinit var model: ChildData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supervise_home)
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home_fragment_container)
                    as NavHostFragment? ?: return
        navController = host.navController
        setupWithNavController(binding.navView, navController)
        sharedPref = SharedPref(this)
        model = sharedPref.getChildDetails(Constant.CHILD_Data)
        if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
            binding.imgHeader.setImageDrawable(this.getDrawable(R.drawable.logout_child))
        }
        binding.imgHeader.setOnClickListener {
            if (sharedPref.getStringValue(Constant.USER_TYPE).equals("child", true)) {
                sharedPref.clearAllPreferences()

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                onBackPressed()
            }

        }
        binding.tvLogo.text = model.name
        binding.imgMenu.setOnClickListener {

        }
    }
    fun checkAccessibilityPermission(): Boolean {
        var accessEnabled = 0
        try {
            accessEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return if (accessEnabled == 0) {
            // if not construct intent to request permission
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // request permission via start activity for result
            startActivity(intent)
            false
        } else {
            true
        }
    }
    override fun onResume() {
        getTimerApi()

        super.onResume()
    }

    private fun getTimerApi() {
    }




}