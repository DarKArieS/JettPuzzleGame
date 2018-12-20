package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aries.mygame.databinding.FragmentAchievementBinding


class AchievementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentAchievementBinding>(inflater, R.layout.fragment_achievement, container, false)

        for (i in 0..12){
            if((activity as MainActivity).achievementRecord[i]==1){
                when(i){
                    0->binding.achieveCheckBox11.isChecked = true
                    1->binding.achieveCheckBox12.isChecked = true
                    2->binding.achieveCheckBox13.isChecked = true
                    3->binding.achieveCheckBox14.isChecked = true
                    4->binding.achieveCheckBox15.isChecked = true
                    5->binding.achieveCheckBox16.isChecked = true
                    6->binding.achieveCheckBox17.isChecked = true
                    7->binding.achieveCheckBox18.isChecked = true
                    8->binding.achieveCheckBox19.isChecked = true
                    9->binding.achieveCheckBox21.isChecked = true
                    10->binding.achieveCheckBox22.isChecked = true
                    11->binding.achieveCheckBox23.isChecked = true
                    12->binding.achieveCheckBox24.isChecked = true
                }
            }
        }
        return binding.root
    }
}
