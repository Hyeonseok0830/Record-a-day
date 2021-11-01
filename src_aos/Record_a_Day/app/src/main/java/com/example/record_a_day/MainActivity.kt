package com.example.record_a_day

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.record_a_day.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mBinding:ActivityMainBinding? = null

    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }
    fun init(){

    }
    override fun onDestroy() {

        mBinding = null
        super.onDestroy()
    }
}