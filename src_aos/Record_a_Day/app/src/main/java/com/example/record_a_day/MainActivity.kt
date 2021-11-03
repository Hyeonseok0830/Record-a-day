package com.example.record_a_day

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    lateinit var myInfo_anim : Animation
    lateinit var record_anim : Animation
    lateinit var taskList_anim : Animation
    lateinit var goal_anim : Animation
    lateinit var share_anim : Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }
    fun init(){

        myInfo_anim = AnimationUtils.loadAnimation(this,R.anim.myinfo_anim)
        myInfo_anim.fillAfter = true
        record_anim = AnimationUtils.loadAnimation(this,R.anim.record_anim)
        record_anim.fillAfter = true
        taskList_anim = AnimationUtils.loadAnimation(this,R.anim.tasklist_anim)
        taskList_anim.fillAfter = true
        goal_anim = AnimationUtils.loadAnimation(this,R.anim.goal_anim)
        goal_anim.fillAfter = true
        share_anim = AnimationUtils.loadAnimation(this,R.anim.share_anim)
        share_anim.fillAfter = true

        //하단 메뉴 버튼 클릭
        binding.moreBtn.setOnClickListener {
            if(!show)
                showMoreBtn()
        }
    }
    fun showMoreBtn(){
        show=true
        binding.myinfoBtn.apply {
            visibility = View.VISIBLE
            animation = myInfo_anim
            startAnimation(animation)
        }
        binding.recordBtn.apply {
            visibility = View.VISIBLE
            animation = record_anim
            startAnimation(animation)
        }
        binding.taskListBtn.apply {
            visibility = View.VISIBLE
            animation = taskList_anim
            startAnimation(animation)
        }
        binding.goalBtn.apply {
            visibility = View.VISIBLE
            animation = goal_anim
            startAnimation(animation)
        }
        binding.shareBtn.apply {
            visibility = View.VISIBLE
            animation = share_anim
            startAnimation(animation)
        }
        CoroutineScope(Main).launch {
            delay(3000L)
            Log.d(TAG, "showMoreBtn: $show")
            hideMoreBtn()
        }
    }
    fun hideMoreBtn(){
        if(show) {
            binding.myinfoBtn.apply {
                visibility = View.INVISIBLE
                clearAnimation()
            }
            binding.recordBtn.apply {
                visibility = View.INVISIBLE
                clearAnimation()
            }
            binding.taskListBtn.apply {
                visibility = View.INVISIBLE
                clearAnimation()
            }
            binding.goalBtn.apply {
                visibility = View.INVISIBLE
                clearAnimation()
            }
            binding.shareBtn.apply {
                visibility = View.INVISIBLE
                clearAnimation()
            }
            show = false
        }
    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
}