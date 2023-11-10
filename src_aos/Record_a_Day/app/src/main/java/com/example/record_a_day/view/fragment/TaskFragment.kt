package com.example.record_a_day.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.adapter.TaskAdapter
import com.example.record_a_day.databinding.TaskFragmentBinding
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.TaskPresenter
import com.google.firebase.firestore.FirebaseFirestore

class TaskFragment : Fragment(), Contractor.TaskView {
    private var mBinding: TaskFragmentBinding? = null
    private val binding get() = mBinding!!

    var mContext: Context? = null

    private val TAG = "seok"


    private var recyclerView: RecyclerView? = null

    val firestore = FirebaseFirestore.getInstance()

    private var presenter: TaskPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = activity?.let { TaskPresenter(it.applicationContext, this) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TaskFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFunctions()
        presenter?.setEventListener(object: TaskPresenter.EventListener{
            override fun refreshadapter(taskAdapter: TaskAdapter) {
                binding.recyclerView.adapter = taskAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(mContext!!)
                taskAdapter.notifyDataSetChanged()
            }

        })
        presenter?.initRecyclerView()

    }

    private fun initFunctions() {
        binding.addTask.setOnClickListener {
            if (binding.contentTask.text.isEmpty()) {
                Toast.makeText(mContext, "할 일을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            presenter?.addTask(binding.contentTask.text.toString())
            binding.contentTask.text.clear()
        }
    }

}