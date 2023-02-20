package com.vilborgtower.user.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.techno.mndalakanm.BuildConfig
import  com.techno.mndalakanm.R
import java.text.DateFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils(context: Context) {

    var context: Context
    var mProgressDialog: Dialog? = null

    init {
        this.context = context
    }

    fun removeLastChar(s: String): String {
        return if (s != "" && s.length > 15) {
            s.substring(0, s.length - 3)
        } else {
            s
        }
    }
    fun removeLastChars(str: String, chars: Int): String? {
        return str.substring(0, str.length - chars)
    }


    fun shareAd(title: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("text/plain")
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        share.putExtra(
            Intent.EXTRA_TEXT, """
     $title
     
     Check out the App at: https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
     """.trimIndent()
        )
        context.startActivity(Intent.createChooser(share, context.getString(R.string.app_name)))
    }

    val darkMode: Boolean
        get() {
            var modeValue = false
            val nightModeFlags =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> modeValue = true
                Configuration.UI_MODE_NIGHT_NO -> modeValue = false
                Configuration.UI_MODE_NIGHT_UNDEFINED -> modeValue = false
            }
            return modeValue
        }

    fun phoneValidation(countryCode: String, number: String): Boolean {
        var isValid = false
        if (countryCode == "57" && number.replace(" ", "").length == 10) {
            isValid = true
        } else if (countryCode == "507" && number.replace(" ", "").length == 8) {
            isValid = true
        } else if (countryCode == "56" && number.replace(" ", "").length == 11) {
            isValid = true
        } else if (countryCode == "1" && number.replace(" ", "").length == 10) {
            isValid = true
        } else if (countryCode == "91" && number.replace(" ", "").length == 10) {
            isValid = true
        } else if (countryCode == "971" && (number.replace(
                " ",
                ""
            ).length == 10 || number.replace(" ", "").length == 9)
        ) {
            isValid = true
        } else if (number.replace(" ", "").length == 10) {
            isValid = true
        }
        return isValid
    }


    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun logException(e: Exception) {
        Log.e("TAG", e.message!!)
    }

    fun showProgressDialog(): Dialog? {
        if (mProgressDialog != null) {
            mProgressDialog!!.cancel()
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        mProgressDialog = Dialog(context, R.style.FullScreenDialog)
        mProgressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val layoutParams: WindowManager.LayoutParams? =
            mProgressDialog!!.getWindow()?.getAttributes()
        mProgressDialog!!.getWindow()?.setAttributes(layoutParams)
        mProgressDialog!!.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        mProgressDialog!!.setContentView(R.layout.progress_loader)
        mProgressDialog!!.show()
        mProgressDialog!!.setCancelable(false)
        return mProgressDialog
    }

    fun dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog!!.cancel()
                mProgressDialog!!.dismiss()
                mProgressDialog = null
            }
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }



    companion object {
        lateinit var context: Context


        fun currency(value: String): String {
            return try {
                NumberFormat.getCurrencyInstance(Locale("en", "AE")).format(value.toDouble())
            } catch (e: Exception) {
                e.printStackTrace()
                value
            }
        }

        fun validUrl(url: String): String {
            return if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "http://$url"
            } else url
        }

        fun dateCheckValidation(SubscriptionDate: String?): Boolean {
            var flag = false
            try {
                val c = Calendar.getInstance().time
                println("Current time => $c")
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = df.format(c)
                val expiryDate = df.format(SubscriptionDate)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                var convertedcurrentDate = Date()
                var convertedSelectedDate = Date()
                try {
                    convertedcurrentDate = dateFormat.parse(currentDate)
                    convertedSelectedDate = dateFormat.parse(expiryDate)
                    println("$convertedcurrentDate=============> $convertedSelectedDate")
                    if (convertedcurrentDate.after(convertedSelectedDate)) {
                        flag = true
                    }
                } catch (e: ParseException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return flag
        }

        fun capitalize(str: String): String {
            return if (str == "") str else str.substring(0, 1).toUpperCase() + str.substring(1)
        }

        fun changeDateFormat(date: String): String {
            return if (date != "") {
                var changeDate = date
                changeDate = try {
                    val spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val newDate = spf.parse(date)
                    val spf1 = SimpleDateFormat("dd MMM yyyy")
                    spf1.format(newDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return changeDate
                }
                changeDate
            } else {
                date
            }
        }

        fun convertDate(inputDateStr: String): String {
            var outputDateStr = ""
            outputDateStr = try {
                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val outputFormat: DateFormat = SimpleDateFormat("EEE, MMM d, h:mm a")
                val date = inputFormat.parse(inputDateStr)
                outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return inputDateStr
            }
            return outputDateStr
        }

        fun changeDate(date: String?): String? {
            var changeDate = date
            changeDate = try {
                val spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val newDate = spf.parse(date)
                val spf1 = SimpleDateFormat("dd/MMM/yyyy")
                spf1.format(newDate)
            } catch (e: Exception) {
                e.printStackTrace()
                return changeDate
            }
            return changeDate
        }

        val currentDate: String
            get() {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                return dateFormat.format(calendar.time)
            }

        fun loadWebView(value: String?, webView: WebView) {
            webView.requestFocus()
            webView.getSettings().setLightTouchEnabled(true)
            webView.getSettings().setJavaScriptEnabled(true)
            webView.getSettings().setGeolocationEnabled(true)
            webView.setSoundEffectsEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.getSettings()
                    .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING)
            } else {
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL)
            }
            webView.getSettings()
                .setBlockNetworkImage(false) //false); // Solve the image does not display
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
            }
            //        webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(false)
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
            //        webView.setBackgroundColor(Color.TRANSPARENT);
            webView.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE)
            //        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM)
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
            webView.getSettings().setLoadsImagesAutomatically(true)
            webView.getSettings().setDomStorageEnabled(true)
            webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }
            })
            //        webView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}iframe{display: inline;height: auto;max-width: 100%;}</style>" + value, "text/html", "UTF-8", null);
//        webView.loadData(value, "text/html", "UTF-8");
            webView.loadUrl(value!!)
        }
    }

}