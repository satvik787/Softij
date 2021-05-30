package com.kest.softij

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kest.softij.api.model.Res
import com.kest.softij.api.model.User

class LoginActivity : AppCompatActivity() {


    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var loading:ProgressBar
    private lateinit var forgotPassword:TextView
    private val liveData = MutableLiveData<Res<Unit>>()
    private lateinit var userInfo: LiveData<Res<User>>
    private lateinit var registerBtn:TextView
    private lateinit var loginBtn:Button
    private var emailStr:String? = null
    private var passwordStr:String? = null

    companion object{
        const val EMAIL_KEY = "EMAIL_KEY"
        const val PASSWORD_KEY = "PASSWORD_KEY"
        const val REQUEST_LOGIN = 5
        const val RESPONSE_KEY = "USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        passwordStr = savedInstanceState?.getString(PASSWORD_KEY)
        emailStr = savedInstanceState?.getString(EMAIL_KEY)

        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        loading = findViewById(R.id.login_progressBar)

        registerBtn = findViewById<TextView>(R.id.signup).apply { setOnClickListener{ finish() }}

        loginBtn = findViewById<Button>(R.id.btnlogin).apply {
            setOnClickListener {
                emailStr = email.text.toString()
                passwordStr = password.text.toString()

                disable()
                SoftijRepository
                    .getRepo()
                    .login(email.text.toString(),password.text.toString(),liveData)
                Toast.makeText(this@LoginActivity,"This might Take a minute \n don't press back button",Toast.LENGTH_LONG).show()
            }
        }



        liveData.observe(this,{
            it?.let { res ->
                if(res.code > 0) {
                    Toast.makeText(this, "retrieving your data", Toast.LENGTH_LONG).show()
                    userInfo = SoftijRepository
                        .getRepo()
                        .getUserId(emailStr!!)
                    setObserver()
                }else {
                    Toast.makeText(this,res.msg,Toast.LENGTH_LONG).show()
                    enable()
                }
            }
        })

        forgotPassword = findViewById(R.id.forgotPassword)

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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(EMAIL_KEY,emailStr)
        outState.putString(PASSWORD_KEY,emailStr)
    }

    private fun disable(){
        loading.visibility = View.VISIBLE
        loginBtn.isEnabled = false
        registerBtn.isEnabled = false
        email.isEnabled = false
        password.isEnabled = false
    }

    private fun enable(){
        loading.visibility = View.GONE
        loginBtn.isEnabled = true
        registerBtn.isEnabled = true
        email.isEnabled = true
        password.isEnabled = true
    }

}