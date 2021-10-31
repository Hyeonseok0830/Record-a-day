package com.example.record_a_day

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
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
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.log

class JoinActivity : AppCompatActivity() {

    private var mBinding: ActivityJoinBinding? = null

    private val binding get() = mBinding!!
    //Firebase Test
    private var mRootRef = FirebaseDatabase.getInstance().getReference()
    private var user_conditionRef = mRootRef.child("user")
    private val TAG = "TESTTEST"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //비밀번호 입력
        binding.joinPw.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.
                Log.d(TAG, "onTextChanged:1 "+textValidate(s.toString()))
                Log.d(TAG, "onTextChanged:2 "+s.toString())
                if(s?.length!!>=8&&s.length<=16) {
                    if(!textValidate(s.toString())){
                        binding.passInfo.visibility = View.GONE
                    } else {
                        binding.passInfo.visibility = View.VISIBLE
                    }
                } else {
                    binding.passInfo.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        //비밀번호 확인 입력
        binding.joinPwC.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.joinPw.text.toString().equals(binding.joinPwC.text.toString())){
//                    binding.passInfo.setTextColor(Color.parseColor("#00FF19"))
//                    binding.passInfo.text = "비밀번호가 일치합니다."
                    binding.passCInfo.visibility = View.GONE
                } else {
                    binding.passCInfo.visibility = View.VISIBLE
                }
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
            }
        })
    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
    fun textValidate(str :String?):Boolean{
        val Password_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|]*$"
        var pattern  = Pattern.compile(Password_PATTERN)
        var matcher = pattern.matcher(str)
        return matcher.matches()
    }
    fun textValidate2(str :String?):Boolean{
        val Password_PATTERN = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|]*$"
        var pattern  = Pattern.compile(Password_PATTERN)
        var matcher = pattern.matcher(str)
        return matcher.matches()
    }
}