package com.example.aries.mygame


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.aries.mygame.databinding.FragmentSignupBinding
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private var progressingBool = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)

        binding.clearSignupAccountButton.setOnClickListener {
            if(!progressingBool){
                binding.nameSignupEditText.setText("")
                binding.accountSignupEditText.setText("")
                binding.passwordSignupEditText.setText("")
                binding.passwordSignupComfirmEditText.setText("")
            }
        }

        binding.signupAccountButton.setOnClickListener { view: View ->
            if(!progressingBool)signupProcess(view)
        }

        return binding.root
    }

    private fun requestTest(){
        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val emptyBody : RequestBody = RequestBody.create(jSON,"wwww")

        val request = Request.Builder().url("http://192.168.52.130:8000/api/test").post(emptyBody).build()

        Thread(Runnable {
            (activity as MainActivity).runOnUiThread {progressStart()}
            val output: String
            val response = client.newCall(request).execute()
            output = response.body()!!.string()

            println(output)
            Thread.sleep(2000)

            (activity as MainActivity).runOnUiThread {progressDone()}

        }).start()
    }

    private fun progressStart(){
        (activity as MainActivity).progressingBool=true
        progressingBool=true
        binding.signupProgressBar.visibility = View.VISIBLE
    }

    private fun progressDone(){
        (activity as MainActivity).progressingBool=false
        progressingBool=false
        binding.signupProgressBar.visibility = View.INVISIBLE
    }

    private fun signupProcess(view: View){
        if (binding.nameSignupEditText.text.toString()=="") {
            Toast.makeText((activity as MainActivity), "Name should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.accountSignupEditText.text.toString()=="") {
            Toast.makeText((activity as MainActivity), "E-mail should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.passwordSignupEditText.text.toString()=="") {
            Toast.makeText((activity as MainActivity), "Password should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.passwordSignupComfirmEditText.text.toString()=="") {
            Toast.makeText((activity as MainActivity), "Password confirmation should not be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val myBody = SignupData(binding.nameSignupEditText.text.toString(),
            binding.accountSignupEditText.text.toString(),
            binding.passwordSignupEditText.text.toString(),
            binding.passwordSignupComfirmEditText.text.toString()
            )

        val gson = Gson()
        val myBodyJson = gson.toJson(myBody)

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val jSON = MediaType.get("application/json")
        val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

        val request = Request.Builder().url(resources.getString(R.string.URL) +"/api/register")
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
                        signUpSuccess(view)
                    }
                }else{
                    (activity as MainActivity).runOnUiThread {
                        progressDone()
                        Toast.makeText((activity as MainActivity), "Sigh Up Failed! \n The email has already been taken.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    private fun signUpSuccess(view: View){
        Toast.makeText((activity as MainActivity), "Sigh Up Success!", Toast.LENGTH_SHORT).show()
        Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_memberFragment)
    }

}
