package com.example.cribb.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.cribb.Main2Activity
import com.example.cribb.R
import com.example.cribb.db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.NetworkInterface
import java.util.*
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
//        val email  = resources.getString(R.string.email)
//        val pass = resources.getString(R.string.password)
//        signIn(email,pass)
        setContentView(R.layout.activity_sign_up)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        val firstName = findViewById<EditText>(R.id.firstName)
        val lastName = findViewById<EditText>(R.id.lastName)


        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@SignUpActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@SignUpActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            //finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                //loginViewModel.login(username.text.toString(), password.text.toString())
                signUp(username.text.toString(), password.text.toString(), firstName.text.toString(),
                    lastName.text.toString())
            }
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun signUp(email: String, password: String, fName: String, lName: String) {
        val b: Boolean? = false
        // [START sign_in_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Test", "Login Success!")
                    val user = auth.currentUser
                    //updateUI(user)
                    val intent = Intent(this, Main2Activity::class.java)
                    startActivity(intent)

                    val signUpInfo = hashMapOf(
                        "Admin" to false,
                        "Dark Mode" to false,
                        "Email" to email,
                        "First Name" to fName,
                        "Last Name" to lName,
                        "IP Address" to getIPAddress(true)
                    )

                    db.collection("Users").document(email)
                        .set(signUpInfo)
                        .addOnSuccessListener {
                            Log.d("Success", "DocumentSnapshot successfully written!")
                            IPaddress(getIPAddress(true))
                        }
                        .addOnFailureListener { e -> Log.w("Fail", "Error writing document", e) }

                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)

                }

                // ...
            }
    }

    fun goToLogIn(v:View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }


}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun IPaddress(ipAddress:String){

    data class Add(
        val occurrences: Long? = null,
        val emails: List<String>? = null
    )
   var docRef = db.collection("IP").document(ipAddress)
    docRef.get()
        .addOnSuccessListener {

            if (it.exists()) {
                docRef.update("occurrences",FieldValue.increment(1))
                    .addOnSuccessListener {
                        val email:String = FirebaseAuth.getInstance().currentUser!!.email!!
                        docRef.update("emails",FieldValue.arrayUnion(email))
                    }
                    .addOnFailureListener { }

            } else {
                val email:String = FirebaseAuth.getInstance().currentUser!!.email!!
                val add = Add(1,listOf(email))
                docRef.set(add)
                    .addOnSuccessListener { }
                    .addOnFailureListener { }
            }
        }
        .addOnFailureListener { }
}

fun getIPAddress(useIPv4 : Boolean): String {
    try {
        var interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            var addrs = Collections.list(intf.getInetAddresses());
            for (addr in addrs) {
                if (!addr.isLoopbackAddress()) {
                    var sAddr = addr.getHostAddress();
                    var isIPv4: Boolean
                    isIPv4 = sAddr.indexOf(':')<0
                    if (useIPv4) {
                        if (isIPv4)
                            return sAddr;
                    } else {
                        if (!isIPv4) {
                            var delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            if (delim < 0) {
                                return sAddr.toUpperCase()
                            }
                            else {
                                return sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        }
    } catch (e: java.lang.Exception) { }
    return ""
}

