package com.example.record_a_day.view.activity



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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import java.util.Timer
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
                Logger.d("onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext,"인증된 앱이 아닙니다. 테스트 번호를 사용해 주세요\n번호 : 010-1111-1111, 인증 번호 : 111111",Toast.LENGTH_LONG).show()
                Logger.e("onVerificationFailed:$e")

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
                Log.d(TAG,"verificationId = "+verificationId+", resendToken = "+token.toString())
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
                binding.joinBtn.isEnabled = s?.length!! >=11
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

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
                    Toast.makeText(this,"회원가입이 완료되었습니다.",Toast.LENGTH_LONG).show()
                    Logger.d("onCreate: Success add user info")
                }
                .addOnFailureListener {
                    Toast.makeText(this,"알 수 없는 이유로 회원가입에 실패하였습니다.",Toast.LENGTH_LONG).show()
                    Logger.d("onCreate: Fail add user info")
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
        var matcher = pattern.matcher(str!!)
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
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Logger.d("signInWithCredential:success")
//                    Toast.makeText(this,"인증 완료",Toast.LENGTH_LONG).show()
//                    binding.joinBtn.isEnabled = true
//                    timerTask?.cancel()
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    Logger.d("signInWithCredential:failure  ${task.exception}")
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        // The verification code entered was invalid
//                    }
//                    Toast.makeText(this,"인증에 실패하였습니다. 다시 시도해주세요.",Toast.LENGTH_LONG).show()
//                    // Update UI
//                }
//            }
//    }
}