package com.kodiakapps.petbuddy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SendInfoToPbd : AppCompatActivity() {
    private var TAG = "PBD:SendInfoToPbd"

    private lateinit var mTextViewReplyFromServer:TextView
    private lateinit var spinner:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_info_to_pbd)

        spinner = findViewById(R.id.progressBar2) as ProgressBar
        spinner.visibility = View.VISIBLE

        mTextViewReplyFromServer = findViewById(R.id.textView2) as TextView
        mTextViewReplyFromServer.visibility = View.INVISIBLE

        val regInfo = intent.extras!!.get("reginfo") as String
        Log.d(TAG, "regInfo is: " + regInfo)

        sendMessage(regInfo)
    }

    private fun sendMessage(msg:String) {
        val handler = Handler()
        val thread = Thread(object:Runnable {
            override fun run() = try
            {
                Log.d(TAG, "create socket")
                val s = Socket("192.168.1.1", 8234)
                Log.d(TAG, "socket created")
                val out = s.getOutputStream()
                val output = PrintWriter(out)

                output.println(msg)
                output.flush()
                Log.d(TAG, "flush message")

                val input = BufferedReader(InputStreamReader(s.getInputStream()))
                val st = input.readLine()

                Log.d(TAG, "try to get response")
                handler.post(object:Runnable {
                    override fun run() {
                        if (st.trim({ it <= ' ' }).length != 0) {
                            spinner.visibility = View.INVISIBLE
                            mTextViewReplyFromServer.setText("From Server : " + st)
                            mTextViewReplyFromServer.visibility = View.VISIBLE
                        }
                    }
                })

                Log.d(TAG, "Close up shop")
                output.close()
                out.close()
                s.close()

            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
}
