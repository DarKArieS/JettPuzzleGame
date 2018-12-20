package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.aries.mygame.databinding.FragmentLoginBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private var progressingBool = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.loginAccountButton.setOnClickListener{view: View ->
            if(!progressingBool)loginProcess(view)
        }

        binding.clearAccountButton.setOnClickListener {
            if(!progressingBool){
                binding.accountEditText.setText("")
                binding.passwordEditText.setText("")
            }
        }

        return binding.root
    }

    private fun loginProcess(view: View){
        val myBody = LoginData(binding.accountEditText.text.toString(),binding.passwordEditText.text.toString())

        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        //println(myBodyJson)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

        val request = Request.Builder().url(resources.getString(R.string.URL) +"api/login")
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
                        loginSuccess(view, readJSON)
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

    private fun progressStart(){
        (activity as MainActivity).progressingBool=true
        progressingBool=true
        binding.loginProgressBar.visibility = View.VISIBLE
    }

    private fun progressDone(){
        (activity as MainActivity).progressingBool=false
        progressingBool=false
        binding.loginProgressBar.visibility = View.INVISIBLE
    }

    private fun loginSuccess(view: View, readJSON : JSONObject){

        //println(readJSON.getString("data"))
        val dataJsonArray = JSONObject(readJSON.getString("data"))
        //println(dataJsonArray.getString("name"))
        //println(dataJsonArray.getString("balance"))
        //println(dataJsonArray.getString("api_token"))

        //println(dataJsonArray.getString("balance").toInt())

        // update point
        if (dataJsonArray.getString("balance") != "null"){
            (activity as MainActivity).remainMoney = dataJsonArray.getString("balance").toInt()
        }else{
            (activity as MainActivity).remainMoney = 0
        }

        (activity as MainActivity).userName = dataJsonArray.getString("name")
        (activity as MainActivity).apiToken = dataJsonArray.getString("api_token")

        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_gameTitle)
    }


}
