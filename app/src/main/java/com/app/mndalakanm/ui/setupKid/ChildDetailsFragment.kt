package com.app.mndalakanm.ui.setupKid

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.Window
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.app.mndalakanm.BuildConfig
import com.app.mndalakanm.Mndalakanm
import com.app.mndalakanm.model.SuccessAddChildRes
import com.app.mndalakanm.retrofit.ApiClient
import com.app.mndalakanm.retrofit.ProviderInterface
import com.app.mndalakanm.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.app.mndalakanm.R
import com.app.mndalakanm
.databinding.FragmentChildDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.Collator


class ChildDetailsFragment : Fragment() {
    private lateinit var apiInterface: ProviderInterface
    lateinit var sharedPref: SharedPref
    lateinit var binding: FragmentChildDetailsBinding

    var profileImage: File? = null
    var apps = ArrayList<PInfo>()

    private val GALLERY = 0
    private val CAMERA = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_child_details, container, false)
        sharedPref = SharedPref(requireContext())

        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.imgHeader.setOnClickListener {
            // activity?.onBackPressed()
            sharedPref.clearAllPreferences()
            Navigation.findNavController(binding.root).navigate(R.id.action_menu_to_login_type)

        }
        binding.btnSignIn.setOnClickListener {
            var name = binding.editName.text.toString()
            var age = binding.agePick.text.toString()

            if (name.equals("", true)) {
                binding.editName.error = getString(R.string.empty)
            } else if (age.equals("", true)) {
                binding.agePick.error = getString(R.string.empty)
            } else if (profileImage == null) {
                Toast.makeText(requireContext(), "Please Add Image", Toast.LENGTH_SHORT).show()
            } else {
                AddDetails(name, age)

            }
        }
        binding.agePick.setOnClickListener {
            openPicker()

        }
        binding.addImage.setOnClickListener {
            if (ProjectUtil.checkPermissions(requireActivity())) {
                showPictureDialog()
            } else {
                ProjectUtil.requestPermissions(requireActivity())
            }
        }


        binding.editName.setText(sharedPref.getStringValue(Constant.CHILD_NAME))
        // binding.editName.setText(sharedPref.getStringValue(Constant.CHILD_NAME))

            val myScope = CoroutineScope(Dispatchers.Default)
            myScope.launch {
                // do some background work here
                try {   apps = getPackages(requireContext())
                val db = FirebaseFirestore.getInstance()
                val collectionRef = db.collection("Apps_"+sharedPref.getStringValue(Constant.USER_ID)+"_"+sharedPref.getStringValue(Constant.CHILD_ID))
                val batch = db.batch()
                for (data in apps) {
                    val documentRef = collectionRef.document()
                    batch.set(documentRef, data)
                }
                batch.commit()
                    .addOnSuccessListener {
                        Log.e(TAG, "Batch write succeeded.")
                        myScope.cancel()


                    }
                    .addOnFailureListener { e ->
                        myScope.cancel()

                        Log.e(TAG, "Error writing batch", e)
                    }
                } catch (e: Exception) {
                    myScope.cancel()
                    Log.e(TAG, "Error writing batch", e)
                    Log.e(TAG, "Error writing batch"+ e.message)
                    Log.e(TAG, "Error writing batch"+ e.localizedMessage)

                    e.printStackTrace()
                }
            }

/*
            for (data in apps) {
                collectionRef.add(data)
                    .addOnSuccessListener { documentRef ->
                        Log.d(TAG, "Document added with ID: ${documentRef.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
*/
          // Log.e(TAG, "onCreateView:   flagsflagsflags " + apps.size)


        return binding.root
    }

    fun openPicker() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.attributes?.windowAnimations =
            android.R.style.Widget_Material_ListPopupWindow
        dialog.setContentView(R.layout.number_picker_dialog)
        val lp = WindowManager.LayoutParams()
        val window: Window = dialog.window!!
        lp.copyFrom(window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        val no_btn: TextView = dialog.findViewById(R.id.no_btn)
        val yes_btn: TextView = dialog.findViewById(R.id.yes_btn)
        val numberPicker: NumberPicker = dialog.findViewById(R.id.dialog_number_picker)
        numberPicker.maxValue = 18
        numberPicker.minValue = 3
        numberPicker.wrapSelectorWheel = false

        numberPicker.setOnValueChangedListener { numberPicker, i, i1 ->
            Log.e(TAG, "openPicker: iiiii---" + i)
            Log.e(TAG, "openPicker: 11111---" + i1)
            //  Toast.makeText(requireContext(), ""+i, Toast.LENGTH_SHORT).show()

        }
        no_btn.setOnClickListener { v1: View? -> dialog.dismiss() }
        yes_btn.setOnClickListener { v1: View? ->
            binding.agePick.setText(numberPicker.value.toString())
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("HardwareIds")
    private fun AddDetails(name: String, age: String) {
        Log.e(TAG, "AddDetails: " + profileImage?.path)
        DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
        val profileFilePart: MultipartBody.Part
        val attachmentEmpty: RequestBody
        if (profileImage == null) {
            attachmentEmpty = "".toRequestBody("image/*".toMediaTypeOrNull())
            profileFilePart = MultipartBody.Part.createFormData(
                "attachment",
                "image", attachmentEmpty
            )
        } else {
            profileFilePart = MultipartBody.Part.createFormData(
                "image",
                profileImage!!.name, profileImage!!
                    .asRequestBody("image/*".toMediaTypeOrNull())
            )

        }
        val agedata = age.toRequestBody("text/plain".toMediaTypeOrNull())
        val namedata = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val register = sharedPref.getStringValue(Constant.CHILD_ID).toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        apiInterface.add_child(
            register, agedata,
            namedata, profileFilePart
        ).enqueue(object : Callback<SuccessAddChildRes?> {
            override fun onResponse(
                call: Call<SuccessAddChildRes?>,
                response: Response<SuccessAddChildRes?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body()?.status.equals("1", true)) {
                        sharedPref.setChildDetails(Constant.CHILD_Data, response.body()?.result!!)
                        val bundle = Bundle()
                        bundle.putString("type", "child")
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_splash_to_child_permission_fragment, bundle)
                        sharedPref.setStringValue(Constant.CHILD_NAME, name)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Exception = " + e.message, Toast.LENGTH_SHORT).show()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<SuccessAddChildRes?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                call.cancel()
                Log.e(TAG, "onFailure: " + t.message)
                Log.e(TAG, "onFailure: " + t.cause)
                Log.e(TAG, "onFailure: " + t.localizedMessage)
            }

        })


    }


        suspend fun getPackages(context: Context):
             ArrayList<PInfo> {
            return withContext(Dispatchers.IO) {
                val appList: ArrayList<PInfo> = arrayListOf()

                try {
                    val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
                    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                    var iss = 0
                    for (profile in userManager.userProfiles) {
                        for (app in launcherApps.getActivityList(null, profile)) {
                            val newInfo = PInfo()
                            newInfo.id = iss.toString()
                            newInfo.appname =
                                app.applicationInfo.loadLabel(Mndalakanm.context!!.packageManager).toString()
                            newInfo.pname =  app.applicationInfo.packageName
                            newInfo.versionName =  app.applicationInfo.className
                            newInfo.versionCode =  app.applicationInfo.className.toString()
                            newInfo.icon =  showPictureDialogbash(  app.applicationInfo.loadIcon(Mndalakanm.context!!.packageManager).toBitmap(124,124,Bitmap.Config.ARGB_8888))
                            newInfo.cat = app.applicationInfo.category.toString()
                            appList.add(newInfo)
                            iss++
                        }
                    }
                    appList.sortBy { it.appname.lowercase() }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                appList
            }
        }

    private fun getPackages2(): ArrayList<PInfo> {
        val apps: ArrayList<PInfo> = getInstalledApps(true) /* false = no system packages */
        val max: Int = apps.size
        for (i in 0 until max) {
            apps[i]
            //  Log.e(  apps[i].appname, "\t" +  apps[i].pname + "\t" +   apps[i].versionName + "\t" +   apps[i].versionCode+ "\t" +   apps[i].cat)

        }
        return apps
    }

    private fun getInstalledApps(getSysPackages: Boolean): ArrayList<PInfo> {
        val res = ArrayList<PInfo>()
        val packs: List<PackageInfo> =
            Mndalakanm.context!!.packageManager.getInstalledPackages(0)
        var iss = 0
       /* DataManager.instance
            .showProgressMessage(requireActivity(), getString(R.string.please_wait))
     */   for (i in packs.indices) {

            val p = packs[i]
            if (!getSysPackages && p.versionName == null) {
                // apps with launcher intent
                if (p.applicationInfo.flags == 1) {
                    // updated system apps

                } else {
                    continue
                    // system apps

                }

            }
            Log.e(TAG,
                "getInstalledApps: flagsflagsflags-----   " + p.applicationInfo.loadLabel(Mndalakanm.context!!.packageManager)
                    .toString() + " -----" + p.applicationInfo.category
            )


            val newInfo = PInfo()
            newInfo.id = iss.toString()
            newInfo.appname =
                p.applicationInfo.loadLabel(Mndalakanm.context!!.packageManager).toString()
            newInfo.pname = p.packageName
            newInfo.versionName = p.versionName
            newInfo.versionCode = p.versionCode.toString()
            newInfo.icon =  showPictureDialogbash( p.applicationInfo.loadIcon(Mndalakanm.context!!.packageManager).toBitmap(64,64,Bitmap.Config.ARGB_8888))
            newInfo.cat = p.applicationInfo.category.toString()
            res.add(newInfo)
            iss++
        }
     //   DataManager.instance.hideProgressMessage()

        return res
    }

    private fun showPictureDialogbash(bitmap:Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return base64String
    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle(getString(R.string.select_action))
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> {


                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, GALLERY)
                    // val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //   galleryIntent.type = "image/*"
                    //startActivityForResult(galleryIntent, GALLERY)


                }
                1 -> {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (cameraIntent.resolveActivity(requireActivity().packageManager) != null)
                        startActivityForResult(cameraIntent, CAMERA)

                }
            }
        }
        pictureDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* if (requestCode == GALLERY) {
             if (resultCode == RESULT_OK) {
                 val path: String = RealPathUtil.getRealPath(requireActivity(), data!!.data)!!
                 var imageUri = data.data
                     profileImage = File(path)
                 binding.addImage.setImageURI(imageUri)

             }else{
                 Log.e(TAG, "onActivityResult: errer" )
             }
         } else if (requestCode == CAMERA) {
             if (resultCode == RESULT_OK) {
                 try {
                     if (data != null) {
                         val extras = data.extras
                         val bitmapNew = extras!!["data"] as Bitmap
                         val imageBitmap: Bitmap =
                             BITMAP_RE_SIZER(bitmapNew, bitmapNew.width, bitmapNew.height)!!
                         val tempUri: Uri = ProjectUtil.getImageUri(requireContext(), imageBitmap)!!
                         val image = RealPathUtil.getRealPath(requireContext(), tempUri)
                         profileImage = File(image)
                         Log.e("sgfsfdsfs", "profileImage = $profileImage")
                         binding.addImage.setImageURI(Uri.parse(image))
                     }
                 } catch (e: Exception) {
                     e.printStackTrace()
                     Log.e(TAG, "onActivityResult: "+e.message )
                     Log.e(TAG, "onActivityResult: "+e.localizedMessage )
                     Log.e(TAG, "onActivityResult: "+e.cause )
                 }
             }
         }*/

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                val path: String = RealPathUtil.getRealPath(requireContext(), data!!.data)!!
                profileImage = File(path)
                binding.addImage.setImageURI(data!!.data)
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data != null) {
                        val extras = data.extras
                        val bitmapNew = extras!!["data"] as Bitmap
                        val imageBitmap: Bitmap =
                            BITMAP_RE_SIZER(bitmapNew, bitmapNew.width, bitmapNew.height)!!
                        val tempUri: Uri = ProjectUtil.getImageUri(requireContext(), imageBitmap)!!
                        val image = RealPathUtil.getRealPath(requireContext(), tempUri)
                        profileImage = File(image)
                        Log.e("sgfsfdsfs", "profileImage = $profileImage")
                        binding.addImage.setImageURI(Uri.parse(image))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun BITMAP_RE_SIZER(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val ratioX = newWidth / bitmap.width.toFloat()
        val ratioY = newHeight / bitmap.height.toFloat()
        val middleX = newWidth / 2.0f
        val middleY = newHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bitmap,
            middleX - bitmap.width / 2,
            middleY - bitmap.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }


}