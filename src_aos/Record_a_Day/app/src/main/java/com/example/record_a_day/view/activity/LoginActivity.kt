package com.example.record_a_day.view.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.record_a_day.MainActivity
import com.example.record_a_day.R
import com.example.record_a_day.databinding.ActivityLoginBinding
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.LoginPresenter
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger

class LoginActivity : AppCompatActivity(), Contractor.View {

    // 뷰 바인딩 변수
    private var mBinding: ActivityLoginBinding? = null
    private val binding get() = mBinding!!

    //SharedPref 키 값 - 자동 로그인
    companion object {
        const val AUTO_LOGIN_KEY = "auto_login"
        const val USER_INFO_KEY = "user_info"
    }

    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    private val TAG = "seok"

    private var backKeyPressedTime = 0L

    //private val app_mode = "DEBUG" // 메인 화면 진입
    private val app_mode = "COMM"

    private var animationView: LottieAnimationView? = null

    private var presenter: LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate")
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = LoginPresenter(this)

        animationView = findViewById(R.id.animationView)
        buttonInit()
        with(animationView) {
            this?.addAnimatorListener(object : Animator.AnimatorListener {
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
            val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.loginBtn.getWindowToken(), 0);

            binding.animationView.visibility = View.VISIBLE
            if (animationView != null) {
                animationView!!.bringToFront()
                binding.loginBtn.visibility = View.GONE
                animationView!!.playAnimation()
            }
            if (app_mode.equals("DEBUG")) {
                presenter?.autoLogin(this, binding.loginId.text.toString(), binding.autoLogin.isChecked)
            } else {
                presenter?.login_btn(
                    this,
                    binding.loginId.text.toString(),
                    binding.loginPw.text.toString(),
                    binding.autoLogin.isChecked
                )
            }

        }
        //회원가입 버튼 밑줄 처리
        val content = SpannableString(binding.userJoin.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.userJoin.text = content

        //회원가입 버튼
        binding.userJoin.setOnClickListener {
            var intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        Logger.i("onStart")
        var auto_login = PreferenceManager.getString(this, AUTO_LOGIN_KEY)
        if (auto_login.isNullOrEmpty()) {
            binding.loginBackground.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        } else {
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        super.onStart()

    }

    override fun onResume() {
        Logger.i("onResume")
        super.onResume()
    }

    override fun onPause() {
        Logger.i("onPause")
        super.onPause()
    }

    override fun onStop() {
        Logger.i("onStop")
        if (animationView != null) {
            animationView!!.cancelAnimation()
            animationView = null
        }
        super.onStop()
    }

    override fun onDestroy() {
        Logger.i("onDestroy")
        mBinding = null
        super.onDestroy()
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기를 한 번 더 누르시면 종료합니다.", Toast.LENGTH_SHORT).show()
        } else {
            FinishApp()
        }
    }

    private fun FinishApp() {
        finish()
        System.exit(0)

    }


    override fun loginResult(type: String, result: Boolean) {
        if ("Login".equals(type)) {
            animationView!!.cancelAnimation()
            animationView!!.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        } else if ("AutoLogin".equals(type)) {
            // do nothing
        }
    }
}