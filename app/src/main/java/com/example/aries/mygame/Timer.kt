package com.example.aries.mygame

import android.os.Handler

interface TimesUp{
    fun setShowTime()
    fun gameGG()
}

class Timer(private var timesUp: TimesUp){

    // The number of seconds counted since the timer started
    var secondsCount = 0
    var threshHold = 30

    /**
     * [Handler] is a class meant to process a queue of messages (known as [android.os.Message]s)
     * or actions (known as [Runnable]s)
     */
    private var handler = Handler()
    private lateinit var runnable: Runnable



    fun startTimer() {
        // Create the runnable action, which prints out a log and increments the seconds counter
        runnable = Runnable {
            timesUp.setShowTime()
            //println(secondsCount)
            secondsCount++
            // postDelayed re-adds the action to the queue of actions the Handler is cycling
            // through. The delayMillis param tells the handler to run the runnable in
            // 1 second (1000ms)
            if(secondsCount>threshHold){
                timesUp.gameGG()
            }else{handler.postDelayed(runnable, 1000)}
        }

        // This is what initially starts the timer
        handler.postDelayed(runnable, 1000)

        // Note that the Thread the handler runs on is determined by a class called Looper.
        // In this case, no looper is defined, and it defaults to the main or UI thread.
    }

    fun stopTimer() {
        // Removes all pending posts of runnable from the handler's queue, effectively stopping the
        // timer
        handler.removeCallbacks(runnable)
    }
}