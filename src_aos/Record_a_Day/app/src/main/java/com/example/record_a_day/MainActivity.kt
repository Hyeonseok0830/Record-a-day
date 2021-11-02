package com.example.record_a_day

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.record_a_day.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var mBinding:ActivityMainBinding? = null

    private val binding get() = mBinding!!

    private var show = false

    private val TAG = "TESTTEST"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }
    fun init(){
        //하단 메뉴 버튼 클릭
        binding.moreBtn.setOnClickListener {
            if(!show)
                showMoreBtn()
        }
    }
    fun showMoreBtn(){
        show=true
        binding.myinfoBtn.visibility = View.VISIBLE
        binding.recordBtn.visibility = View.VISIBLE
        binding.taskListBtn.visibility = View.VISIBLE
        binding.goalBtn.visibility = View.VISIBLE
        binding.shareBtn.visibility = View.VISIBLE
        CoroutineScope(Main).launch {
            delay(3000L)
            Log.d(TAG, "showMoreBtn: $show")
            hideMoreBtn()
        }
    }
    fun hideMoreBtn(){
        if(show) {
            binding.myinfoBtn.visibility = View.INVISIBLE
            binding.recordBtn.visibility = View.INVISIBLE
            binding.taskListBtn.visibility = View.INVISIBLE
            binding.goalBtn.visibility = View.INVISIBLE
            binding.shareBtn.visibility = View.INVISIBLE
            show = false
        }
    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
}