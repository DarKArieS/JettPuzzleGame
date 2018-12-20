package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.aries.mygame.databinding.FragmentGameOverBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class GameOverFragment : Fragment() {
    private lateinit var binding: FragmentGameOverBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_over, container, false)

        val scoreString = "Score: " + (activity as MainActivity).currentScore.toString()
        binding.gameResultTextView.text = scoreString

        binding.gameReusltBackButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_gameOverFragment_to_gameTitle)
        }

        sendAchieveProcess()

        return binding.root
    }

    private fun sendAchieveProcess(){
        lateinit var myBody : SendAchieveData
        if((activity as MainActivity).currentScore>=20){
            myBody = SendAchieveData((activity as MainActivity).apiToken, "13")
        }else if((activity as MainActivity).currentScore>=10){
            myBody = SendAchieveData((activity as MainActivity).apiToken, "12")
        }else if((activity as MainActivity).currentScore>=6){
            myBody = SendAchieveData((activity as MainActivity).apiToken, "11")
        }else if((activity as MainActivity).currentScore>=3){
            myBody = SendAchieveData((activity as MainActivity).apiToken, "10")
        }else {return}


        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/achievement")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                if(readJSON.getString("result") == "success") {
//                    (activity as MainActivity).runOnUiThread{ }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        Toast.makeText((activity as MainActivity), "Log In Failed!\nUpdate Achievement Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

}
