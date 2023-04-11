package com.app.mndalakanm.ui.Home.Apps


data class ProposalKotlinActivity (var r :String){
}/*
package com.app.mndalakanm.ui.Home.Apps;

import android.webkit.WebChromeClient
import android.Manifest.permission
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.mndalakanm.MainActivity
import com.app.mndalakanm.adapter.AdapterChildRequestsList
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.model.SuccessChildrReqTime
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.ChildRequestListClickListener
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.LocationHandler
import com.app.mndalakanm.utils.SharedPref
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.ActivitySuperviseHomeBinding
import com.app.mndalakanm
.databinding.ChildRequestsBinding
import com.app.mndalakanm.utils.Constant
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProposalKotlinActivity : AppCompatActivity() {
    private lateinit var hint: GifImageView
    private lateinit var refreshbutton: RelativeLayout
    private lateinit var progressDialog: ProgressDialog
    private lateinit var session: Session
    private lateinit var webView: WebView
        var Error: String = ""
        var imageUri: Uri? = null
        private val FILECHOOSER_RESULTCODE = 2888
        private var mUploadMessage: ValueCallback<Uri?>? = null
        private var mCapturedImageURI: Uri? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_proposal_kotlin)

            session = Session(this@ProposalKotlinActivity)
            progressDialog = ProgressDialog(this@ProposalKotlinActivity)
            progressDialog.setCancelable(false)
            progressDialog.show()
            val backbutton = findViewById<ElasticImageView>(R.id.backbutton_pr)
            backbutton.setOnClickListener { onBackPressed() }
            webView = findViewById<WebView>(R.id.webView)
            refreshbutton = findViewById<RelativeLayout>(R.id.refreshbutton)

            refreshbutton.setOnClickListener {
                Error = ""
                if (isNetworkConnected()) {
                    hint.visibility = View.GONE
                    refreshbutton.visibility = View.GONE
                    webView.visibility = View.GONE
                    progressDialog.setMessage("Loading ...")
                    progressDialog.show()
                    //  webView.loadUrl("https://uat.bridgelogicsoftware.com/1096/ajax/route.php?uid=" + session.userId + "&referer=cGlkPTE1MiZhY3Rpb249Y3JlYXRlX3Byb3Bvc2Fs")

                    webView.loadUrl("https://erp.amarpadma.com/ajax/route.php?uid=" + session.userId + "&referer=cGlkPTMwMSZhY3Rpb249c2FsZXNfZGFzaGJvYXJk")
                } else {
                    hint.visibility = View.VISIBLE
                    refreshbutton.visibility = View.VISIBLE
                    webView.visibility = View.GONE

                }
            }

            val settings = webView.settings
            hint = findViewById<GifImageView>(R.id.hint)
            webView.webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.setAppCacheEnabled(true)
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.setAppCachePath(cacheDir.path)
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.loadWithOverviewMode = true
            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)
            settings.mediaPlaybackRequiresUserGesture = false
            settings.allowUniversalAccessFromFileURLs = true
            settings.allowFileAccess = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                settings.safeBrowsingEnabled = true  // api 26
            }


            webView.fitsSystemWindows = true
            webView.webChromeClient = object : WebChromeClient() {

                override fun onPermissionRequest(request: PermissionRequest?) {


                    if (request != null) {
                        request.grant(request.resources)
                    }


                }

                // openFileChooser for Android 3.0+
                fun openFileChooser(uploadMsg: ValueCallback<Uri?>, acceptType: String?) {
                    Log.e("TAG", "openFileChooser: " + acceptType)

                    // Update message
                    mUploadMessage = uploadMsg
                    try {

                        // Create AndroidExampleFolder at sdcard
                        val imageStorageDir = File(
                            Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                            ), "AndroidExampleFolder"
                        )
                        if (!imageStorageDir.exists()) {
                            // Create AndroidExampleFolder at sdcard
                            imageStorageDir.mkdirs()
                        }

                        // Create camera captured image file path and name
                        val file = File(
                            imageStorageDir.toString() + File.separator + "IMG_"
                                    + System.currentTimeMillis().toString() + ".jpg"
                        )
                        mCapturedImageURI = Uri.fromFile(file)

                        // Camera capture image intent
                        val captureIntent = Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                        )
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
                        val i = Intent(Intent.ACTION_GET_CONTENT)
                        i.addCategory(Intent.CATEGORY_OPENABLE)
                        i.type = "image/*"

                        // Create file chooser intent
                        val chooserIntent = Intent.createChooser(i, "Image Chooser")

                        // Set camera intent to file chooser
                        chooserIntent.putExtra(
                            Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent)
                        )

                        // On select image call onActivityResult method of activity
                        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
                    } catch (e: Exception) {
                        Toast.makeText(baseContext, "Exception:$e", Toast.LENGTH_LONG).show()
                    }


                }


                // openFileChooser for Android < 3.0
                fun openFileChooser(uploadMsg: ValueCallback<Uri?>) {
                    openFileChooser(uploadMsg, "")
                }

                //openFileChooser for other Android versions
                fun openFileChooser(
                    uploadMsg: ValueCallback<Uri?>,
                    acceptType: String?,
                    capture: String?
                ) {
                    openFileChooser(uploadMsg, acceptType)
                }

                // The webPage has 2 filechoosers and will send a
                // console message informing what action to perform,
                // taking a photo or updating the file
                override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                    onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId())
                    return true
                }

                override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
                    //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);
                }
            } // End setWebChromeClient
            webView.webViewClient = object : WebViewClient() {


                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    Error = request.toString()


                    if (error != null) {
                        Log.e("TAG", "onReceivedError: " + error)

                        if (error.description.equals("net::ERR_CONNECTION_TIMED_OUT")) {
                            Toast.makeText(
                                applicationContext,
                                "Connection Time Out, Please Check Your Connection.!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }
                    }
                    hint.visibility = View.VISIBLE
                    webView.visibility = View.GONE
                    refreshbutton.visibility = View.VISIBLE

                    super.onReceivedError(view, request, error)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {


                    webView.visibility = View.GONE
                    progressDialog.setMessage("Loading ...")

                    if (progressDialog.isShowing) {
                        //  progressDialog.dismiss()
                    } else {
                        progressDialog.show()
                    }

                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.e("Error", "onPageFinished: " + Error)
                    Log.e("url", "onPageFinished: " + url)
                    if (progressDialog.isShowing) {
                        if (Error.equals("")) {
                            webView.visibility = View.VISIBLE
                        }
                        if (progressDialog.isShowing) {
                            progressDialog.dismiss()
                        }
                    }
                    super.onPageFinished(view, url)
                }

                override fun onReceivedSslError(
                    view: WebView?, handler: SslErrorHandler?, error: SslError?
                ) {
                    Log.e("TAG", "onReceivedSslError: " + error)

                    if (handler != null) {
                        handler.proceed()
                    }

                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null) {
                        if (isNetworkConnected()) {
                            view?.loadUrl(url)
                            hint.visibility = View.GONE
                            refreshbutton.visibility = View.GONE
                            webView.visibility = View.VISIBLE
                        } else {
                            hint.visibility = View.VISIBLE
                            refreshbutton.visibility = View.VISIBLE
                            webView.visibility = View.GONE
                            Toast.makeText(applicationContext, "No Internet!", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                    return true
                }


            }


            if (isNetworkConnected()) {
                hint.visibility = View.GONE
                refreshbutton.visibility = View.GONE
                progressDialog.setMessage("Loading ...")
                progressDialog.show()
                webView.loadUrl("https://erp.amarpadma.com/ajax/route.php?uid=" + session.userId + "&referer=cGlkPTE1MiZhY3Rpb249Y3JlYXRlX3Byb3Bvc2Fs")
                //  webView.loadUrl("https://uat.bridgelogicsoftware.com/1096/ajax/route.php?uid=" + session.userId + "&referer=cGlkPTE1MiZhY3Rpb249Y3JlYXRlX3Byb3Bvc2Fs")

            } else {
                hint.visibility = View.VISIBLE
                refreshbutton.visibility = View.VISIBLE
            }

        }


        override fun onBackPressed() {
            if (webView != null) {
                webView.clearHistory()
                webView.clearCache(true)
                WebStorage.getInstance().deleteAllData()
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun isNetworkConnected(): Boolean {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }*/

    */


