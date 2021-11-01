package com.example.record_a_day

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.record_a_day.data.UserData
import com.example.record_a_day.databinding.ActivityLoginBinding
import com.example.record_a_day.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import com.google.firebase.database.ValueEventListener as ValueEventListener

class LoginActivity : AppCompatActivity() {

    // 뷰 바인딩 변수
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    private val TAG = "TESTTEST"

    private val app_mode = "DEBUG" // 메인 화면 진입
    //private val app_mode = "COMM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load(R.drawable.logo).override(256, 256).into(binding.loginLogo)
        binding.loginBtn.setOnClickListener {
            if(app_mode.equals("DEBUG")){
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                firestore.collection("user")
                    .whereEqualTo("id", binding.loginId.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.data["pw"] == binding.loginPw.text.toString()
                                    .encryptECB()
                            ) {
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