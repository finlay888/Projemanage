package uk.co.fyrefly.projemanage.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import uk.co.fyrefly.projemanage.R
import uk.co.fyrefly.projemanage.firebase.FirestoreClass
import uk.co.fyrefly.projemanage.models.User

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpActionBar()

        auth = FirebaseAuth.getInstance()

        btn_sign_in.setOnClickListener {
            signInUser()
        }

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigate_before_black_24dp)
            toolbar_sign_in_activity.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        //update UI accordingly
        if(currentUser != null){
            Toast.makeText(
                this,
                "Already Signed in",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun signInUser (){
        val email : String = et_sign_in_email.text.toString().trim { it <= ' '}
        val password : String = et_sign_in_password.text.toString().trim { it <= ' '}
        showProgressDialog(resources.getString(R.string.logging_in))
        if (validateForm(email, password)){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        FirestoreClass().loadUserData(this)
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email : String, password : String) : Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            else -> true
        }
    }

    fun signInSuccess(user : User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
