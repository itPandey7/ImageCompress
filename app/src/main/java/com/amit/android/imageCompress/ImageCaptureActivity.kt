package com.amit.android.imageCompress

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_image_capture.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureActivity : AppCompatActivity() {

    var listPermissionsNeeded = ArrayList<String>()
    var permission_list = ArrayList<String>()
    var builder: AlertDialog.Builder? = null

    private var imageFilePath: String? = null
    private var mCompressor: FileCompressor? = null
    var permissionUtils: PermissionUtils? = null

    var permissions = ArrayList<String>()


    private val REQUEST_PHOTO_CAMERA = 0
    private val SELECT_PHOTO_GALERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)
        initView()
         initListener()

    }

    private fun initView() {
        mCompressor = FileCompressor(this)
        builder = AlertDialog.Builder(this)
        permissionUtils = PermissionUtils(this)
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun initListener() {

        ivCamera.setOnClickListener {
            val permissions = ArrayList<String>()
            permissions.add(Manifest.permission.CAMERA)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkAndRequestPermissions(permissions, 1)) {
                selectImage()
            }
        }

    }


    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                cameraIntent()
            } else if (items[item] == "Choose from Library") {
                galleryIntent()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun galleryIntent() {
        val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, SELECT_PHOTO_GALERY)
    }

    private fun cameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(this.packageManager) != null) {
            var photoFile: File? = null
            photoFile = try {
                createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            val photoUri =
                    FileProvider.getUriForFile(this, this.packageName + ".provider", photoFile!!)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, REQUEST_PHOTO_CAMERA)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
       // imageFilePath = image.absolutePath
        //Log.d("CameraPath=====", imageFilePath!!)
        return image
    }


    private fun checkAndRequestPermissions(
            permissions: ArrayList<String>,
            request_code: Int
    ): Boolean {
        if (permissions.size > 0) {
            listPermissionsNeeded = ArrayList()
            for (i in permissions.indices) {
                val hasPermission = ContextCompat.checkSelfPermission(this, permissions[i])
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions[i])
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                        this,
                        listPermissionsNeeded.toTypedArray(),
                        request_code
                )
                return false
            }
        }
        return true
    }




    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO_GALERY && resultCode == RESULT_OK) {
            val selectedImage = data!!.data
            // startCropImageActivity(selectedImage);
            // Log.d("Gallery =====", "" + selectedImage)
        } else if (requestCode == REQUEST_PHOTO_CAMERA && resultCode == RESULT_OK) {

            //Log.d("Camera =====", "" + imageFilePath)
        }
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                val perms: MutableMap<String, Int> = HashMap()
                for (i in permissions.indices) {
                    perms[permissions[i]] = grantResults[i]
                }
                val pending_permissions = ArrayList<String>()
                for (i in listPermissionsNeeded.indices) {
                    if (perms[listPermissionsNeeded[i]] != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        listPermissionsNeeded[i]
                                )
                        ) pending_permissions.add(
                                listPermissionsNeeded[i]
                        ) else {
                            Log.i("Go to settings", "and enable permissions")
                            Toast.makeText(
                                    this,
                                    "Go to settings and enable permissions",
                                    Toast.LENGTH_LONG
                            ).show()
                            return
                        }
                    }
                }
                if (pending_permissions.size > 0) {

//                    showMessageOKCancel(
//                            "Please provide the permission"
//                    )
//                    { dialog, which ->
//                        when (which) {
//                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions(
//                                    permission_list,
//                                    1
//                            )
//                            DialogInterface.BUTTON_NEGATIVE -> {
//                                Log.i("permisson", "not fully given")
//                                if (permission_list.size == pending_permissions.size) Toast.makeText(
//                                        applicationContext, "Permission Denied", Toast.LENGTH_LONG
//                                ).show() else Toast.makeText(
//                                        applicationContext, "Permission Pending", Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
//                    }

                } else {
                    Log.i("all", "permissions granted")
                    Log.i("proceed", "to next step")
                    //  permissionResultCallback.PermissionGranted(1);
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }


//    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
//        android.app.AlertDialog.Builder(this)
//                .setMessage(message)
//                .setPositiveButton("Ok", okListener)
//                .setNegativeButton("Cancel", okListener)
//                .create()
//                .show()
//    }

}