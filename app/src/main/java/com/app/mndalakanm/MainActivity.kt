package com.app.mndalakanm

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.app.mndalakanm.databinding.ActivityMainBinding
import com.app.mndalakanm.utils.SharedPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.work.NotifyWork
import com.app.mndalakanm.utils.work.NotifyWork.Companion.NOTIFICATION_WORK
import java.nio.charset.CodingErrorAction.REPLACE
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPref: SharedPref
    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
    private var isPermission = false
    //   lateinit var utils: Utils
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController

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
        sharedPref = SharedPref(this@MainActivity)
        navController = host.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupNavigationMenu(navController)
        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        /*FirebaseApp.initializeApp(*//*context=*//*this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )*/
        if (sharedPref.getStringValue(Constant.LANGUAGE).equals("")) {
            sharedPref.setStringValue(Constant.LANGUAGE, "en")
        }
        setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
        //checkForPermission()
        Log.e(
            "TAG",
            "FIREBASETOKENFIREBASETOKENFIREBASETOKEN: -----   " + sharedPref.getStringValue(Constant.FIREBASETOKEN))

        checkNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermission = isGranted
        }

        userInterface()

        checkPermission()

    }

    private fun checkPermission() {
        if (SDK_INT >= TIRAMISU) {
            if (checkSelfPermission( POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                isPermission = true
            } else {
                isPermission = false

                checkNotificationPermission.launch(POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }

    private fun userInterface() {
        //setSupportActionBar(binding.toolbar)

       // val titleNotification = getString(R.string.notification_title)
       // binding.collapsingToolbarLayout.title = titleNotification

        binding.root.setOnClickListener {
            if (isPermission) {
             /*   val customCalendar = Calendar.getInstance()
                customCalendar.set(
                    binding.datePicker.year,
                    binding.datePicker.month,
                    binding.datePicker.dayOfMonth,
                    binding.timePicker.hour,
                    binding.timePicker.minute, 0
                )
                val customTime = customCalendar.timeInMillis
                val currentTime = currentTimeMillis()
                if (customTime > currentTime) {
                    val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                    val delay = customTime - currentTime
                    scheduleNotification(delay, data)

                    val titleNotificationSchedule = getString(R.string.notification_schedule_title)
                    val patternNotificationSchedule = getString(R.string.notification_schedule_pattern)
                    make(
                        binding.coordinatorLayout,
                        titleNotificationSchedule + SimpleDateFormat(
                            patternNotificationSchedule, getDefault()
                        ).format(customCalendar.time).toString(),
                        LENGTH_LONG
                    ).show()
                } else {
                    val errorNotificationSchedule = getString(R.string.notifications)
                    make(binding.coordinatorLayout, errorNotificationSchedule, LENGTH_LONG).show()
                }*/
            } else {
                if (SDK_INT >= TIRAMISU) {
                    checkNotificationPermission.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK,ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
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
