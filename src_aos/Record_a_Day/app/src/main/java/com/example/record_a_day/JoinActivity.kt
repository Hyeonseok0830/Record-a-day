package com.example.record_a_day

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.record_a_day.databinding.ActivityJoinBinding
import com.example.record_a_day.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class JoinActivity : AppCompatActivity() {

    private var mBinding: ActivityJoinBinding? = null

    private val binding get() = mBinding!!
    //Firebase Test
    private var mRootRef = FirebaseDatabase.getInstance().getReference()
    private var user_conditionRef = mRootRef.child("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //비밀번호 입력
        binding.joinPw.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        //비밀번호 확인 입력
        binding.joinPwC.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        //휴대폰 번호 11자리 입력 시 인증번호 받기 버튼 활성화
        binding.ctnInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ctnPassBtn.isEnabled = s?.length!! >=11
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        //인증 번호 6자리 입력 시 회원가입 버튼 활성화
        binding.ctnResult.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.joinBtn.isEnabled = s?.length!! >=6
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //인증 번호 전송 버튼
        binding.ctnPassBtn.setOnClickListener {
            //인증번호 전송
            val ctn = binding.ctnInput.text.toString()

            binding.ctnResult.visibility = View.VISIBLE
            Toast.makeText(this,"인증번호를 전송하였습니다.",Toast.LENGTH_LONG).show()
        }
        //회원 가입 버튼
        binding.joinBtn.setOnClickListener {
            var user = mutableMapOf(
                "name" to binding.joinName.text.toString(),
                "id" to binding.joinId.text.toString(),
                "pw" to binding.joinPw.text.toString(),
                "birth" to "${binding.joinYear.text}${binding.joinMonth.text}${binding.joinDay.text}",
                "phone" to binding.ctnInput.text.toString()
            )
            user_conditionRef.setValue(user)
            //user_conditionRef.setValue(binding.loginId.text.toString())
            //pw_conditionRef.setValue(binding.loginPw.text.toString())
        }


    }
    override fun onStart() {
        super.onStart()
        user_conditionRef.addValueEventListener(object : ValueEventListener {
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