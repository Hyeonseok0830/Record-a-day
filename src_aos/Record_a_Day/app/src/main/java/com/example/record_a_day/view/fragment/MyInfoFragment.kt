package com.example.record_a_day.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.data.TaskItem
import com.example.record_a_day.data.UserData
import com.example.record_a_day.databinding.MyinfoFragmentBinding
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.MyInfoPresenter
import com.example.record_a_day.view.activity.LoginActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.orhanobut.logger.Logger
import java.util.*

class MyInfoFragment : Fragment(), Contractor.MyInfoView {

    private var mBinding: MyinfoFragmentBinding? = null
    private val binding get() = mBinding!!

    private val TAG = "seok"

    //firestore 객체
    val firestore = FirebaseFirestore.getInstance()

    private var presenter: MyInfoPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        presenter = activity?.let { MyInfoPresenter(it.applicationContext ,this) }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = MyinfoFragmentBinding.inflate(inflater, container, false)

        presenter?.getRecordCount()
        presenter?.getTaskState()
        presenter?.setEventListener(object : MyInfoPresenter.EventListener{
            override fun recordCount(recordCount: Int) {
                binding.recordCount.text = recordCount.toString()
            }

            override fun taskState(taskState: String) {
                binding.todoCount.text = taskState
            }
        })

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}