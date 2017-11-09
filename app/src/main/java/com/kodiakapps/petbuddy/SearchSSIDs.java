package com.kodiakapps.petbuddy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

/**
 * Created by usman on 5/29/17.
 */

public class SearchSSIDs extends Activity {
    private static final String TAG = "PetBuddy";
    private static final String DEVICE_SERIAL = "serialno";
    private String deviceName = "";
    private String serialno;

    WifiManager wifi;
    TextView textStatus;
    TextView connectNetworkTV;
    List<ScanResult> results;
    ImageView iw;
    Handler mHandler;
    boolean exitActivityFirstRun = false;
    int exitActivityRuns = 0;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ssid);
        mHandler = new Handler();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                serialno = null;
            } else {
                serialno = extras.getString(DEVICE_SERIAL);
            }
        } else {
            serialno = (String) savedInstanceState.getSerializable(DEVICE_SERIAL);
        }

        deviceName = "PB" + serialno;

        Log.d(TAG, "deviceName is: " + deviceName);
        Log.d(TAG, "PSK is: " + serialno);

        textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setVisibility(View.VISIBLE);
        connectNetworkTV = (TextView) findViewById(R.id.connectNetworkTV);
        connectNetworkTV.setVisibility(View.VISIBLE);
        iw = (ImageView) findViewById(R.id.status_indicator_light);
        iw.setImageResource(R.drawable.redlight);

        Log.d(TAG, "buttons and textstatuses done");

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                serialno = null;
            } else {
                serialno = extras.getString(DEVICE_SERIAL);
            }
        } else {
            serialno = (String) savedInstanceState.getSerializable(DEVICE_SERIAL);
        }

        deviceName = "PB" + serialno;

        Log.d(TAG, "deviceName is: " + deviceName);
        Log.d(TAG, "PSK is: " + serialno);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        Log.d(TAG, "Starting WiFi scan");
        wifi.startScan();

        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
        textStatus.setText("Scanning");


        final Runnable exitActivityTask = new Runnable() {
            public void run() {
                if(exitActivityRuns != 10) {
                    exitActivityRuns++;
                    blinkConnectedText();
                    mHandler.postDelayed(this, 150);
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                    finish();
                }
            }
        };

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                Log.d(TAG, "onReceive()");
                Toast.makeText(getApplicationContext(), "got Scan Results", Toast.LENGTH_LONG).show();
                textStatus.setText("Receiving");
                results = wifi.getScanResults();
                if (results.toString().contains(deviceName)) {
                    Toast.makeText(getApplicationContext(), "PetBuddy Found!", Toast.LENGTH_LONG).show();

                    WifiConfiguration myWiFiConfig = new WifiConfiguration();
                    myWiFiConfig.preSharedKey = "\"" + serialno + "\"";
                    myWiFiConfig.SSID = "\"" + deviceName + "\"";
                    myWiFiConfig.status = WifiConfiguration.Status.ENABLED;
                    myWiFiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    myWiFiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    myWiFiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    myWiFiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    myWiFiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    myWiFiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    int res = wifi.addNetwork(myWiFiConfig);
                    Log.d(TAG, "addNetwork returned " + res);
                    boolean networkEnabled = wifi.enableNetwork(res, true);
                    Log.d(TAG, "enableNetwork returned " + networkEnabled);
                    if(networkEnabled) {
                        textStatus.setText("Connected!");
                        iw.setImageResource(R.drawable.greenlight);

                        if(!exitActivityFirstRun) {
                            exitActivityFirstRun = true;
                            mHandler.postDelayed(exitActivityTask, 250);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find your PetBuddy :(", Toast.LENGTH_LONG).show();
                }

            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void blinkConnectedText() {
        if (textStatus.getVisibility() == View.VISIBLE)
            textStatus.setVisibility(View.INVISIBLE);
        else
            textStatus.setVisibility(View.VISIBLE);
    }
}
