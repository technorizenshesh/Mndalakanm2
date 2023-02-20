package com.app.mndalakanm.ui.setupKid

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import  com.techno.mndalakanm.R
import com.app.mndalakanm.ui.Home.SuperviseHomeActivity
import com.techno.mndalakanm.databinding.FragmentRequestAllPermissionsBinding
import com.app.mndalakanm.utils.DeviceAdmin
import android.content.ComponentName;

class RequestAllPermissionsFragment : Fragment() {
    lateinit var binding: FragmentRequestAllPermissionsBinding

    private val REQUEST_CODE = 0
    private var mDPM: DevicePolicyManager? = null
    private var mAdminName: ComponentName? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_request_all_permissions, container, false
        )

        binding.header.imgHeader.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            /* val bundle = Bundle()
             bundle.putString("type", "child")
             Navigation.findNavController(binding.root)
                 .navigate(R.id.action_splash_to_request_all_permissions_fragment, bundle)

 */

            val intent = Intent(activity, SuperviseHomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        try {
            // Initiate DevicePolicyManager.
            mDPM = requireActivity().getSystemService(Context.DEVICE_POLICY_SERVICE)
                    as DevicePolicyManager?
            // Set DeviceAdminDemo Receiver for active the component with different option
            mAdminName = ComponentName(requireActivity(), DeviceAdmin::class.java)
            if (!mDPM!!.isAdminActive(mAdminName!!)) {
                // try to become active
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Click on Activate button to secure your application."
                )
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                // Already is a device administrator, can do security operations now.
              //  mDPM!!.lockNow()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            if (requestCode == Activity.RESULT_OK) {
                // done with activate to Device Admin
            } else {
                // cancle it.
            }
        }
    }
}