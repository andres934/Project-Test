package test.project

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

import org.json.JSONException
import org.json.JSONObject

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Arrays

import android.Manifest.permission.READ_CONTACTS
import android.content.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    // UI references.
    private val mEmailView: AutoCompleteTextView? = null
    private val mPasswordView: EditText? = null
    private var loginButton: LoginButton? = null
    internal var callbackManager = CallbackManager.Factory.create()
    internal var accessTokenTracker: AccessTokenTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        Log.v("State", "Login")
        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken, newAccessToken: AccessToken) {
                updateWithToken(newAccessToken)
            }
        }
        updateWithToken(AccessToken.getCurrentAccessToken())
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.login_button) as LoginButton
        loginButton?.setReadPermissions(Arrays.asList(
                "public_profile", "email"))
        loginButton?.loginBehavior = LoginBehavior.WEB_ONLY
        loginButton?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("debug", "onSuccess")
                val request = GraphRequest.newMeRequest(loginResult.accessToken,
                        { `object`, response ->
                            Log.v("Facebook Data", "Complete")
                            var email: String? = null
                            var name: String? = null
                            try {
                                email = `object`.getString("email")
                                name = `object`.getString("name")
                                val database = MySqliteHelper(this@LoginActivity)
                                val result = database.getUser(email)
                                Log.v("Result", Arrays.toString(result))
                                val userId: Int
                                if (result == null) {
                                    database.insertUser(name, email, "", "Si", 1)
                                    userId = Integer.parseInt(database.getUser(email)[0])
                                    Log.v("Result", "Creating user")
                                } else {
                                    userId = Integer.parseInt(database.getUser(email)[0])
                                    Log.v("Result", "User already created")
                                }
                                val i = Intent(this@LoginActivity, MainActivity::class.java)
                                i.putExtra("userId", userId)
                                val editor = getSharedPreferences("UserId", Context.MODE_PRIVATE).edit()
                                editor.putInt("userId", userId)
                                editor.commit()
                                startActivity(i)
                                finish()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        })

                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                request.setParameters(parameters)
                request.executeAsync()

            }

            override fun onCancel() {
                // App code
                Toast.makeText(applicationContext,

                        "Canceled",

                        Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                // App code
                Toast.makeText(applicationContext,

                        "Error:" + exception,

                        Toast.LENGTH_LONG).show()
            }


        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return
        }

    }

    private fun updateWithToken(currentAccessToken: AccessToken?) {
        if (currentAccessToken != null) {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    fun logOut() {
        AccessToken.setCurrentAccessToken(null as AccessToken)
        Profile.setCurrentProfile(null as Profile)
    }


}

