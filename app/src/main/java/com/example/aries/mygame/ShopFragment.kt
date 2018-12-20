package com.example.aries.mygame


import android.databinding.DataBindingUtil
import com.example.aries.mygame.databinding.FragmentShopBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShopBinding
    private val price = intArrayOf(1, 1, 1, 1)
    private var nWantedItem = IntArray(4){0}
    private var progressingBool = false
//    private var completedRequestJson :MutableList<JSONObject> = mutableListOf()
    private var completedRequestCallback :MutableList<Int> = mutableListOf()
    private var numTargetRequest = 0

    private var totalCost:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false)

        binding.itemNumEditText1.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.itemNumEditText1.editableText.toString()!=""){
                    nWantedItem[0] = binding.itemNumEditText1.editableText.toString().toInt()
                    showTotalCost()
                }else showIDNCost()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.itemNumEditText2.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.itemNumEditText2.editableText.toString()!=""){
                    nWantedItem[1] = binding.itemNumEditText2.editableText.toString().toInt()
                    showTotalCost()
                }else showIDNCost()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.itemNumEditText3.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.itemNumEditText3.editableText.toString()!=""){
                    nWantedItem[2] = binding.itemNumEditText3.editableText.toString().toInt()
                    showTotalCost()
                }else showIDNCost()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.itemNumEditText4.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(binding.itemNumEditText4.editableText.toString()!=""){
                    nWantedItem[3] = binding.itemNumEditText4.editableText.toString().toInt()
                    showTotalCost()
                }else showIDNCost()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val showString =  ((activity as MainActivity).remainMoney).toString()
        binding.balanceTextView.text = showString

        binding.transactionButton.setOnClickListener { view : View ->
            buyProcess(view)

        }

        return binding.root
    }

    private fun showTotalCost(){
        totalCost = nWantedItem[0]*price[0] + nWantedItem[1]*price[1] + nWantedItem[2]*price[2]+ nWantedItem[3]*price[3]
        val showString = "-" + totalCost.toString()
        binding.spendTextView.text = showString
    }

    private fun showIDNCost(){
        binding.spendTextView.text = "-？"
    }

    private fun progressStart(){
        (activity as MainActivity).progressingBool=true
        progressingBool=true
        binding.shopProgressBar.visibility = View.VISIBLE
    }

    private fun progressDone(){
        (activity as MainActivity).progressingBool=false
        progressingBool=false
        binding.shopProgressBar.visibility = View.INVISIBLE
    }

    private fun buyProcess(view: View){
        completedRequestCallback = mutableListOf()
        //numTargetRequest = 2
        numTargetRequest = 0
        for(i in 0..3){
            numTargetRequest+=nWantedItem[i]
        }

        if (numTargetRequest==0) return

        //本地記錄處理
        //(activity as MainActivity).itemNumArray[0] +=2
        for(i in 0..3){
            (activity as MainActivity).itemNumArray[i]+=nWantedItem[i]
        }

        //伺服器更新
        progressStart()
        for(i in 0..3){
            for (n in 0..(nWantedItem[i]-1)){
                if(nWantedItem[i]==0) break
                val myBody = BuyItemData("2", i.toString(), price[i].toString(), (activity as MainActivity).apiToken)
                val gson = Gson()
                val myBodyJson = gson.toJson(myBody)
                println(myBodyJson)

                val client: OkHttpClient = OkHttpClient().newBuilder().build()
                val jSON = MediaType.get("application/json")
                val requestBody : RequestBody = RequestBody.create(jSON,myBodyJson)

                val request = Request.Builder().url(resources.getString(R.string.URL) +"api/shop")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(requestBody)
                    .build()

                val call = client.newCall(request)
                call.enqueue(object: Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        (activity as MainActivity).runOnUiThread {
                            Toast.makeText((activity as MainActivity), "Connecting failed!", Toast.LENGTH_SHORT).show()
                            //val emptyJSON = JSONObject("{\"result\": \"fail\"}")
                            checkProgressDone(view, Int.MAX_VALUE)
                        }
                    }
                    override fun onResponse(call: Call?, response: Response?) {
                        val readJSON = JSONObject(response!!.body()!!.string())
                        if(readJSON.getString("result") == "success") {
                            (activity as MainActivity).runOnUiThread{
                                println(readJSON)
                                //val dataJsonArray = JSONObject(readJSON.getString("data"))
                                //val getBalance = dataJsonArray.getString("balance").toInt()
                                checkProgressDone(view, Int.MAX_VALUE)
                            }
                        }else{
                            (activity as MainActivity).runOnUiThread {
                                Toast.makeText((activity as MainActivity), "Buy Failed!", Toast.LENGTH_SHORT).show()
                                //val emptyJSON = JSONObject("{\"result\": \"fail\"}")
                                checkProgressDone(view, Int.MAX_VALUE)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun checkProgressDone(view: View, completeBalance :Int){
        completedRequestCallback.add(completeBalance)
        println("buy done!")
        if(completedRequestCallback.size == numTargetRequest){
            // complete!

            // update balance
            (activity as MainActivity).remainMoney -= totalCost

            progressDone()
            Navigation.findNavController(view).navigate(R.id.action_shopFragment_to_gameTitle)
        }
    }



}
