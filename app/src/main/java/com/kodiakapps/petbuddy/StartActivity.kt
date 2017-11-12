package com.kodiakapps.petbuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent



class StartActivity : AppCompatActivity() {

    private val TAG = "PetBuddy:StartActivity"
    private var count = 0
    private var startMillis: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)
    }

    //detect any touch event in the screen (instead of an specific view)
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val eventaction = event.action
        if (eventaction == MotionEvent.ACTION_UP) {

            //get system current milliseconds
            val time = System.currentTimeMillis()


            //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
            if (startMillis == 0L || time - startMillis > 3000L) {
                startMillis = time
                count = 1
            } else { //  time-startMillis< 3000
                count++
            }//it is not the first, and it has been  less than 3 seconds since the first

            if (count === 5) {
                //do whatever you need
                Log.d(TAG, "NICE!")
            }
            return true
        }
        return false
    }
}
