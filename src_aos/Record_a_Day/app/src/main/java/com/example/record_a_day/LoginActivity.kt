package com.example.record_a_day

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.record_a_day.databinding.ActivityLoginBinding
import com.example.record_a_day.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    // 뷰 바인딩 변수
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load(R.drawable.logo).override(256,256).into(binding.loginLogo)
        binding.loginBtn.setOnClickListener {
            Log.i("TESTTEST","${System.currentTimeMillis()}")

            var intent  = Intent(this,MainActivity::class.java)
            startActivity(intent)
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
}