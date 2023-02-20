package com.app.mndalakanm.ui.setupParent.Menu

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.adapter.AdapterSupportChat
import com.app.mndalakanm.model.SuccesChatListRes
import com.app.mndalakanm.model.SuccesInsertChatRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentSupportChatBinding
import com.app.mndalakanm.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class SupportChatFragment : Fragment() {
    lateinit var binding: FragmentSupportChatBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support_chat, container, false)
        sharedPref = SharedPref(requireActivity())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.sendBtn.setOnClickListener {
            var msg = binding.edtMessage.text.toString()
            if (msg == "") {
                Mndalakanm.showToast(
                    requireContext(),
                    getString(R.string.empty_message)
                )
            } else {
                binding.edtMessage.setText("")
                sendMessage(msg)
            }
        }

        getChatList()
        handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                getChatList()
                handler.postDelayed(this, 10000) // schedule the function to run again in 5 seconds
            }
        }, 10000)
        return binding.root
    }

    private fun sendMessage(msg: String) {
        Toast.makeText(context, "Sending....", Toast.LENGTH_LONG).show()
        //  DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["sender_type"] = "User"
        map["receiver_id"] = "1"
        map["chat_message"] = msg
        map["sender_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        // https://3tdrive.com/Mndalakanm/webservice/insert_chat?sender_id=1&receiver_id=1&
        // chat_message=This%20is%20test&sender_type=User
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.insert_chat(map).enqueue(object : Callback<SuccesInsertChatRes?> {
            override fun onResponse(
                call: Call<SuccesInsertChatRes?>,
                response: Response<SuccesInsertChatRes?>
            ) {
              //  DataManager.instance.hideProgressMessage()
                try {
                    //Toast.makeText(context, response.body()?.result, Toast.LENGTH_SHORT).show()
                    getChatList()
                } catch (e: Exception) {
                //    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccesInsertChatRes?>, t: Throwable) {
                call.cancel()
             //   DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                Toast.makeText(context, getString(R.string.invalid_code), Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getChatList() {
        val map = HashMap<String, String>()
        map["sender_id"] = "1"
        map["receiver_id"] = sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.getChat(map).enqueue(object : Callback<SuccesChatListRes?> {
            override fun onResponse(
                call: Call<SuccesChatListRes?>,
                response: Response<SuccesChatListRes?>
            ) {
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        var childsList = response.body()!!.result
                       val adapterRideOption = AdapterSupportChat(requireActivity(), childsList)
                        binding.childList.adapter = adapterRideOption
                    } else {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    //Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccesChatListRes?>, t: Throwable) {
                call.cancel()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)

    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)


    }
}