package com.app.mndalakanm.ui.Home.Apps

import android.icu.text.IDNA.Info
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.mndalakanm.adapter.AdapterBlockedAppsList
import com.app.mndalakanm.adapter.AdapterChildList
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.AppClickListener
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.PInfo
import com.app.mndalakanm.utils.SharedPref
import com.google.firebase.firestore.FirebaseFirestore
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentBockedAppsBinding
import kotlinx.coroutines.launch

class BockedAppsFragment : Fragment() , AppClickListener {
    lateinit var binding: FragmentBockedAppsBinding
    lateinit var sharedPref: SharedPref
    private lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bocked_apps, container, false)
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Apps_"+sharedPref.getStringValue(Constant.USER_ID)+"_"+sharedPref.getStringValue(
            Constant.CHILD_ID))

        lifecycleScope.launch {
            try {
                 binding.progressBar.visibility= View.VISIBLE
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
                    var p  = PInfo(id,appname,pname,versionName,versionCode, iconw ,cat)
                    if (p.cat=="-1")
                        else myList.add(p)

                }
                val adapterRideOption = AdapterBlockedAppsList(requireActivity(), myList, this@BockedAppsFragment)
                binding.childList.adapter = adapterRideOption
                binding.childList.setHasFixedSize(true)
                binding.progressBar.visibility= View.GONE

                // Do something with myList
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "onCreateView:  exception "+exception.message )
                Log.e("TAG", "onCreateView:  exception "+exception.localizedMessage )
                Log.e("TAG", "onCreateView:  exception "+exception.cause )

                // Handle any errors
            }

                Log.e("TAG", "onCreateView:  sizesizesizesizesize "+myList.size )
            } catch (e: Exception) {
                // handle the exception
            }
        }
        return binding.root
    }

    override fun onClick(position: Int, model: PInfo, type: String) {

    }
}