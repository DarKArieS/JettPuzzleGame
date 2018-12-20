package com.example.aries.mygame

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.aries.mygame.databinding.FragmentGameTitleBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class GameTitle : Fragment() {
    private var progressingBool = false
    private lateinit var binding: FragmentGameTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_title, container, false)

        // refresh chosen items before enter the game
        (activity as MainActivity).itemChooseArray = BooleanArray(4){false}

        // refresh item amount
        updateItemAmount()

        binding.startButton.setOnClickListener { view: View ->
            if(!progressingBool) {
                if ((activity as MainActivity).remainMoney<10){
                    Toast.makeText((activity as MainActivity), "No Money!", Toast.LENGTH_SHORT).show()
                }else{startGame(view)}
            }
        }
        binding.accountButton.setOnClickListener {view: View ->
            if(!progressingBool) getHistoryProcess(view)
        }

        binding.achieveButton.setOnClickListener {view: View ->
            if(!progressingBool) getAchievementProcess(view)
        }

        binding.logoutButton.setOnClickListener {view: View ->
            if(!progressingBool) logoutProcess(view)
        }

        binding.shopButton.setOnClickListener { view:View->
            if(!progressingBool) Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_shopFragment)
        }

        binding.expEntryTextView.setOnClickListener { view:View->
            if(!progressingBool) Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_explainFragment) }

        binding.expEntryTextView2.setOnClickListener { view:View->
            if(!progressingBool) Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_explainAchieveFragment) }

        binding.itemImageView1.setOnClickListener {
            chooseItem(1)
        }

        binding.itemImageView2.setOnClickListener {
            chooseItem(2)
        }

        binding.itemImageView3.setOnClickListener {
            chooseItem(3)
        }

        binding.itemImageView4.setOnClickListener {
            chooseItem(4)
        }

        binding.moneyTextView.text = "剩下" + ((activity as MainActivity).remainMoney).toString() +  "元"
        binding.showUserNameTextView.text = (activity as MainActivity).userName

        return binding.root
    }

    private fun progressStart(){
        (activity as MainActivity).progressingBool=true
        progressingBool=true
        binding.logoutProgressBar.visibility = View.VISIBLE
    }

    private fun progressDone(){
        (activity as MainActivity).progressingBool=false
        progressingBool=false
        binding.logoutProgressBar.visibility = View.INVISIBLE
    }

    private fun logoutProcess(view: View){
        val myBody = AutoLoginData((activity as MainActivity).apiToken)

        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/logout")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .delete(requestBody)
            .build()

        progressStart()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                    progressDone()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                if(readJSON.getString("result") == "success") {
                    (activity as MainActivity).runOnUiThread{
                        progressDone()
                        logoutSuccess(view)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Log Out Failed! wtf !?", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun logoutSuccess(view: View){
        (activity as MainActivity).apiToken = ""
        Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_loginFragment)
    }

    private fun startGame(view: View){
        val myBody = PlayData((activity as MainActivity).apiToken,"2")

        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        println(myBodyJson)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/play")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(requestBody)
            .build()

        //ToDo add a delete request for the used items, need to do it sequentially!

        progressStart()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                    progressDone()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                if(readJSON.getString("result") == "success") {
                    (activity as MainActivity).runOnUiThread{
                        progressDone()
                        startGameSuccess(view, readJSON)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Log In Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun startGameSuccess(view: View, readJSON : JSONObject){
        val dataJsonArray = JSONObject(readJSON.getString("data"))
        (activity as MainActivity).remainMoney = dataJsonArray.getString("balance").toInt()
        // use item
        for (i in 0 .. 3){
            if((activity as MainActivity).itemChooseArray[i]){
                (activity as MainActivity).itemNumArray[i]--
            }
        }

        Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_gameFragment)
    }

    private fun getAchievementProcess(view: View){
        val client: OkHttpClient = OkHttpClient().newBuilder().build()

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/achievement")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization","Bearer " + (activity as MainActivity).apiToken)
            .get()
            .build()

        progressStart()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                    progressDone()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                if(readJSON.getString("result") == "success") {
                    (activity as MainActivity).runOnUiThread{
                        progressDone()
                        getAchievementSuccess(view, readJSON)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Log In Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getAchievementSuccess(view: View, readJSON : JSONObject){
        val dataJsonArray = JSONArray(readJSON.getString("data"))
        println(dataJsonArray)
        println(dataJsonArray.length())

        for (i in 0 .. (dataJsonArray.length()-1)){
            println(dataJsonArray[i])
            val dataJsonObject =  JSONObject(dataJsonArray[i].toString())
            val achievedItem = dataJsonObject.getString("achieve_id").toInt()
            when(achievedItem){
                1 -> (activity as MainActivity).achievementRecord[0] = 1
                2 -> (activity as MainActivity).achievementRecord[1] = 1
                3 -> (activity as MainActivity).achievementRecord[2] = 1
                4 -> (activity as MainActivity).achievementRecord[3] = 1
                5 -> (activity as MainActivity).achievementRecord[4] = 1
                6 -> (activity as MainActivity).achievementRecord[5] = 1
                7 -> (activity as MainActivity).achievementRecord[6] = 1
                8 -> (activity as MainActivity).achievementRecord[7] = 1
                9 -> (activity as MainActivity).achievementRecord[8] = 1
                10 -> (activity as MainActivity).achievementRecord[9] = 1
                11 -> (activity as MainActivity).achievementRecord[10] = 1
                12 -> (activity as MainActivity).achievementRecord[11] = 1
                13 -> (activity as MainActivity).achievementRecord[12] = 1
            }
        }

         Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_achievementFragment)
    }

    private fun getHistoryProcess(view: View){
        val client: OkHttpClient = OkHttpClient().newBuilder().build()

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/detail")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization","Bearer " + (activity as MainActivity).apiToken)
            .get()
            .build()

        progressStart()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                    progressDone()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                if(readJSON.getString("result") == "success") {
                    (activity as MainActivity).runOnUiThread{
                        progressDone()
                        getHistorySuccess(view, readJSON)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Log In Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getHistorySuccess(view: View, readJSON : JSONObject){
        val dataJsonArray = JSONArray(readJSON.getString("data"))
        (activity as MainActivity).gameHistoryDataList = mutableListOf()
        for (i in 0 .. (dataJsonArray.length()-1)){
            val dataJsonObject =  JSONObject(dataJsonArray[i].toString())

            val gameID = dataJsonObject.getString("game_id")
            val balance = dataJsonObject.getString("amount")
            val date = dataJsonObject.getString("updated_at")

            val gameHistoryData = GameHistoryData(gameID,date,balance)
            (activity as MainActivity).gameHistoryDataList.add(gameHistoryData)
        }
        (activity as MainActivity).gameHistoryDataList//.reverse()

        Navigation.findNavController(view).navigate(R.id.action_gameTitle_to_accountFragment)
    }

    private fun chooseItem(chosenItemID:Int){
        if((activity as MainActivity).itemNumArray[chosenItemID-1]>0){
            if (!(activity as MainActivity).itemChooseArray[chosenItemID-1]){
                (activity as MainActivity).itemChooseArray[chosenItemID-1] = true
                when(chosenItemID){
                    1-> binding.itemImageView1.alpha = 1f
                    2-> binding.itemImageView2.alpha = 1f
                    3-> binding.itemImageView3.alpha = 1f
                    4-> binding.itemImageView4.alpha = 1f
                }
            }else{
                (activity as MainActivity).itemChooseArray[chosenItemID-1] = false
                when(chosenItemID){
                    1-> binding.itemImageView1.alpha = 0.3f
                    2-> binding.itemImageView2.alpha = 0.3f
                    3-> binding.itemImageView3.alpha = 0.3f
                    4-> binding.itemImageView4.alpha = 0.3f
                }
            }
        }
    }

    private fun updateItemAmount(){

        val client: OkHttpClient = OkHttpClient().newBuilder().build()

        val request = Request.Builder().url( resources.getString(R.string.URL) +"api/shop/2")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization","Bearer " + (activity as MainActivity).apiToken)
            //.addHeader("game_id","2")
            .get()
            .build()

        progressStart()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                    progressDone()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result") == "success") {
                    (activity as MainActivity).runOnUiThread{
                        progressDone()
                        //println(readJSON.getString("data"))
                        //getHistorySuccess(view, readJSON)
                        val dataJsonArray = JSONArray(readJSON.getString("data"))
                        var itemNumArray = IntArray(4){0}
                        for (i in 0 .. (dataJsonArray.length()-1)){
                            val dataJsonObject =  JSONObject(dataJsonArray[i].toString())
                            val item = dataJsonObject.getString("item_id").toInt()
                            itemNumArray[item]++
                        }

                        for (i in 0..3){
                            (activity as MainActivity).itemNumArray[i] = itemNumArray[i]
                        }
                        binding.itemNumTextView1.text = (activity as MainActivity).itemNumArray[0].toString()
                        binding.itemNumTextView2.text = (activity as MainActivity).itemNumArray[1].toString()
                        binding.itemNumTextView3.text = (activity as MainActivity).itemNumArray[2].toString()
                        binding.itemNumTextView4.text = (activity as MainActivity).itemNumArray[3].toString()
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Log In Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


}
