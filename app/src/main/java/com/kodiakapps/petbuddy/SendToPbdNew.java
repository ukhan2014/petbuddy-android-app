package com.kodiakapps.petbuddy;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SendToPbdNew extends AppCompatActivity {

    private String TAG = "PBD:SendInfoToPbd";
    private TextView mTextViewReplyFromServer;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_info_to_pbd);


        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);

        mTextViewReplyFromServer = (TextView) findViewById(R.id.textView2);
        mTextViewReplyFromServer.setVisibility(View.INVISIBLE);

        String msgType = (String) getIntent().getExtras().get("type");
        String info = "";
        String msg = "";
        if(msgType.equals("reginfo")) {
            info = (String) getIntent().getExtras().get("reginfo");
            msg = msgType.concat("=" + info);
            Log.d(TAG, "received regInfo: " + info);
        }
        else if(msgType.equals("wifiinfo")) {
            info = (String) getIntent().getExtras().get("wifiinfo");
            msg = info;
            Log.d(TAG, "received wifi info: " + info);
        }

        Log.d(TAG, "msg=" +msg);

        new ServerCommunicator(getApplicationContext()).execute(msg);
    }
}
