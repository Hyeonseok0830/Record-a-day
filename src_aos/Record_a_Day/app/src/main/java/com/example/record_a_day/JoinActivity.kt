package com.example.record_a_day



import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.record_a_day.databinding.ActivityJoinBinding
import com.example.record_a_day.util.SoftKeyboardDectectorView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class JoinActivity : AppCompatActivity() {

    private var mBinding: ActivityJoinBinding? = null

    private val binding get() = mBinding!!
    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    val auth = FirebaseAuth.getInstance()
    private val TAG = "seok"

    companion object {
        const val SECRET_KEY = "ABCDEFGH12345678"
    }

    private var timerTask: Timer? = null

    private var storedVerificationId:String?=null
    private var resendToken:PhoneAuthProvider.ForceResendingToken?=null

    private val callbacks by lazy{
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                //super.onCodeSent(verificationId, token)
                storedVerificationId = verificationId
                resendToken = token
                //Log.d(TAG,"verificationId = "+verificationId+", resendToken = "+token.toString())
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUI()
        val mSoftKeyboardDectectorView = SoftKeyboardDectectorView(this)
        addContentView(mSoftKeyboardDectectorView, FrameLayout.LayoutParams(-1,-1))

        mSoftKeyboardDectectorView.setOnShownKeyboard(object :SoftKeyboardDectectorView.OnShownKeyboardListener{
            override fun onShowSoftKeyboard() {
                binding.logoImg.visibility = View.GONE
            }
        })
        mSoftKeyboardDectectorView.setOnHiddenKeyboard(object :SoftKeyboardDectectorView.OnHiddenKeyboardListener{
            override fun onHiddenSoftKeyboard() {
                binding.logoImg.visibility = View.VISIBLE
            }

        })
    }
    fun setUI(){
        //비밀번호 입력
        binding.joinPw.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.
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
        //생년월일 입력 필드
        binding.joinYear.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!!>=4){
                    binding.joinMonth.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        //생년월일 입력 필드
        binding.joinMonth.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!!>=2){
                    binding.joinDay.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        //생년월일 입력 필드
        binding.joinDay.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!!>=2){
                    binding.ctnInput.requestFocus()
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
//                binding.joinBtn.isEnabled = s?.length!! >=6
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        //인증 번호 전송 버튼
        binding.ctnPassBtn.setOnClickListener {
            binding.authTimer.visibility = View.VISIBLE
            var time = 60
            //인증번호 전송
            val ctn = binding.ctnInput.text.toString()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82$ctn")       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            if(timerTask!=null){
                timerTask?.cancel()
            }
            timerTask = kotlin.concurrent.timer(period = 1000) {
                time--
                val sec = time

                runOnUiThread {
                    if(sec<15){
                        binding.authTimer.setTextColor(Color.parseColor("#FF0000"))
                    }
                    if(sec<10){
                        binding.authTimer.text = "00:0$sec"
                    }else {
                        binding.authTimer.text = "00:$sec"
                    }
                }
                if(time<=0){
                    runOnUiThread {
                        timerTask?.cancel()
                        binding.authTimer.text = "00:60"
                        binding.authTimer.setTextColor(Color.parseColor("#FF000000"))
                        binding.authTimer.visibility = View.GONE
                    }
                }
            }
            binding.ctnAuthLayout.visibility = View.VISIBLE
            Toast.makeText(this,"인증번호를 전송하였습니다.",Toast.LENGTH_LONG).show()
        }
        //인증하기 버튼
        binding.ctnPassAuthBtn.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, binding.ctnResult.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
        //회원 가입 버튼
        binding.joinBtn.setOnClickListener {
            val encrypt_pass = binding.joinPw.text.toString().encryptECB()
            var user = mutableMapOf(
                "name" to binding.joinName.text.toString(),
                "id" to binding.joinId.text.toString(),
                "pw" to encrypt_pass,
                "birth" to "${binding.joinYear.text}${binding.joinMonth.text}${binding.joinDay.text}",
                "phone" to binding.ctnInput.text.toString()
            )
            firestore.collection("user")
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "onCreate: Success add user info")
                }
                .addOnFailureListener {
                    Log.d(TAG, "onCreate: Fail add user info")

                }
            finish()
        }
    }
    override fun onStart() {
        super.onStart()

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
    /**
     * ECB 암호화
     */
    private fun String.encryptECB(): String{
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")    /// 키
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")     //싸이퍼
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)       // 암호화/복호화 모드
        val ciphertext = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(ciphertext, Base64.DEFAULT)
        return String(encodedByte)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this,"인증 완료",Toast.LENGTH_LONG).show()
                    binding.joinBtn.isEnabled = true
                    timerTask?.cancel()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    Toast.makeText(this,"인증에 실패하였습니다. 다시 시도해주세요.",Toast.LENGTH_LONG).show()
                    // Update UI
                }
            }
    }
}