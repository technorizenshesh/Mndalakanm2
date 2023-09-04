package com.app.mndalakanm.ui.loginSignup

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.model.SuccessLoginRes
import com.app.mndalakanm.model.SuccessVerifyOtpRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.InternetConnection
import com.app.mndalakanm.utils.SharedPref
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.app.mndalakanm.R
import com.app.mndalakanm.databinding.FragmentLoginNoBinding
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class LoginNoFragment : Fragment() {
    private lateinit var binding: FragmentLoginNoBinding
    private lateinit var sharedPref: SharedPref
    var type = ""
    var phoneNum = ""
    var verificationId = ""
    lateinit var apiInterface: ProviderInterface
    private var countdown_timer: CountDownTimer? = null
    private var time_in_milliseconds = 60000L
    private var pauseOffSet = 0L
    lateinit  var mAuth: FirebaseAuth
    private lateinit var count: CountDownTimer
    private lateinit var mBottomSheetDialog: RoundedBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_no, container, false)
        if (arguments != null) {
            type = arguments?.getString("type").toString()
        }
        FirebaseApp.initializeApp(requireActivity())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        mAuth = FirebaseAuth.getInstance()
       // mAuth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
       mAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
     //   mAuth.setLanguageCode(sharedPref.getStringValue(Constant.LANGUAGE).toString())
    /*    val nonce: String = ""
        val myScope = CoroutineScope(Dispatchers.Default)
        myScope.launch {
            val integrityManager =
                IntegrityManagerFactory.create(requireActivity())
            integrityManager.requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setNonce(nonce)
                    .build())

             .addOnSuccessListener {
                    Log.e(TAG, "Batch write succeeded.")
                    myScope.cancel()


                }
                .addOnFailureListener { e ->
                    myScope.cancel()

                    Log.e(TAG, "Error writing batch", e)
                }

        }
        myScope.cancel()*/



        binding.btnSignIn.setOnClickListener {
            var code = binding.ccp.selectedCountryCode.toString()
            var no = binding.editNo.text.toString()
            if (TextUtils.isEmpty(no)) {
                Mndalakanm.showToast(requireContext(), getString(R.string.please_enter_phone_no))
            } else {
                if (InternetConnection.checkConnection(requireContext())) {
                    phoneNum = "+" + code + no
                    Timber.tag(TAG).e("onCreateView: %s", phoneNum)
                    DataManager.instance
                        .showProgressMessage(requireActivity(), getString(R.string.please_wait))
                    val options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNum)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .requireSmsValidation(false)
                        //.setCallbacks(callbacks)
                        .setCallbacks(object :
                            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onCodeSent(
                                verificationd: String,
                                forceResendingToken: PhoneAuthProvider.ForceResendingToken
                            ) {
                                verificationId = verificationd
                                Timber.tag(TAG).e(
                                    "PhoneAuthOptionsPhoneAuthOptions:  verificationId %s",
                                    verificationId
                                )
                                DataManager.instance.hideProgressMessage()
                                showDialog("", "")
                            }

                            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                DataManager.instance.hideProgressMessage()
                                mBottomSheetDialog.dismiss()
                                signInWithCredential(phoneAuthCredential)

                                Timber.tag(TAG).e(
                                    "PhoneAuthOptionsPhoneAuthOptions:  onVerificationCompleted %s",
                                    phoneAuthCredential.smsCode
                                )
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                DataManager.instance.hideProgressMessage()
                                if (e.localizedMessage.equals(
                                        "The format of the phone number provided is incorrect",
                                        false
                                    )
                                ) {
                                    Mndalakanm.showAlert(
                                        requireContext(),
                                        getString(R.string.invalid_mobile_no)
                                    )
                                } else {
                                    Mndalakanm.showAlert(
                                        requireContext(),
                                        e.localizedMessage.toString()
                                    )
                                }

                                Timber.tag(TAG)
                                    .e(
                                        "PhoneAuthOptionsPhoneAuthOptions:  onVerificationFailed %s",
                                        e.message
                                    )
                                Timber.tag(TAG)
                                    .e(
                                        "PhoneAuthOptionsPhoneAuthOptions:  onVerificationFailed  %s",
                                        e.localizedMessage
                                    )

                            }
                        })
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    //  LoginSignupByUser(no)

                } else {
                    Mndalakanm.showConnectionDialog(requireContext())
                }

            }


        }
        return binding.root
    }

    private fun verifyCode(code: String, mBottomSheetDialog: RoundedBottomSheetDialog) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        mBottomSheetDialog.dismiss()
        signInWithCredential(credential)
    }

    private fun signInWithCredential(
        credential: PhoneAuthCredential
    ) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    DataManager.instance.hideProgressMessage()
                    LoginSignupByUser(task.result.user?.uid.toString())
                    Toast.makeText(requireContext(), "Verification Successfull", Toast.LENGTH_LONG)
                        .show()
                    Log.e(TAG, "signInWithCredential: uiddd " + task.result.user?.uid)
                } else {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun LoginSignupByUser(ud_id: String) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.getting_user))
        var map = HashMap<String, String>()
        map.put("mobile", phoneNum)
        map.put("ud_id", ud_id)
        map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        val tz = TimeZone.getDefault()
        val id = tz.id
        map["time_zone"] = id.toString()
        if (sharedPref.getStringValue(Constant.LANGUAGE).toString() == "en") map["language"] =
            "English"
        if (sharedPref.getStringValue(Constant.LANGUAGE).toString() == "ar") map["language"] =
            "Arabic"
        if (sharedPref.getStringValue(Constant.LANGUAGE).toString() == "ku") map["language"] =
            "Kurdish"
        else map["language"] = "English"
        Log.e(TAG, "Login user Request = $map")
        apiInterface.login_signup(map).enqueue(object : Callback<SuccessLoginRes?> {
            override fun onResponse(
                call: Call<SuccessLoginRes?>,
                response: Response<SuccessLoginRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()!!.getStatus().equals("1")) {
                        val res: SuccessLoginRes = response.body()!!
                        Log.e(
                            TAG,
                            "res.getResult()?.id .toString(): " + res.getResult()?.id.toString()
                        )
                        sharedPref.setStringValue(Constant.USER_ID, res.getResult()?.id.toString())
                        sharedPref.setStringValue(Constant.USER_TYPE, "provider")
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        sharedPref.setStringValue(Constant.LOCK, "0")
                        val bundle = Bundle()
                        bundle.putString("type", type)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_login_no_to_set_pin, bundle)
                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<SuccessLoginRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Log.e(TAG, "onFailure: " + t.localizedMessage)
                Log.e(TAG, "onFailure: " + t.cause.toString())
                Log.e(TAG, "onFailure: " + t.message.toString())
            }
        })
    }

    private fun showDialog(otp: String?, mobile: String?) {
        mBottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        val sheetView: View = mBottomSheetDialog.layoutInflater.inflate(R.layout.otp_bottam, null)
        mBottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.setCancelable(false)
        val close = mBottomSheetDialog.findViewById<ImageView>(R.id.close)
        val btn_sign_in = mBottomSheetDialog.findViewById<Button>(R.id.btn_sign_in)
        val tv_timer = mBottomSheetDialog.findViewById<TextView>(R.id.tv_timer)
        tv_timer!!.isClickable = false
        tv_timer.text = "00:" + "${(time_in_milliseconds / 1000)}"
        count = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_timer.text = "" + millisUntilFinished / 1000
                tv_timer.isEnabled = false
            }

            override fun onFinish() {
                if (tv_timer != null) {
                    tv_timer.text = getString(R.string.resend)
                    tv_timer.isEnabled = true
                }
            }
        }.start()


        tv_timer.setOnClickListener {
            mBottomSheetDialog.dismiss()
            binding.btnSignIn.performClick()
        }
        val et1 = mBottomSheetDialog.findViewById<EditText>(R.id.et1)
        val et2 = mBottomSheetDialog.findViewById<EditText>(R.id.et2)
        val et3 = mBottomSheetDialog.findViewById<EditText>(R.id.et3)
        val et4 = mBottomSheetDialog.findViewById<EditText>(R.id.et4)
        val et5 = mBottomSheetDialog.findViewById<EditText>(R.id.et5)
        val et6 = mBottomSheetDialog.findViewById<EditText>(R.id.et6)
        if (et1 != null) {
            if (et2 != null) {
                if (et3 != null) {
                    if (et4 != null) {
                        if (et5 != null) {
                            if (et6 != null) {
                                configOtpEditText(et1, et2, et3, et4, et5, et6)
                            }
                        }
                    }
                }
            }
        }
        et6?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().equals("", true)) {
                    btn_sign_in?.performClick()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        close!!.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }
        btn_sign_in!!.setOnClickListener {
            val et1txt = et1!!.text.toString()
            val et2txt = et2!!.text.toString()
            val et3txt = et3!!.text.toString()
            val et4txt = et4!!.text.toString()
            val et5txt = et5!!.text.toString()
            val et6txt = et6!!.text.toString()
            if (TextUtils.isEmpty(et1txt)) {
                et1.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et2txt)) {
                et2.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et3txt)) {
                et3.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et4txt)) {
                et4.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et5txt)) {
                et5.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et6txt)) {
                et6.error = getString(R.string.empty)
            } else {
                val otp = et1txt + et2txt + et3txt + et4txt + et5txt + et6txt
                // VerifyOTP(otp, mobile,mBottomSheetDialog)
                verifyCode(otp, mBottomSheetDialog)

            }
        }
        mBottomSheetDialog.show()
    }

    private fun VerifyOTP(
        otp: String,
        mobile: String?,
        mBottomSheetDialog: RoundedBottomSheetDialog
    ) {

        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        var map = HashMap<String, String>()
        map.put("otp", otp)
        if (mobile != null) {
            map.put("mobile", mobile)
        }
        Log.e(TAG, "Login user Request = $map")
        apiInterface.verify_otp(map).enqueue(object : Callback<SuccessVerifyOtpRes?> {
            override fun onResponse(
                call: Call<SuccessVerifyOtpRes?>,
                response: Response<SuccessVerifyOtpRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()!!.getStatus().equals("1")) {
                        response.body()!!.getMessage()?.let {
                            Mndalakanm.showToast(
                                requireContext(),
                                it
                            )
                        }
                        mBottomSheetDialog.dismiss()

                        response.body()!!.getResult()?.id?.let {
                            sharedPref.setStringValue(
                                Constant.USER_ID,
                                it
                            )
                        }
                        sharedPref.setStringValue(Constant.USER_TYPE, "provider")
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        val bundle = Bundle()
                        bundle.putString("type", type)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_splash_to_plans, bundle)
                    } else {
                        Toast.makeText(context, response.body()!!.getMessage(), Toast.LENGTH_SHORT)
                            .show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("Exception", "Exception = " + e.message)
                }
            }

            override fun onFailure(call: Call<SuccessVerifyOtpRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Log.e(TAG, "onFailure: " + t.localizedMessage)
                Log.e(TAG, "onFailure: " + t.cause.toString())
                Log.e(TAG, "onFailure: " + t.message.toString())
            }

        })


    }

    fun configOtpEditText(vararg etList: EditText) {
        val afterTextChanged = { index: Int, e: Editable? ->
            val view = etList[index]
            val text = e.toString()
            when (view.id) {
                etList[0].id -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                }
                etList[etList.size - 1].id -> {
                    if (text.isEmpty()) etList[index - 1].requestFocus()
                }
                else -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                    else etList[index - 1].requestFocus()
                }
            }
            false
        }
        etList.forEachIndexed { index, editText ->
            editText.doAfterTextChanged { afterTextChanged(index, it) }
        }
    }

    override fun onDetach() {
        try{
            count.cancel()}catch (e:Exception){
            e.printStackTrace()
        }
        super.onDetach()
    }

    override fun onDestroy() {
        try{
        count.cancel()}catch (e:Exception){
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
