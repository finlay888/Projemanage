package uk.co.fyrefly.projemanage.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_my_profile.*
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.firebase.FirestoreClass
import uk.co.fyrefly.projemanage.models.User
import uk.co.fyrefly.projemanage.utils.Constants
import uk.co.fyrefly.projemanage.utils.Constants.GALLERY_REQUEST_CODE
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var contentURI : Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setUpActionBar()

        FirestoreClass().loadUserData(this)

        civ_my_profile_profile_image.setOnClickListener {
            Constants.choosePhotoFromGallery(this)
        }

        btn_my_profile_update.setOnClickListener{
            if (contentURI != null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = resources.getString(R.string.my_profile_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp)
            toolbar_profile_activity.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun setUserDataInUI(user: User){

        mUserDetails = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(civ_my_profile_profile_image)

        et_my_profile_name.setText(user.name)
        et_my_profile_email.setText(user.email)
        if(user.mobile != 0L){
            et_my_profile_mobile.setText(user.mobile.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (data != null) {
                    contentURI = data.data
                    Log.i("contentURI", contentURI.toString())
                    try {
                        Glide
                            .with(this@MyProfileActivity)
                            .load(contentURI)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(civ_my_profile_profile_image)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this, "Failed to load image",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(contentURI != null){
            val sRef : StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "." +
                            Constants.getFileExtension(this, contentURI))

            sRef.putFile(contentURI!!).addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        if (mProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }else {
            userHashMap[Constants.IMAGE] = mUserDetails.image
        }

        userHashMap[Constants.NAME] = et_my_profile_name.text.toString()
        userHashMap[Constants.MOBILE] = et_my_profile_mobile.text.toString().toLong()

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}
