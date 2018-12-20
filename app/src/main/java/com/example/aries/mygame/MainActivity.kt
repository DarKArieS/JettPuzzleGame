package com.example.aries.mygame

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.findNavController
import com.google.gson.Gson
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var savedWallet: SharedPreferences
    private val data = "DATA"

    private val moneyField = "MONEY"
    private val userNameField = "NAME"
    private val apiTokenField = "API_TOKEN"
    var remainMoney : Int = -1
    var userName : String = ""
    var apiToken : String = ""

    // consuming record
    private val gameTypeHistoryField = "GAMETYPE_HISTORY"
    private val dateHistoryField = "DATE_HISTORY"
    private val moneyHistoryField = "MONEY_HISTORY"
    var gameTypeArray : MutableList<String> = mutableListOf()
    var dateArray :MutableList<String> = mutableListOf()
    var moneyArray : MutableList<String> = mutableListOf()

    // achievements record
    private val scoreHistoryField = "SCORE_HISTORY"
    var scoreArray : MutableList<String> = mutableListOf()
    var currentScore : Int = 0

    //activity member
    val achievementRecord = IntArray(13){0}
    var gameHistoryDataList = mutableListOf<GameHistoryData>()
    var itemNumArray = IntArray(4){0}
//    var itemNumArray = intArrayOf(2, 2, 2, 2)
    var itemChooseArray = BooleanArray(4){false}
    var progressingBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedWallet = this.getSharedPreferences(data, Context.MODE_PRIVATE)
        println("login token")
        println(savedWallet.getString(apiTokenField, ""))
        if(savedWallet.getString(apiTokenField, "")!=""){
            remainMoney = savedWallet.getString(moneyField, "0")!!.toInt()
            userName = savedWallet.getString(userNameField, "")!!
            apiToken = savedWallet.getString(apiTokenField, "")!!

            val jsonArray = JSONArray(savedWallet.getString(dateHistoryField, ""))
            for (i in 0 until jsonArray.length()) {
                dateArray.add(jsonArray.getString(i))
            }
        }


//        if(savedWallet.getString(moneyField, "")==""){
//            savedWallet.edit()
//                .putString(moneyField, (500).toString())
//                .apply()
//            remainMoney = 500
//        }else{
//            remainMoney = savedWallet.getString(moneyField, "")!!.toInt()
//
//            val jsonArray = JSONArray(savedWallet.getString(dateHistoryField, ""))
//
//            for (i in 0 until jsonArray.length()) {
//                dateArray.add(jsonArray.getString(i))
//            }
//        }

//        var gson = Gson()
//
//        dateArray = mutableListOf("abc","bcd")
//        var dataArrayJson = gson.toJson(dateArray)
//
//        var jsonArray = JSONArray(dataArrayJson)
//        jsonArray.length()
//        var dateArray2 :MutableList<String> = mutableListOf()
//
//        for (i in 0 until jsonArray.length()) {
//            dateArray2.add(jsonArray.getString(i))
//        }

    }

    override fun onBackPressed() {
        //saveShared()
        val navController = this.findNavController(R.id.mainNavHostFragment)
        if(!progressingBool) if(!navController.navigateUp()) super.onBackPressed()
    }

//    override fun onDestroy() {
//        saveShared()
//        super.onDestroy()
//    }

    override fun onStop() {
        saveShared()
        super.onStop()
    }

//    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
//        saveShared()
//        super.onSaveInstanceState(outState, outPersistentState)
//    }

    fun saveShared(){
        println("save SharedPreferences!")
        println(apiToken)
        val gson = Gson()
        val dataArrayJson = gson.toJson(dateArray)

        savedWallet.edit()
            .putString(moneyField, remainMoney.toString())
            .putString(userNameField, userName)
            .putString(apiTokenField, apiToken)
            .putString(dateHistoryField, dataArrayJson.toString())
            .apply()
    }

    //===================================================================================
    //點一下EditTextView外面收起鍵盤

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN){

            val v : View? = this.currentFocus

            if (this.isShouldHideInput(v, ev)) {
                hideSoftInput(v!!.windowToken)
            }

        }

        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(v : View?, event: MotionEvent): Boolean{
        if (v!= null && (v is EditText) ){
            val l : IntArray  = intArrayOf( 0, 0 )
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()

            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
    //===================================================================================
}
