package com.example.record_a_day

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.record_a_day.databinding.ActivityMainBinding
import com.example.record_a_day.manager.PreferenceManager
import com.example.record_a_day.view.activity.LoginActivity
import com.example.record_a_day.view.fragment.GoalFragment
import com.example.record_a_day.view.fragment.MyInfoFragment
import com.example.record_a_day.view.fragment.RecordFragment
import com.example.record_a_day.view.fragment.TaskFragment
import com.orhanobut.logger.Logger
import io.reactivex.observers.DisposableObserver

class MainActivity : AppCompatActivity() {
    /*
    * ViewBinding 관련
    * */
    private var mBinding:ActivityMainBinding? = null

    private val binding get() = mBinding!!

    private var show = false

    private val TAG = "seok"
    //SharedPref 키 값
    private val AUTO_LOGIN_KEY = "auto_login"

    private var backKeyPressedTime = 0L
    /*
    * fragment 관련
    * */
    lateinit var fragmentManager : FragmentManager
    lateinit var myInfoFragment : MyInfoFragment
    lateinit var taskFragment : TaskFragment
    lateinit var recordFragment: RecordFragment
    lateinit var goalFragment: GoalFragment
    lateinit var transaction : FragmentTransaction

    /*
    * animation 관련
    * */
    lateinit var myInfo_anim : Animation
    lateinit var record_anim : Animation
    lateinit var taskList_anim : Animation
    lateinit var goal_anim : Animation
    lateinit var share_anim : Animation

    /*
    * Handler 변수
    * */
    lateinit var mHandler: Handler
    
    companion object{
        const val SHOW_BTN = 0
        const val HIDE_BTN = 1
        const val BTN_HIDE_DELAY = 3500L
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        fragmentInit()
        buttonInit()

//        val source = Observable.create<String> {
//            it.onNext("test")
//            it.onComplete()
//        }
//        source.subscribe(mObserver)
    }

    override fun onStart() {
        Logger.i("onStart")
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
        super.onStop()
    }


    //----------------------------------------------------------------------------------------------
    // Initialize Method
    //----------------------------------------------------------------------------------------------
    fun init(){

        myInfo_anim = AnimationUtils.loadAnimation(this,R.anim.myinfo_anim).apply{
            fillAfter = true
        }
        record_anim = AnimationUtils.loadAnimation(this,R.anim.record_anim).apply {
            fillAfter = true
        }
        taskList_anim = AnimationUtils.loadAnimation(this,R.anim.tasklist_anim).apply {
            fillAfter = true
        }
        goal_anim = AnimationUtils.loadAnimation(this,R.anim.goal_anim).apply {
            fillAfter = true
        }
        share_anim = AnimationUtils.loadAnimation(this,R.anim.share_anim).apply {
            fillAfter = true
        }

       /* mHandler = Handler{
            when(it.what){
                SHOW_BTN-> {
                    showMoreBtn()
                }
                HIDE_BTN -> {
                    Log.d(TAG, "init: hide")
                    hideMoreBtn()
                }
            }
            true
        }*/

    }
    //메뉴 버튼  네비게이션뷰로 변경 (2022/07/16 by seok)
    /*fun showMoreBtn(){
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
    }
    fun hideMoreBtn(){
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


    }*/

    fun fragmentInit() {
        fragmentManager = supportFragmentManager
        myInfoFragment = MyInfoFragment()
        recordFragment = RecordFragment()
        taskFragment = TaskFragment()
        goalFragment = GoalFragment()

        transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.display_view, myInfoFragment).commitAllowingStateLoss()
    }
    fun buttonInit(){

        binding.bottomNavigationview.run {
            setOnItemSelectedListener { item ->
                when(item.itemId){
                    R.id.menu_myinfo ->{
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.display_view, myInfoFragment).commitAllowingStateLoss()
                    }
                    R.id.menu_record ->{
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.display_view, recordFragment).commitAllowingStateLoss()
                    }
                    R.id.menu_tasklist ->{
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.display_view, taskFragment).commitAllowingStateLoss()
                    }
                    R.id.menu_goal ->{
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.display_view, goalFragment).commitAllowingStateLoss()
                    }
                    R.id.menu_share ->{
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.display_view, goalFragment).commitAllowingStateLoss()
                    }

                }
                true
            }
            selectedItemId = R.id.menu_myinfo
        }

        //하단 메뉴 버튼 - 메뉴 펼치기
     /*   binding.moreBtn.setOnClickListener {
            mHandler.removeMessages(1)
            mHandler.sendEmptyMessage(0)
            mHandler.sendEmptyMessageDelayed(1,BTN_HIDE_DELAY)
 *//*           CoroutineScope(Main).launch {
                delay(3000L)
                hideMoreBtn()
            }*//*
        }*/
        binding.logout.setOnClickListener {
            PreferenceManager.setString(this,AUTO_LOGIN_KEY,"")
            var i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
        //메뉴 네비게이션 뷰로 변경 (2022/07/16 by seok)
       /* //각 메뉴 버튼 - 내 정보 버튼
        binding.myinfoBtn.setOnClickListener {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.display_view, myInfoFragment).commitAllowingStateLoss()
            mHandler.removeMessages(1)
            mHandler.sendEmptyMessageDelayed(1,BTN_HIDE_DELAY)
        }
        //각 메뉴 버튼 - 일기장 버튼
        binding.recordBtn.setOnClickListener {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.display_view, recordFragment).commitAllowingStateLoss()
            mHandler.removeMessages(1)
            mHandler.sendEmptyMessageDelayed(1,BTN_HIDE_DELAY)
        }
        //각 메뉴 버튼 - 일기장 버튼
        binding.taskListBtn.setOnClickListener {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.display_view, taskFragment).commitAllowingStateLoss()
            mHandler.removeMessages(1)
            mHandler.sendEmptyMessageDelayed(1,BTN_HIDE_DELAY)
        }
        //각 메뉴 버튼 - 일기장 버튼
        binding.goalBtn.setOnClickListener {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.display_view, goalFragment).commitAllowingStateLoss()
            mHandler.removeMessages(1)
            mHandler.sendEmptyMessageDelayed(1,BTN_HIDE_DELAY)
        }*/
    }



    override fun onBackPressed() {
        if(System.currentTimeMillis()>backKeyPressedTime+2000){
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로 가기를 한 번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT).show()
        } else {
            AppFinish()
        }
    }
    private fun AppFinish(){
        finish()
        System.exit(0)

    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }

    var mObserver = object : DisposableObserver<String>(){
        override fun onNext(str: String) {
            Log.i(TAG, "onNext: $str")
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
            Log.i(TAG, "onComplete: ")
        }

    }

}