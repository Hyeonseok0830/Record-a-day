package com.example.record_a_day.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.record_a_day.R
import com.example.record_a_day.presenter.Contractor
import com.example.record_a_day.presenter.GoalPresenter

class GoalFragment : Fragment(), Contractor.GoalView {

    private var presenter: GoalPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = GoalPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.goal_fragment, container, false);

    }
}