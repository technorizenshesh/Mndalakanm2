package com.app.mndalakanm.ui.Home.Apps

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.app.mndalakanm.R
import com.app.mndalakanm.adapter.AdapterBlockedAppsList
import com.app.mndalakanm.databinding.FragmentBockedAppsBinding
import com.app.mndalakanm.model.SuccessAddChildRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.HashMap

class BockedAppsFragment : Fragment(), AppClickListener {
    lateinit var binding: FragmentBockedAppsBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bocked_apps, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(
            "Apps_" + sharedPref.getStringValue(Constant.USER_ID) + "_" + sharedPref.getStringValue(
                Constant.CHILD_ID
            )
        )

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val myList = ArrayList<PInfo>()

                collectionRef.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Log.e("TAG", "onCreateView: "+document.data.toString() )
                            val appname = document.data["appname"].toString()
                            val pname = document.data["pname"].toString()
                            val cat = document.data["cat"].toString()
                            val iconw = document.data["icon"].toString()
                            val id = document.data["id"].toString()
                            val versionName = document.data["versionName"].toString()
                            val versionCode = document.data["versionCode"].toString()
                            var p = PInfo(id, appname, pname, versionName, versionCode, iconw, cat)
                            //   if (p.cat=="-1") else myList.add(p)
                            myList.add(p)
                        }
                        val adapterRideOption = AdapterBlockedAppsList(
                            requireActivity(),
                            myList,
                            this@BockedAppsFragment
                        )
                        binding.childList.adapter = adapterRideOption
                        binding.childList.setHasFixedSize(true)
                        binding.progressBar.visibility = View.GONE

                        // Do something with myList
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TAG", "onCreateView:  exception " + exception.message)
                        Log.e("TAG", "onCreateView:  exception " + exception.localizedMessage)
                        Log.e("TAG", "onCreateView:  exception " + exception.cause)

                        // Handle any errors
                    }

                Log.e("TAG", "onCreateView:  sizesizesizesizesize " + myList.size)
            } catch (e: Exception) {
                // handle the exception
            }
        }
        return binding.root
    }

    override fun onClick(position: Int, model: PInfo, type: String,imageBitmap:Bitmap) {
        val tempUri: Uri = ProjectUtil.getImageUri(requireContext(), imageBitmap)!!
        val image = RealPathUtil.getRealPath(requireContext(), tempUri)
       val profileImage = File(image)

        Log.e(TAG, "onClick: " + model.appname)
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.doyou_want))
            .setPositiveButton(getString(R.string.ok_loc)) { dilogs, _ ->
                dilogs.dismiss()

                addtorestrict(model,profileImage);
            }
            .setNegativeButton(getString(R.string.cancel)) { dilogs, _ ->
                dilogs.dismiss()

            }
            .create()
            .show()

    }

    private fun addtorestrict(model: PInfo  ,profileImage:File) {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val attachmentEmpty: RequestBody
        val profileFilePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            profileImage.name, profileImage
                .asRequestBody("image/*".toMediaTypeOrNull())
        )
        val child_id = sharedPref.getStringValue(Constant.CHILD_ID).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val parent_id = sharedPref.getStringValue(Constant.USER_ID).toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val namedata = model.appname.toRequestBody("text/plain".toMediaTypeOrNull())
        val app_id = model.pname.toRequestBody("text/plain".toMediaTypeOrNull())
        apiInterface.add_child_apps(
            parent_id, child_id,
            namedata,app_id, profileFilePart
        ).enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {

                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                call.cancel()
                Log.e(TAG, "onFailure: " + t.message)
                Log.e(TAG, "onFailure: " + t.cause)
                Log.e(TAG, "onFailure: " + t.localizedMessage)
            }

        })


    }

}