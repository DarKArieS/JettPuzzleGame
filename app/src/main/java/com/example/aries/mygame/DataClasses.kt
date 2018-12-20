package com.example.aries.mygame

data class LoginData (val email: String, val password: String)

data class SignupData (val name : String, val email: String, val password: String, val password_confirmation: String)

data class AutoLoginData( val api_token: String)

data class GameHistoryData(val game_type: String, val date: String, val balance: String)

data class PlayData (val api_token: String, val game_id: String)

data class SendAchieveData (val api_token: String, val achieve_id: String)

data class BuyItemData (val game_id: String, val item_id: String, val cost: String, val api_token: String)

