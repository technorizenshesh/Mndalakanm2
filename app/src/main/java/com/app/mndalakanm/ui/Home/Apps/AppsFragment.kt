package com.app.mndalakanm.ui.Home.Apps

import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.utils.PInfo
import com.app.mndalakanm.R
import com.app.mndalakanm.databinding.FragmentAppsBinding
import com.app.mndalakanm.utils.Constant
import com.app.mndalakanm.utils.SharedPref
import kotlin.math.log

class AppsFragment : Fragment() {
    lateinit var binding: FragmentAppsBinding
     lateinit var  sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_apps,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        Log.e("TAG", "onCreateView:type iew:type "+ sharedPref.getStringValue(Constant.USER_TYPE).toString())
          if (sharedPref.getStringValue(Constant.USER_TYPE).toString()=="child") binding.cccc.visibility = View.GONE
        binding.cccc.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_block)
        }
        binding.systemApps.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_system)
        }
        binding.webFiltering.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.apps_to_web_filtering)
        }
//val  apss =  getPackages()

            //    Log.e("TAG", "onCreateView:  apss .size-----"+ apss.size)
      /*   for (app in apss){
             Log.e("TAG", "onCreateView:   cat ----- "+ app.cat)
             Log.e("TAG", "onCreateView: appname----- "+ app.appname)

         }*/
        return binding.root
    }



    private fun getPackages(): ArrayList<PInfo> {
        val apps: ArrayList<PInfo> = getInstalledApps(false) /* false = no system packages */
        val max: Int = apps.size
        for (i in 0 until max) {
            apps[i]
                Log.e(  apps[i].appname, "\t" +  apps[i].pname + "\t" +   apps[i].versionName + "\t" +   apps[i].versionCode+ "\t" +   apps[i].cat)

        }
        return apps
    }

    private fun getInstalledApps(getSysPackages: Boolean): ArrayList<PInfo> {
        val res = ArrayList<PInfo>()
        val packs: List<PackageInfo> = Mndalakanm.context!!.packageManager.getInstalledPackages(0)
        var  iaa = 0
        for (i in packs.indices) {
            val p = packs[i]
            if (!getSysPackages && p.versionName == null) {
                continue
            }
            val newInfo = PInfo()
            newInfo.id = iaa.toString()
            newInfo.appname = p.applicationInfo.loadLabel(Mndalakanm.context!!.packageManager).toString()
            newInfo.pname = p.packageName
            newInfo.versionName = p.versionName
            newInfo.versionCode = p.versionCode.toString()
            newInfo.icon = p.applicationInfo.loadIcon(Mndalakanm.context!!.packageManager).toString()
            newInfo.cat = p.applicationInfo.category.toString()
            res.add(newInfo)
            iaa++
        }
        return res
    }
}