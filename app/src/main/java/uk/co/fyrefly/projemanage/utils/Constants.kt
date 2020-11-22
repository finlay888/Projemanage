package uk.co.fyrefly.projemanage.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object Constants {

    const val USERS : String = "users"
    const val GALLERY_REQUEST_CODE = 1
    const val BOARDS : String = "boards"
    const val NAME : String = "name"
    const val IMAGE : String = "image"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST : String = "taskList"
    const val BOARD_DETAIL : String = "board_detail"
    const val ID : String = "id"
    const val EMAIL : String = "email"
    const val SELECT : String = "Select"
    const val UN_SELECT : String = "UnSelect"
    const val BOARD_MEMBERS_LIST : String = "board_members_list"

    const val PROJEMANAGE_PREFERENCES = "ProjemanagePreferences"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAAXvOU5Ws:APA91bGW1Ut5CwFqiQa2WSBQ3ZMA9RojwAL__Rpv-INp_OQ0CbLowQqE1Yo0gZ8gX4yW_n69ItreLr8plrOrVnQpd15CMCvn8vG8l8qh7VZHIeL1KyOZQbwJfOgQNGQxTn4sGXF-4ijH"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun choosePhotoFromGallery(activity: Activity){
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                if(report!!.areAllPermissionsGranted()){
                    showImageChooser(activity)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permission: MutableList<PermissionRequest>,
                                                            token: PermissionToken)
            {
                showRationalDialogForPermissions(activity)
            }
        }).onSameThread().check()
    }

    fun showRationalDialogForPermissions(activity: Activity){
        AlertDialog.Builder(activity).setMessage(
            "It looks like you have turned off permissions required for this feature." +
                    " It can be enabled under the Application Settings")
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    fun getFileExtension (activity: Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}