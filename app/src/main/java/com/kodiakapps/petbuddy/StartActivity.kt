package com.kodiakapps.petbuddy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.kodiakapps.petbuddy.barcode.BarcodeCaptureActivity


class StartActivity : AppCompatActivity() {

    private val TAG = "PetBuddy:StartActivity"
    private var count = 0
    private var startMillis: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        var startbtn = findViewById(R.id.start_btn) as Button
        startbtn.setOnClickListener {
            val intent = Intent(this, BarcodeCaptureActivity::class.java)
            //intent.putExtra("key", value)
            startActivity(intent)
            finish()
        }
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

            if (count == 7) {
                //start debug mode
                Log.d(TAG, "Enter debug mode!")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            return true
        }
        return false
    }
}
