package com.kest.softij

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kest.softij.api.model.Res
import com.kest.softij.api.model.User
import org.w3c.dom.Text
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstName : EditText
    private lateinit var lastName : EditText
    private lateinit var email : EditText
    private lateinit var telephone : EditText
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText
    private lateinit var loading:ProgressBar
    private lateinit var registerBtn:Button
    private lateinit var loginBtn:TextView
    private var emailStr:String? = null
    private var passwordStr:String? = null
    private val liveData = MutableLiveData<Res<Unit>>()
    private lateinit var userInfo: LiveData<Res<User>>
    private var pressedTime:Long = 0

    companion object{
        const val REQUEST_NEW_USER = 1
        const val EMAIL_KEY = "EMAIL_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
        const val RESPONSE_KEY = "RES_KEY"
        const val RESULT_EXIT = 5050
        private val  VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailStr = savedInstanceState?.getString(EMAIL_KEY)
        passwordStr = savedInstanceState?.getString(PASSWORD_KEY)
        loading = findViewById(R.id.register_progressBar)
        firstName = findViewById(R.id.FirstName)
        lastName = findViewById(R.id.LastName)
        email = findViewById(R.id.Email)
        telephone = findViewById(R.id.telephone)
        password = findViewById(R.id.Password)
        confirmPassword = findViewById(R.id.confirmPassword)

        findViewById<TextView>(R.id.alreadyHaveAccount).setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        liveData.observe(this,{
            it?.let { res ->
                if(res.code > 0){
                    Toast.makeText(this,"retrieving your data",Toast.LENGTH_LONG).show()
                    userInfo = SoftijRepository
                        .getRepo()
                        .getUserId(emailStr!!)
                    setObserver()
                }else{
                    Toast.makeText(this,res.msg,Toast.LENGTH_LONG).show()
                    enable()
                }
            }
        })

        registerBtn = findViewById<Button>(R.id.btnRegister).apply {
            setOnClickListener{
                if (registerUser()){
                    emailStr = email.text.toString()
                    passwordStr = password.text.toString()
                    disable()
                    SoftijRepository
                        .getRepo()
                        .register(
                            firstName.text.toString(),
                            lastName.text.toString(),
                            password.text.toString(),
                            email.text.toString(),
                            telephone.text.toString(),
                            liveData
                        )
                    Toast.makeText(this@RegisterActivity,"This might Take a minute don't press back button",Toast.LENGTH_LONG).show()
                }
            }
        }
        loginBtn = findViewById<TextView>(R.id.alreadyHaveAccount).apply{
            setOnClickListener {
                startActivityForResult(Intent(this@RegisterActivity,LoginActivity::class.java),LoginActivity.REQUEST_LOGIN)
            }
        }

    }

    private fun setObserver(){
        userInfo.observe(this,{
            it?.let{ res->
                if(res.code > 0){
                    res.data?.password = passwordStr!!
                    setResult(RESULT_OK,Intent().putExtra(RESPONSE_KEY,res.data!!))
                }else {
                    Toast.makeText(this,res.msg,Toast.LENGTH_LONG).show()
                }
                finish()
            }?:run{
                Toast.makeText(this,"Empty Body",Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LoginActivity.REQUEST_LOGIN && resultCode == RESULT_OK){
            val user = data?.getSerializableExtra(LoginActivity.RESPONSE_KEY)
            setResult(RESULT_OK,Intent().putExtra(RESPONSE_KEY,user))
            finish()
        }
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            setResult(RESULT_EXIT)
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun disable(){
        loading.visibility = View.VISIBLE
        loginBtn.isEnabled = false
        registerBtn.isEnabled = false
        firstName.isEnabled = false
        lastName.isEnabled = false
        email.isEnabled = false
        telephone.isEnabled = false
        password.isEnabled = false
        confirmPassword.isEnabled = false
    }

    private fun enable(){
        loading.visibility = View.GONE
        loginBtn.isEnabled = true
        registerBtn.isEnabled = true
        firstName.isEnabled = true
        lastName.isEnabled = true
        email.isEnabled = true
        telephone.isEnabled = true
        password.isEnabled = true
        confirmPassword.isEnabled = true
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(EMAIL_KEY,emailStr)
        outState.putString(PASSWORD_KEY,passwordStr)
    }

    private fun validateName(): Boolean {
        if (lastName.text.length >30 || lastName.text.length <4 ){
            lastName.error = "length should be in between 4 and 30"
            return false
        }

        if (firstName.text.length >30 || firstName.text.length <4 ){
            firstName.error = "length should be in between 4 and 30"
            return false
        }
        return true
    }

    private fun validatePhone() : Boolean {
        if (telephone.text.length!=10){
            telephone.error = "Length should be 10"
            return false
        }
        return true
    }

    private fun validatePassword() : Boolean {
        if(password.text.length in 4..20){
            println(password.text)
            println(confirmPassword.text)
            if (password.text.toString() == confirmPassword.text.toString()){
                return true
            }
            confirmPassword.error = "Passwords do not match"
            return false
        }
        password.error = "password length should be in between 4 and 20"
        return false
    }

    private fun validateEmail(): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email.text)
        if(matcher.find()){
            return true
        }
        email.error = "invalid email"
        return false
    }

    //This function will execute when user clicks on Register Button
    private fun registerUser() : Boolean {
        if (!validateName() or !validatePhone() or !validateEmail() or !validatePassword()) {
            return false
        }
        return true
    }


}

