package com.example.aries.mygame

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class GameHistoryListAdapter (val context: Context, val gameHistList: List<GameHistoryData>):
    RecyclerView.Adapter<GameHistoryListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0?.context).inflate(R.layout.gamehistory_list_item, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.gameHistList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0?.bind(gameHistList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val date = itemView?.findViewById<TextView>(R.id.gameHistoryDateTextView)
        private val gameType = itemView?.findViewById<ImageView>(R.id.gameHistoryIconImageView)
        private val balance = itemView?.findViewById<TextView>(R.id.gameHistoryBalanceTextView)

        fun bind(gameHistoryData: GameHistoryData) {
            date?.text = gameHistoryData.date
            balance?.text = gameHistoryData.balance

            when(gameHistoryData.game_type){
                "2"->{
                    gameType.setImageResource(R.drawable.sheep_puzzle)
                    when(gameHistoryData.description){
                        "購買道具神奇鬧鐘"->gameType.setImageResource(R.drawable.clock)
                        "購買道具扣分盾牌"->gameType.setImageResource(R.drawable.shield)
                        "購買道具加分神器"->gameType.setImageResource(R.drawable.relic)
                        "購買道具識破眼鏡"->gameType.setImageResource(R.drawable.glasses)
                    }}
                "3"->gameType.setImageResource(R.drawable.sheep_lighton)
                "4"->gameType.setImageResource(R.drawable.sheep_left)
                "5"->gameType.setImageResource(R.drawable.save_money)
            }
        }

    }

}