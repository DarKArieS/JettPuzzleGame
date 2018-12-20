package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aries.mygame.databinding.FragmentAccountBinding


class AccountFragment : Fragment() {

    private lateinit var binding : FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)

        var moneyHistory = (activity as MainActivity).dateArray
        var listStrings: String = ""
        for (i in moneyHistory.reversed()){
            listStrings += (i + "\n")
        }

        //binding.accountTextView.text = listStrings

        setupAdapter()

        return binding.root
    }

//    private var threeRecord = mutableListOf<GameHistoryData>(
//        GameHistoryData("Puzzle", "20121212", "32"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Light On", "20121212", "30"),
//        GameHistoryData("Keep Sharking", "20121212", "25")
//    )

    private fun setupAdapter() {
        binding.recyclerView.adapter = GameHistoryListAdapter(this.context!!, (activity as MainActivity).gameHistoryDataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }

}
