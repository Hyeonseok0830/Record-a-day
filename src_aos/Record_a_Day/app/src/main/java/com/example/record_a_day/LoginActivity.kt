package com.example.record_a_day

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation

import com.example.record_a_day.databinding.ActivityLoginBinding
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.manager.UserDataManager
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LoginActivity : AppCompatActivity() {

    // 뷰 바인딩 변수
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    //SharedPref 키 값 - 자동 로그인
    companion object{
        const val AUTO_LOGIN_KEY = "auto_login"
        const val USER_INFO_KEY = "user_info"
    }
    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    private val TAG = "seok"

    private var backKeyPressedTime = 0L

    //private val app_mode = "DEBUG" // 메인 화면 진입
    private val app_mode = "COMM"

    private var animationView:LottieAnimationView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: $this")
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animationView = findViewById(R.id.animationView)
        buttonInit()
        with(animationView) {
            this?.addAnimatorListener(object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    //TODO("Not yet implemented")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    //TODO("Not yet implemented")
                }

                override fun onAnimationCancel(animation: Animator?) {
                    //TODO("Not yet implemented")
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    //TODO("Not yet implemented")
                }

            })
        }


    }

    private fun buttonInit() {
        binding.loginBtn.setOnClickListener {
            binding.animationView.visibility = View.VISIBLE
            if(animationView!=null){
                animationView!!.bringToFront()
                binding.loginBtn.visibility=View.GONE
                animationView!!.playAnimation()
            }
            var id = binding.loginId.text.toString()
            if(app_mode.equals("DEBUG")){
                if(binding.autoLogin.isChecked) {
                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,id)
                } else {
                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
                }
                PreferenceManager.setString(this, USER_INFO_KEY,"정현석|test")

                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                firestore.collection("user")
                    .whereEqualTo("id", binding.loginId.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.data["pw"] == binding.loginPw.text.toString().encryptECB()) {
                                val id = document.data["id"]
                                val name = document.data["name"]
                                if(binding.autoLogin.isChecked) {
                                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"$name|$id")
                                } else {
                                    PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
                                }
                                PreferenceManager.setString(this, USER_INFO_KEY,"$name|$id")
                                if(!binding.loginId.text.toString().isNullOrEmpty()) {
                                    var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    animationView!!.cancelAnimation()
                                    animationView!!.visibility = View.GONE
                                    binding.loginBtn.visibility=View.VISIBLE

                                }

                            } else {
                                animationView!!.cancelAnimation()

                                Toast.makeText(
                                    this@LoginActivity,
                                    "비밀번호가 일치하지 않습니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                    }
                    .addOnFailureListener { exception ->
                        animationView!!.cancelAnimation()

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
        Log.i(TAG, "onStart: $this")
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

    override fun onResume() {
        Log.i(TAG, "onResume: $this")
        super.onResume()
    }

    override fun onPause() {
        Log.i(TAG, "onPause: $this")
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "onStop: $this")
        if(animationView!=null){
            animationView!!.cancelAnimation()
            animationView = null
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: $this")
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
    override fun onBackPressed() {
        if(System.currentTimeMillis()>backKeyPressedTime+2000){
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로 가기를 한 번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT).show()
        } else {
            FinishApp()
        }
    }
    fun FinishApp(){
        finish()
        System.exit(0)

    }

}