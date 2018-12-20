package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.aries.mygame.databinding.FragmentMemberBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class MemberFragment : Fragment() {
    private var progressingBool = false
    private lateinit var binding:FragmentMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member, container, false)

        binding.loginButton.setOnClickListener {view: View ->
            if ((activity as MainActivity).apiToken!="" && !progressingBool){
                autoLogin(view)
            }else{
                Navigation.findNavController(view).navigate(R.id.action_memberFragment_to_loginFragment)
            }
        }

        binding.signupButton.setOnClickListener { view: View ->
            if(!progressingBool) Navigation.findNavController(view).navigate(R.id.action_memberFragment_to_signupFragment)
        }

        return binding.root
    }

    private fun autoLogin(view: View){
        val myBody = AutoLoginData((activity as MainActivity).apiToken)

        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

//        val request = Request.Builder().url("http://192.168.52.130:8000/api/autologin")
        val request = Request.Builder().url(resources.getString(R.string.URL) + "api/autologin")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(requestBody)
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
                        autoLogInSuccess(view,readJSON)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Auto Log In Failed!\nPlease Log In Again!", Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).apiToken = ""
                        Navigation.findNavController(view).navigate(R.id.action_memberFragment_to_loginFragment)
                    }
                }
            }
        })
    }

    private fun progressStart(){
        (activity as MainActivity).progressingBool = true
        progressingBool=true
        binding.autoLoginProgressBar.visibility = View.VISIBLE
        binding.memberTitleImageView.alpha = 0.5f
    }

    private fun progressDone(){
        (activity as MainActivity).progressingBool = false
        progressingBool=false
        binding.autoLoginProgressBar.visibility = View.INVISIBLE
        binding.memberTitleImageView.alpha = 0f
    }

    private fun autoLogInSuccess(view: View, readJSON : JSONObject){
        val dataJsonArray = JSONObject(readJSON.getString("data"))
        // update point
        //(activity as MainActivity).remainMoney = dataJsonArray.getString("balance").toInt()

        if (dataJsonArray.getString("balance") != "null"){
            (activity as MainActivity).remainMoney = dataJsonArray.getString("balance").toInt()
        }else{
            (activity as MainActivity).remainMoney = 0
        }

        (activity as MainActivity).userName = dataJsonArray.getString("name")
        (activity as MainActivity).apiToken = dataJsonArray.getString("api_token")

        Navigation.findNavController(view).navigate(R.id.action_memberFragment_to_gameTitle)
    }
}
