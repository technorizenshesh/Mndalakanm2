package com.app.mndalakanm.ui.setupParent

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.app.mndalakanm.model.SuccessChildsListRes
import com.app.mndalakanm.adapter.AdapterChildList
import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.retrofit.ApiClient
import  com.techno.mndalakanm.R
import com.app.mndalakanm.ui.Home.SuperviseHomeActivity
import com.app.mndalakanm.utils.ChildClickListener
import com.app.mndalakanm.utils.DataManager
import com.app.mndalakanm.utils.SharedPref
import com.app.mndalakanm.retrofit.ProviderInterface
import com.techno.mndalakanm.databinding.FragmentNodeviceParentBinding
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class NodeviceParentFragment : Fragment() , ChildClickListener {
    lateinit var binding: FragmentNodeviceParentBinding
    lateinit var navController: NavController
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    private lateinit var childsList:  ArrayList<ChildData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_nodevice_parent, container, false)
        if (container != null) {
            navController = container.findNavController()
        }
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.imgMenu.visibility= View.VISIBLE
        binding.header.imgHeader.setImageResource(R.drawable.setting)
        binding.header.imgHeader.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("type", "parent")
            Log.e("TAG", "onCreateView: "+"--------------------" )
            navController.navigate(R.id.action_splash_to_menu_fragment, bundle)

        }
        binding.header.imgMenu.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_support_fragment, bundle)


        }
        binding. btnSignIn.setOnClickListener{
            val intent = Intent( activity, SuperviseHomeActivity::class.java)
            startActivity(intent)
        }
        binding. btnAnother.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("type", "parent")
            navController.navigate(R.id.action_splash_to_code_generated, bundle)
        }
        gethildList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
    private fun gethildList() {
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val map = HashMap<String, String>()
        map["parent_id"] =  sharedPref.getStringValue(Constant.USER_ID).toString()
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_parent_child(map).enqueue(object : Callback<SuccessChildsListRes?> {
            override fun onResponse(call: Call<SuccessChildsListRes?>, response: Response<SuccessChildsListRes?>) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body() != null && response.body()?.status.equals("1")) {
                        childsList = response.body()!!.result
                        val adapterRideOption =
                            AdapterChildList(requireActivity(),
                                childsList,this@NodeviceParentFragment )
                        binding.childList.adapter = adapterRideOption
                    }else{
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessChildsListRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
            }
        })
    }

    override fun onClick(position: Int, model: ChildData) {
        sharedPref.setStringValue(Constant.CHILD_ID,model.id.toString())
        sharedPref.setStringValue(Constant.CHILD_NAME,model.name.toString())
        sharedPref.setChildDetails(Constant.CHILD_Data,model)
        val intent = Intent( activity, SuperviseHomeActivity::class.java)
        startActivity(intent)
    }

}