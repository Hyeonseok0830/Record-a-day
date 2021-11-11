package com.example.record_a_day

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.record_a_day.databinding.ActivityLoginBinding
import com.example.record_a_day.manager.PreferenceManager
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    // 뷰 바인딩 변수
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    //SharedPref 키 값 - 자동 로그인
    private val AUTO_LOGIN_KEY = "auto_login"
    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    private val TAG = "TESTTEST"

    private val app_mode = "DEBUG" // 메인 화면 진입
    //private val app_mode = "COMM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginBtn.setOnClickListener {
            if(app_mode.equals("DEBUG")){
                if(binding.autoLogin.isChecked) {
                    var id = binding.loginId.text.toString()
                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,id)
                } else {
                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
                }
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                firestore.collection("user")
                    .whereEqualTo("id", binding.loginId.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.data["pw"] == binding.loginPw.text.toString().encryptECB()) {
                                if(binding.autoLogin.isChecked) {
                                    var id = document.data["id"]
                                    var name = document.data["name"]
                                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"$id|$name")
                                } else {
                                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
                                }
                                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "비밀번호가 일치하지 않습니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }

        }
        //회원가입 버튼 밑줄 처리
        val content = SpannableString(binding.userJoin.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.userJoin.text = content

        //회원가입 버튼
        binding.userJoin.setOnClickListener {
            var intent  = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        var auto_login = PreferenceManager.getString(this,AUTO_LOGIN_KEY)
        if(auto_login.isNullOrEmpty()){
            binding.loginBackground.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        } else{
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        super.onStart()

    }


    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }

    /**
     * ECB 암호화
     */
    private fun String.encryptECB(): String{
        val keySpec = SecretKeySpec(JoinActivity.SECRET_KEY.toByteArray(), "AES")    /// 키
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")     //싸이퍼
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)       // 암호화/복호화 모드
        val ciphertext = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(ciphertext, Base64.DEFAULT)
        return String(encodedByte)
    }
}