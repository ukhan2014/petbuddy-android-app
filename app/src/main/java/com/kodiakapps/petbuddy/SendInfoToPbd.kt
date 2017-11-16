package com.kodiakapps.petbuddy

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

    private val mTextViewReplyFromServer: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_info_to_pbd)

        var spinner = findViewById(R.id.progressBar2) as ProgressBar
        spinner.visibility = View.VISIBLE

        val regInfo = intent.extras!!.get("reginfo") as String

        Log.d(TAG, "regInfo is: " + regInfo)
    }

    private fun sendMessage(msg:String) {
        val handler = Handler()
        val thread = Thread(object:Runnable {
            public override fun run() {
                try
                {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    val s = Socket("192.168.1.2", 8234)
                    val out = s.getOutputStream()
                    val output = PrintWriter(out)
                    output.println(msg)
                    output.flush()
                    val input = BufferedReader(InputStreamReader(s.getInputStream()))
                    val st = input.readLine()
                    handler.post(object:Runnable {
                        public override fun run() {
                            val s = (if (mTextViewReplyFromServer != null)
                                mTextViewReplyFromServer.getText() else null).toString()
                            if (st.trim({ it <= ' ' }).length != 0)
                                if (mTextViewReplyFromServer != null) {
                                    mTextViewReplyFromServer.setText(s + "\nFrom Server : " + st)
                                }
                        }
                    })
                    output.close()
                    out.close()
                    s.close()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
        thread.start()
    }
}
