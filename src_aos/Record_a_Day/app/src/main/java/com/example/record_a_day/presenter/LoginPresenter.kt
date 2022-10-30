package com.example.record_a_day.presenter

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.widget.Toast
import com.example.record_a_day.MainActivity
import com.example.record_a_day.model.LoginModel
import com.example.record_a_day.view.activity.JoinActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class LoginPresenter(view: Contractor.LoginView?) : Contractor.LoginPresenter {
    var view: Contractor.LoginView? = null
    var loginModel: LoginModel? = null
    val firestore = FirebaseFirestore.getInstance()

    init {
        this.view = view
        loginModel = LoginModel(this)
    }


    override fun login_btn(context: Context, inputId: String, inputPw: String, autoLogin: Boolean) {
        firestore.collection("user")
            .whereEqualTo("id", inputId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data["pw"] == inputPw.encryptECB()) {
                        val id = document.data["id"]
                        val name = document.data["name"]
                        if (autoLogin) {
                            loginModel?.setPreference(context, "Login", "$name|$id")
                        } else {
                            loginModel?.setPreference(context, "Login", "")
                        }
                        loginModel?.setPreference(context, "User", "$name|$id")
                        if (!inputId.isNullOrEmpty()) {
                            //view?.loginResult()
                            var intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            view?.loginResult("Login", true)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "비밀번호가 일치하지 않습니다.",
                            Toast.LENGTH_LONG
                        ).show()

                        view?.loginResult("Login", false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                view?.loginResult("Login", false)
                Logger.w("Error getting documents:$exception")
            }
            .addOnCompleteListener { it ->
                if (it.result != null && it.result?.isEmpty!!) {
                    Toast.makeText(
                        context,
                        "존재하지 않는 아이디입니다.\n회원가입을 진행해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    view?.loginResult("Login", false)
                }
            }
    }

    override fun autoLogin(context: Context, inputId: String, autoLogin: Boolean) {

        if (autoLogin) {
            loginModel?.setPreference(context, "Login", inputId)
        } else {
            loginModel?.setPreference(context, "Login", "")
        }
        loginModel?.setPreference(context, "User", "현석|test")
        var intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        view?.loginResult("AutoLogin", true)
    }


    /**
     * ECB 암호화
     */
    private fun String.encryptECB(): String {
        val keySpec = SecretKeySpec(JoinActivity.SECRET_KEY.toByteArray(), "AES")    /// 키
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")     //싸이퍼
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)       // 암호화/복호화 모드
        val ciphertext = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(ciphertext, Base64.DEFAULT)
        return String(encodedByte)
    }
}