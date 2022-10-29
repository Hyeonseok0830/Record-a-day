package com.example.record_a_day.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.record_a_day.view.activity.LoginActivity
import com.example.record_a_day.data.UserData
import com.example.record_a_day.databinding.MyinfoFragmentBinding
import com.example.record_a_day.manager.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.orhanobut.logger.Logger
import java.util.*

class MyInfoFragment : Fragment() {

    private var mBinding: MyinfoFragmentBinding? = null
    private val binding get() = mBinding!!

    private val TAG = "seok"


    lateinit var user_info: String
    lateinit var user_data: UserData

    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = MyinfoFragmentBinding.inflate(inflater, container, false)
        user_info = activity?.let {
            PreferenceManager.getString(it.applicationContext, LoginActivity.USER_INFO_KEY)
        }.toString()
        Logger.i("onCreateView: $user_info")

        val list = arrayListOf<String>()
        StringTokenizer(user_info, "|").apply {
            while (hasMoreTokens()) {
                list.add(nextToken())
            }
        }
        user_data = UserData(list[0], list[1], "", "", "")


        firestore.collection("record")
            .whereEqualTo("id", user_data.id)
            .get()
            .addOnSuccessListener { documents ->
                "${documents.size()} 회".also { binding.recordCount.text = it }
            }


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}