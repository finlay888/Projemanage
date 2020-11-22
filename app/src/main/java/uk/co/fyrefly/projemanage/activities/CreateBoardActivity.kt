package uk.co.fyrefly.projemanage.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.firebase.FirestoreClass
import uk.co.fyrefly.projemanage.models.Board
import uk.co.fyrefly.projemanage.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var  boardPhotoUri : Uri? = null

    private lateinit var mUserName: String

    private var mBoardImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        if (intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        setUpActionBar()

        civ_create_board_activity.setOnClickListener {
            Constants.choosePhotoFromGallery(this)
        }

        btn_create_board.setOnClickListener {
            if(boardPhotoUri != null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = resources.getString(R.string.create_board_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_white_24dp)
            toolbar_create_board_activity.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GALLERY_REQUEST_CODE) {
                if (data != null) {
                    boardPhotoUri = data.data
                    Log.i("contentURI", boardPhotoUri.toString())
                    try {
                        Glide
                            .with(this)
                            .load(boardPhotoUri)
                            .centerCrop()
                            .into(civ_create_board_activity)

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

    private fun createBoard(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        var board = Board(
        et_board_name.text.toString(),
        mBoardImageURL,
        mUserName,
        assignedUsersArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef : StorageReference =
            FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "." +
                        Constants.getFileExtension(this, boardPhotoUri))

        sRef.putFile(boardPhotoUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Board URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                createBoard()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(
                this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_LONG
            ).show()
            hideProgressDialog()
        }
    }

    fun boardCreatedSuccessfully(){
        setResult(Activity.RESULT_OK)
        hideProgressDialog()
        finish()
    }

}
