package com.example.record_a_day

import android.os.Bundle
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
    //Firebase Test
    private var mRootRef = FirebaseDatabase.getInstance().getReference()
    private var login_conditionRef = mRootRef.child("id")
    private var pw_conditionRef = mRootRef.child("pw")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load(R.drawable.logo).override(256,256).into(binding.loginLogo)
        binding.loginBtn.setOnClickListener {
            login_conditionRef.setValue(binding.loginId.text.toString())
            pw_conditionRef.setValue(binding.loginPw.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        login_conditionRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
}