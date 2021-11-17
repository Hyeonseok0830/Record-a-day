package com.example.record_a_day

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.record_a_day.databinding.ActivityMainBinding
import com.example.record_a_day.fragment.MyInfoFragment
import com.example.record_a_day.manager.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var mBinding:ActivityMainBinding? = null

    private val binding get() = mBinding!!

    private var show = false

    private val TAG = "TESTTEST"
    private val AUTO_LOGIN_KEY = "auto_login"

    private var backKeyPressedTime = 0L

    lateinit var fragmentManager : FragmentManager
    lateinit var myInfoFragment : MyInfoFragment
    lateinit var transaction : FragmentTransaction

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
        fragment_inint()
        button_init()
    }
    //----------------------------------------------------------------------------------------------
    // Initialize Method
    //----------------------------------------------------------------------------------------------
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
        binding.logout.setOnClickListener {
            PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
            var i = Intent(this,LoginActivity::class.java)
            startActivity(i)
            finish()
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

    fun fragment_inint() {
        fragmentManager = supportFragmentManager
        myInfoFragment = MyInfoFragment()

        //transaction = fragmentManager.beginTransaction()
        //transaction.replace(R.id.display_view, myInfoFragment).commitAllowingStateLoss()
    }
    fun button_init(){
        binding.myinfoBtn.setOnClickListener {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.display_view, myInfoFragment).commitAllowingStateLoss()
            Log.d(TAG,"click fragment")
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis()>backKeyPressedTime+2000){
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로 가기를 한 번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT).show()
        } else {
            AppFinish()
        }
    }
    fun AppFinish(){
        finish()
        System.exit(0)

    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
}