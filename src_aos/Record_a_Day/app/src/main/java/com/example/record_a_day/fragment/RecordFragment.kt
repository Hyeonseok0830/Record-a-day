package com.example.record_a_day.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.record_a_day.R
import com.example.record_a_day.adapter.RecordAdapter
import com.example.record_a_day.data.RecordItem
import com.example.record_a_day.databinding.MyinfoFragmentBinding
import com.example.record_a_day.databinding.RecordFragmentBinding
import com.example.record_a_day.manager.UserDataManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

class RecordFragment : Fragment() {

    private var mBinding: RecordFragmentBinding? = null
    private val binding get() = mBinding!!

    var mContext: Context? = null

    private val TAG = "seok"

    lateinit var recordAdapter: RecordAdapter
    val datas = mutableListOf<RecordItem>()


    private var search_keyword: String? = null
    private var recyclerView: RecyclerView? = null

    val firestore = FirebaseFirestore.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RecordFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

    }

    private fun initRecyclerView() {


        Log.d(TAG, "initRecyclerView: ${UserDataManager.getInstance(mContext!!).id}")

        firestore.collection("record")
            .whereEqualTo("id", UserDataManager.getInstance(mContext!!).id)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "initRecyclerView: select success")
                for (document in documents) {

                    datas.apply {
                        add(RecordItem(
                                document.data["title"].toString(),
                                document.data["date"].toString(),
                                document.data["weather"].toString()))
                    }

                }
                val source = Observable.create<MutableList<RecordItem>> {
                    it.onNext(datas)
                    it.onComplete()
                }
                source.subscribe(mObserver)
                recordAdapter = RecordAdapter()
                recordAdapter.datas = datas
                binding.recyclerView.adapter = recordAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(mContext!!)
                recordAdapter.notifyDataSetChanged()
                }.addOnFailureListener {
                    Log.e(TAG, "initRecyclerView: error! ")
                }

//        Log.i(TAG, "initRecyclerView: ${datas[0].title}")
//        Log.i(TAG, "initRecyclerView: ${datas[0].date}")
//        Log.i(TAG, "initRecyclerView: ${datas[0].weather}")

    }
    var mObserver = object : DisposableObserver<MutableList<RecordItem>>(){
        override fun onNext(datas: MutableList<RecordItem>) {
            for(data in datas){
//                Log.d(TAG, "onNext: ${data.title}")
//                Log.d(TAG, "onNext: ${data.date}")
//                Log.d(TAG, "onNext: ${data.weather}")
            }

        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            Log.i(TAG, "onComplete: ")

        }

    }

}
