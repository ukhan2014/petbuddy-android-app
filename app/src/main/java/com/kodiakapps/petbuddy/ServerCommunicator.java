package com.kodiakapps.petbuddy;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by usman on 11/18/17.
 */

public class ServerCommunicator extends AsyncTask<String,Void,String> {
    public static String SERVER_IP = "192.168.1.1";
    public static int SERVER_PORT = 8234;
    public Context context;

    public ServerCommunicator(Context c) {
        this.context = c;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            System.out.println(SERVER_IP);
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            System.out.println("Created serverAddr "+ SERVER_IP);
            Socket socket = new Socket(SERVER_IP,SERVER_PORT);
            System.out.println("Socket created..");
            //sends the message to the server
            PrintWriter mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            String msg2Send = "{\"HelloWorld\", \"1234\"}";
            mBufferOut.println(msg2Send);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String result = in.readLine();
            System.out.println("result: " + result);
            socket.close();


            return result;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }
}